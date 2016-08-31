package com.ichangmao.app.ui;

import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ichangmao.app.R;
import com.ichangmao.commons.BackgroundThread;
import com.ichangmao.commons.IOUtils;
import com.ichangmao.commons.MainThread;
import com.ichangmao.commons.MaoLog;
import com.ijinshan.rt.common.IRootKeeper;
import com.ijinshan.rt.common.RootServiceConst;

import eu.chainfire.libsuperuser.Shell;

public class RootFragment extends Fragment implements View.OnClickListener {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private static final String ROOT_SERVICE_NAME = "com.ichangmao.app_rtsrv";
    private IRootKeeper mRootKeeper = null;

    String path = "/many_pic";

    TextView txt_content;
    Button btn_checkroot;
    Button btn_root;
    Button btn_compute_with_fuse;
    Button btn_compute_without_fuse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_root, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        btn_checkroot = (Button) rootView.findViewById(R.id.btn_checkroot);
        btn_checkroot.setOnClickListener(this);
        btn_root = (Button) rootView.findViewById(R.id.btn_root);
        btn_root.setOnClickListener(this);
        btn_compute_with_fuse = (Button) rootView.findViewById(R.id.btn_compute_with_fuse);
        btn_compute_with_fuse.setOnClickListener(this);
        btn_compute_without_fuse = (Button) rootView.findViewById(R.id.btn_compute_without_fuse);
        btn_compute_without_fuse.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkroot:
                txt_content.setText("check root:" + checkRoot() + "\n");
                break;
            case R.id.btn_root:
                enterRoot();
                break;
            case R.id.btn_compute_with_fuse:
                compute_with_fuse();
                break;
            case R.id.btn_compute_without_fuse:
                compute_without_fuse();
                break;
        }
    }

    public void enterRoot() {
        log.i("enterRoot");
        if (checkRoot()) {
            return;
        }
        btn_root.setEnabled(false);
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                boolean suAvailable = Shell.SU.available();
                if (suAvailable) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("export CLASSPATH=%s\n", "data/local/rootkeeper.jar"));
                    sb.append("/system/bin/app_process /system/bin com.ijinshan.rootkeeper.runMain ");
                    sb.append(android.os.Process.myUid() + " " + getContext().getPackageName() + " " + ROOT_SERVICE_NAME);
                    // 加&符号表示不阻塞
                    sb.append(" &\n");
                    String cmdString = sb.toString();
                    Shell.SU.run(new String[]{cmdString});
                }
                MainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_root.setEnabled(true);
                    }
                });
            }
        });
    }

    public void compute_with_fuse() {
        setButtonEnabled(false);
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getPath() + path;
                long start = SystemClock.uptimeMillis();
                long size = IOUtils.getFileSize(filePath);
                long end = SystemClock.uptimeMillis();
                final String fMsg = "with fuse time:" + (end - start) + " size:" + size;
                log.i(fMsg);
                MainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        setButtonEnabled(true);
                        txt_content.append(fMsg + "\n");
                    }
                });
            }
        });
    }

    public void compute_without_fuse() {
        if (!checkRoot()) {
            txt_content.append("check root:false\n");
            return;
        }
        setButtonEnabled(false);
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                String msg = "without fuse time:";
                try {
                    String filePath = "/data/media/0" + path;
                    long start = SystemClock.uptimeMillis();
                    long size = mRootKeeper.getPathFileSize(filePath);
                    long end = SystemClock.uptimeMillis();
                    msg += (end - start) + " size:" + size;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    msg += "RemoteException";
                }
                log.i(msg);
                final String fMsg = msg;
                MainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        setButtonEnabled(true);
                        txt_content.append(fMsg + "\n");
                    }
                });
            }
        });
    }

    private void setButtonEnabled(boolean enabled) {
        btn_compute_with_fuse.setEnabled(enabled);
        btn_compute_without_fuse.setEnabled(enabled);
    }

    public boolean checkRoot() {
        if (mRootKeeper != null && mRootKeeper.asBinder().pingBinder()) {
            return true;
        }

        try {
            IBinder binder = ServiceManager.getService(ROOT_SERVICE_NAME);
            if (binder == null) {
                return false;
            }

            mRootKeeper = IRootKeeper.Stub.asInterface(binder);
            if (mRootKeeper == null) {
                log.d("checkRoot-asInterface-false");
                return false;
            }

            if (mRootKeeper.asBinder().pingBinder()) {

                if (!mRootKeeper.UpdateUid()) {
                    log.d("checkRoot-UpdateUid-failed");
                    mRootKeeper = null;
                    return false;
                }

                if (mRootKeeper.GetVersion() < RootServiceConst.VERSION) {
                    log.d("checkRoot-GetVersion-error");
                    mRootKeeper.exit();
                    mRootKeeper = null;
                    return false;
                }
                return true;
            } else {
                log.d("checkRoot-pingBinder-failed-------------------------------");
                return false;
            }
        } catch (Exception e) {
            log.d("checkRoot-Exception" + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
