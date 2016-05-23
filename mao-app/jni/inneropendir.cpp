/*
 * Copyright (C) 2008 The Android Open Source Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
#include "inneropendir.h"
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/syscall.h>

/*
#ifndef NDEBUG
#include <android/log.h>
#define APPNAME "INNERDIR"
#endif
*/

INNERDIR*  inneropendir( const char*  dirpath )
{
/*
#ifndef NDEBUG
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Open %s", dirpath);
#endif
*/
    INNERDIR*  dir = (INNERDIR*)malloc(sizeof(INNERDIR));

    if (!dir)
        goto Exit;

    dir->_DIR_fd = open(dirpath, O_RDONLY|O_DIRECTORY);
    if (dir->_DIR_fd < 0)
    {
        free(dir);
        dir = NULL;
    }
    else
    {
        dir->_DIR_avail = 0;
        dir->_DIR_next  = NULL;
        pthread_mutex_init( &dir->_DIR_lock, NULL );
    }
Exit:
    return dir;
}

static struct dirent* _readdir_unlocked(INNERDIR*  dir)
{
    struct dirent*  entry;
/*
#ifndef NDEBUG
    unsigned reclen;
#endif
*/
    if ( !dir->_DIR_avail )
    {
        int  rc;

        for (;;) {
	    rc = syscall(__NR_getdents64, dir->_DIR_fd, dir->_DIR_buff, sizeof(dir->_DIR_buff));
            //rc = getdents( dir->_DIR_fd, dir->_DIR_buff, sizeof(dir->_DIR_buff));
            if (rc >= 0 || errno != EINTR)
            break;
        }
        if (rc <= 0)
            return NULL;
/*
#ifndef NDEBUG
	__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Enter getdents %d, %d", rc, sizeof(dir->_DIR_buff));
#endif
*/
        dir->_DIR_avail = rc;
        dir->_DIR_next  = dir->_DIR_buff;
    }

    entry = dir->_DIR_next;

    /* perform some sanity checks here */
    if (((long)(void*)entry & 3) != 0)
        return NULL;
/*
#ifndef NDEBUG
    // paranoid testing of the interface with the kernel getdents64 system call
    reclen = offsetof(struct dirent, d_name) + strlen(entry->d_name) + 1;
    if ( reclen > sizeof(*entry) || reclen <= offsetof(struct dirent, d_name) )
        goto Bad;

    if ( (char*)entry + reclen > (char*)dir->_DIR_buff + sizeof(dir->_DIR_buff) )
        goto Bad;

    if ( !memchr( entry->d_name, 0, reclen - offsetof(struct dirent, d_name)) )
        goto Bad; 
#endif
*/
    dir->_DIR_next   = (struct dirent*)((char*)entry + entry->d_reclen);
    dir->_DIR_avail -= entry->d_reclen;
/*
#ifndef NDEBUG
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "read entry %d", dir->_DIR_avail);
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "read entry %s", entry->d_name);
#endif
*/
    return entry;

  Bad:
    errno = EINVAL;
    return NULL;
}


struct dirent* innerreaddir(INNERDIR * dir)
{
    struct dirent *entry = NULL;
/*
#ifndef NDEBUG
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "read");
#endif
*/
    pthread_mutex_lock( &dir->_DIR_lock );
    entry = _readdir_unlocked(dir);
    pthread_mutex_unlock( &dir->_DIR_lock );

    return entry;
}

int innerclosedir(INNERDIR *dir)
{
  int rc;

  rc = close(dir->_DIR_fd);
  dir->_DIR_fd = -1;

  pthread_mutex_destroy( &dir->_DIR_lock );

  free(dir);
  return rc;
}

