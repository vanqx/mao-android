/**
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.content.IntentSender;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageMoveObserver;

/***
 *  See {@link PackageManager} for documentation on most of the APIs
 *  here.
 * 
 *  {@hide}
 */
interface IPackageManager {
    
    /***
     * As per {@link android.content.pm.PackageManager#setComponentEnabledSetting}.
     */
    void setComponentEnabledSetting(in ComponentName componentName,
            in int newState, in int flags);

    /***
     * As per {@link android.content.pm.PackageManager#getComponentEnabledSetting}.
     */
    int getComponentEnabledSetting(in ComponentName componentName);
    
    /***
     * As per {@link android.content.pm.PackageManager#setApplicationEnabledSetting}.
     */
    void setApplicationEnabledSetting(in String packageName, in int newState, int flags);
    
    /***
     * As per {@link android.content.pm.PackageManager#getApplicationEnabledSetting}.
     */
    int getApplicationEnabledSetting(in String packageName);
    
    /***
     * Set whether the given package should be considered stopped, making
     * it not visible to implicit intents that filter out stopped packages.
     */
    void setPackageStoppedState(String packageName, boolean stopped);
    
    String[] getPackagesForUid(int uid);
    
    void deleteApplicationCacheFiles(in String packageName, in IPackageDataObserver observer);
    void movePackage(String packageName, IPackageMoveObserver observer, int flags);
}

