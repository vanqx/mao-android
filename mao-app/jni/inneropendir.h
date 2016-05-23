#ifndef __INNEROPENDIR_H_
#define __INNEROPENDIR_H_

#include <unistd.h>
#include <dirent.h>
#include <memory.h>
#include <string.h>
#include <fcntl.h>
#include <stdlib.h>
#include <pthread.h>
#include <errno.h>

struct INNERDIR
{
    int              _DIR_fd;
    size_t           _DIR_avail;
    struct dirent*   _DIR_next;
    pthread_mutex_t  _DIR_lock;
    struct dirent    _DIR_buff[255];
};

INNERDIR*  inneropendir( const char*  dirpath );
struct dirent* innerreaddir(INNERDIR * dir);
int innerclosedir(INNERDIR *dir);

#endif
