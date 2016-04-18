package com.ichangmao.app.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.ichangmao.commons.MaoLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TcpServer {
    private final MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    protected static final int SOCKET_READ_TIMEOUT = 60 * 1000;
    /**
     * 心跳间隔时间，单位ms
     */
    protected static final int HEARTBEAT_INTERVAL = 10 * 1000;
    private int port = 0;
    private boolean isRunning = false;
    /**
     * 前3位是#CM的AscII码，第4位为保留位
     */
    protected static final byte[] cmHead = {0x23, 0x43, 0x4d, 0x00};
    /**
     * head里面的后4位存放消息正文的长度
     */
    private static final int msgHeadLen = 4;
    private static final int cmHeadLen = cmHead.length;

    protected static final byte heartbeat[] = {0x23, 0x43, 0x4d, 0x00, 0x00, 0x00, 0x00, 0x00};

    private ServerSocket myServerSocket = null;
    private Thread myThread;
    /**
     * 处理请求的线程池
     */
    protected final ExecutorService mClientHandlerPool = Executors.newCachedThreadPool(new ClientHandlerThreadFactory());


    private final HashMap<String, WorkRunnable> mClients = new HashMap<>();

    public TcpServer(int port) {
        this.port = port;
    }

    public boolean start() {
        log.i("start");
        if (isRunning) {
            log.i("isRunning true");
            return true;
        }
        try {
            myServerSocket = new ServerSocket();
            myServerSocket.setReuseAddress(true);
            myServerSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        isRunning = true;
        ServerRunnable serverRunnable = new ServerRunnable(SOCKET_READ_TIMEOUT);
        myThread = new Thread(serverRunnable);
        myThread.setDaemon(true);
        myThread.setName("Bee Server");
        myThread.start();

        return true;
    }

    public boolean stop() {
        log.i("stop");
        if (myThread == null || myServerSocket == null || !isRunning) {
            return true;
        }
        dicconnect();
        isRunning = false;
        try {
            myServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myThread.join();
            myThread = null;
            myServerSocket = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 监听客户端的连接
     */
    private class ServerRunnable implements Runnable {

        private final int timeout;

        private ServerRunnable(int timeout) {
            this.timeout = timeout;
        }

        @Override
        public void run() {
            do {
                try {
                    final Socket finalAccept = TcpServer.this.myServerSocket.accept();
                    if (this.timeout > 0) {
                        finalAccept.setSoTimeout(this.timeout);
                    }
                    TcpServer.this.mClientHandlerPool.execute(new WorkRunnable(finalAccept));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.i("Communication with the client broken");
                }
            } while (!TcpServer.this.myServerSocket.isClosed());
        }
    }

    /**
     * 处理客户端发送的消息
     */
    private class WorkRunnable implements Runnable {

        private final Socket mClientSocket;
        private final String ip;
        private boolean isDisconnected = false;
        DataInputStream mDis = null;
        DataOutputStream mDos = null;

        private WorkRunnable(Socket clientSocket) {
            this.mClientSocket = clientSocket;
            this.ip = clientSocket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try {
                mDis = new DataInputStream(mClientSocket.getInputStream());
                mDos = new DataOutputStream(mClientSocket.getOutputStream());

                boolean isClientAdd = false;
                do {
                    byte[] head = new byte[8];
                    int len = 0;
                    while (len < head.length) {
                        int read = mDis.read(head, len, head.length - len);
                        if (read == -1) {
                            log.i("server read head -1, len:" + len + " is closed:" + mClientSocket.isClosed());
                            return;
                        }
                        len += read;
                    }
                    int msgLength = parseMsgLength(head);
                    if (msgLength == 0) {
                        log.d("heartbeat");
                        continue;
                    }
                    if (msgLength < 0 || msgLength > 5 * 1024 * 1024) {
                        log.i("parse head error:" + msgLength + " head:" + getHexString(head));
                        break;
                    }

                    byte[] data = new byte[msgLength];
                    len = 0;
                    while (len < data.length) {
                        int read = mDis.read(data, len, msgLength - len);
                        if (read == -1) {
                            log.i("server read data -1");
                            return;
                        }
                        len += read;
                    }
                    String msg = new String(data, "UTF-8");
                    log.d("server receive:" + msg);

                    if (!isClientAdd) {
                        log.i("add client:" + ip);
                        if (mClients.containsKey(ip)) {
                            log.i("disconnect old client");
                            mClients.get(ip).disconnect();
                        }
                        synchronized (mClients) {
                            mClients.put(ip, this);
                            isClientAdd = true;
                        }

                        sendHeartbeat();
                    }

                } while (!mClientSocket.isClosed());

            } catch (IOException e) {
                e.printStackTrace();
                log.i("handle client exception:" + ip);
            } finally {
                if (ip != null) {
                    log.i("a client exit:" + ip + " isDisconnected:" + isDisconnected);
                    if (!isDisconnected) {
                        //通过disconnect断开连接时不需要回调
                        isDisconnected = true;
                    }
                }
                stopHeartbeat();
                if (ip != null) {
                    synchronized (mClients) {
                        mClients.remove(ip);
                    }
                }
                if (mDis != null) {
                    try {
                        mDis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mDos != null) {
                    try {
                        mDos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!mClientSocket.isClosed()) {
                    try {
                        mClientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getName() {
            return ip;
        }

        public boolean send(String msg) {
            log.d("server send:" + msg);
            if (mDos == null || msg == null) {
                return false;
            }
            try {
                byte data[] = msg.getBytes("UTF-8");
                mDos.write(createHead(data.length));
                mDos.write(data);
                mDos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void disconnect() {
            isDisconnected = true;
            if (!mClientSocket.isClosed()) {
                try {
                    mClientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendHeartbeat() {
            mHandler.sendEmptyMessageDelayed(1, TcpServer.HEARTBEAT_INTERVAL);
        }

        private void stopHeartbeat() {
            mHandler.removeMessages(1);
        }

        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (!mClientSocket.isClosed() && mDos != null) {
                        try {
                            mDos.write(TcpServer.heartbeat);
                            mDos.flush();

                            sendHeartbeat();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private class ClientHandlerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread t = new Thread(runnable);
            if (runnable instanceof WorkRunnable) {
                String name = ((WorkRunnable) runnable).getName();
                t.setName("Mao Client Handler #" + name);
            } else {
                t.setName("Mao Client Handler");
            }
            t.setDaemon(true);
            return t;
        }
    }

    /**
     * 解析头，获取消息正文的长度
     *
     * @param head 消息头 8个字节
     * @return 消息正文的长度或错误码
     */
    protected static int parseMsgLength(byte[] head) {
        if (head == null || head.length != cmHeadLen + msgHeadLen) {
            return -1;
        }
        for (int i = 0; i < cmHead.length; i++) {
            if (cmHead[i] != head[i]) {
                //不是cm头
                return -2;
            }
        }
        int len = 0;
        for (int i = cmHeadLen; i < head.length; i++) {
            int x = head[i] & 0xff;
            len += x << ((i - cmHeadLen) * 8);
        }
        return len;
    }

    /**
     * 根据消息正文的长度创建一个消息头
     *
     * @param msgLength 消息正文的长度
     * @return 消息头 8个字节
     */
    protected static byte[] createHead(int msgLength) {
        byte[] head = new byte[cmHeadLen + msgHeadLen];
        for (int i = 0; i < cmHeadLen; i++) {
            head[i] = cmHead[i];
        }
        for (int i = 0; i < msgHeadLen; i++) {
            head[cmHeadLen + i] = (byte) (msgLength >> (i * 8) & 0xff);
        }
        return head;
    }

    protected static String getHexString(byte[] data) {
        StringBuilder hexSb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            hexSb.append("0x");
            hexSb.append(Integer.toHexString(b));
            if (i < data.length - 1) {
                hexSb.append(" ");
            }
        }
        return hexSb.toString();
    }

    public synchronized boolean send(String ip, String msg) {
        WorkRunnable client = mClients.get(ip);
        if (client == null) {
            log.i("server send error: no client");
            return false;
        }
        log.i("server send:" + msg);
        return client.send(msg);
    }

    /**
     * 断开指定客户端
     */
    public synchronized boolean disconnect(String ip) {
        log.i("disconnect peer:" + ip);
        WorkRunnable client = mClients.get(ip);
        if (client == null) {
            log.i("server disconnect error: no client");
            return false;
        }
        client.disconnect();
        return true;
    }

    /**
     * 断开所有已连接的客户端，但Server本身并不停止
     */
    public synchronized void dicconnect() {
        log.i("disconnect");
        List<WorkRunnable> clients;
        synchronized (mClients) {
            clients = new ArrayList<>(mClients.values());
        }
        for (WorkRunnable client : clients) {
            client.disconnect();
        }
    }
}
