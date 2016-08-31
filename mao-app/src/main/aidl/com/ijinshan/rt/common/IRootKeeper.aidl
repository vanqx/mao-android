package com.ijinshan.rt.common;

import android.os.ParcelFileDescriptor;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageMoveObserver;
import com.ijinshan.rt.common.FileInfo;
import com.ijinshan.rt.common.JunkFileInfo;
import com.ijinshan.rt.common.JunkFileInfoNew;
import com.ijinshan.rt.common.IDelCacheObserver;
import com.ijinshan.rt.common.AppDirData;


// ！！！！！！！！！！注意：接口只能顺序增加，不可插入！！！！！！！！！！！！！！！！！

interface IRootKeeper {
	int GetVersion();
	void exit();
	
	boolean exec(String strCmd);
	int execSh(String sh, out List<String> output);
	int execRc(String strCmd);
	boolean forceStopPackage(String strPkg);
	String	dumpNotification();


	// 卸载重装后更新UID
	boolean UpdateUid();
	
	boolean isClientSoExistInProc (String soFileName, int nPid);
	
	//从设备管理器中删除一个apk
	boolean DeleteFromDeviceAdmin(String strPkg);
	

	
	//记录/获取内存中的ksmotejar、so 版本号
	boolean SetMemoryModuleVersion(int ver);
	int     GetMemoryModuleVersion();
	void reboot(String reason);
	
	// 禁用程序的组件
	void	EnableComponent(in android.content.ComponentName comp, boolean bEnable);
	
	void deleteApplicationCacheFiles(in String packageName, IPackageDataObserver observer);
	
	//文件是否存在
	boolean isFileExist(in String filePath);
	
	//只是删除文件,没有线程
	boolean deleteFile(in String path);

	//删除文件，文件夹
	void deleteFiles(in String path, IDelCacheObserver observer);
	boolean execWithTimeOut(String strCmd, long nTime);
	
	boolean AyncExec(String strCmd);

	// 系统搬家
	void movePackage(String packageName, IPackageMoveObserver observer, int flags); 
	
	// 取文件大小
	long getFileSize(String pathFile);
	
	int GetPidByName(String procName);
	boolean CheckMarkInProcMaps(int nPid, String strMark);
	
	List<FileInfo> EnumFiles(String path);
	
	// 判断filePath是否为文件
	boolean isFile(in String filePath);
	
	// 取得ls -l /proc/PID/fd的信息
	String listProcFdInfo(int pid);
	
	//dump am 消息
	String dumpActivityManager(String param); 
	
	//搬移文件或文件夹
	boolean moveFileOrFolder(String srcPath, String dstPath, boolean isFile);
	
	//遍历指定目录垃圾文件(无用，低版本兼容)
	List<JunkFileInfo> enumJunkFiles(String path);
	
	//dump system large app信息
	AppDirData dumpSystemAppDirInfo(String libPath, String pkgs);
	
	int moveApkToSD(String packageName, String sdcardPath);
	
	//遍历指定目录垃圾文件
	JunkFileInfoNew enumJunkFilesNew(String path,String libPath);
	
	String convertRootCacheCleanCloudPath(String rootPath,String path,String pkgName);
	
	List<String> convertRootCacheCleanCloudPathREG(String rootPath,String path,String pkgName);
	
	// 获得该目录及以下所有文件大小或者该文件文件大小
	long getPathFileSize(String path);

	//Query 需root权限的database 
    String queryReadOnlyDataBase(String dbPath, String queryString, String queryJsonDataFormat);
    
    //删除文件，保留文件夹
	void deleteFilesLeftFoder(in String path, IDelCacheObserver observer);

	//write file content
    boolean writeFile(String fileAbsolutePath, String strContent);
}
