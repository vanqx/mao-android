package com.ichangmao.http;

import android.support.annotation.NonNull;

import com.ichangmao.commons.MaoLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by yangchangmao on 2016/9/9.
 */
public class MaoServer {
    private MaoLog log = MaoLog.getLogger("MaoServer");

    private volatile boolean mIsRunning = false;

    CharsetEncoder mCharsetEncoder = Charset.forName("UTF-8").newEncoder();
    CharsetDecoder mCharsetDecoder = Charset.forName("UTF-8").newDecoder();

    //处理请求的线程池
    protected final ExecutorService mClientHandlerPool = Executors.newCachedThreadPool(new ClientHandlerThreadFactory());

    public synchronized void start(int port) {
        if (mIsRunning) {
            log.i("mIsRunning true");
            return;
        }
        mIsRunning = true;
        Thread serverThread = new Thread(new ServerRunnable(port));
        serverThread.setName("Mao Server");
        serverThread.start();
        log.i("server start...");
    }

    private class ServerRunnable implements Runnable {

        private final int mPort;

        private ServerRunnable(int port) {
            mPort = port;
        }

        @Override
        public void run() {

            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(mPort));
                serverSocketChannel.configureBlocking(false);
                Selector selector = Selector.open();
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                do {
                    selector.select();
                    Iterator ite = selector.selectedKeys().iterator();
                    while (ite.hasNext()) {
                        SelectionKey selectionKey = (SelectionKey) ite.next();
                        ite.remove();
                        log.d("readyOps:" + selectionKey.readyOps());

                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                            SocketChannel socketChannel = serverChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);

                        } else if (selectionKey.isReadable()) {
                            selectionKey.interestOps(SelectionKey.OP_WRITE);
                            mClientHandlerPool.execute(new WorkRunnable(selector, selectionKey));
                        }
                    }

                } while (true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class WorkRunnable implements Runnable {
        private String mIp;
        private int mPort;

        Selector mSelector;
        SocketChannel mSocketChannel;
        SelectionKey mSelectionKey;

        private WorkRunnable(Selector selector, SelectionKey selectionKey) {
            mSelector = selector;
            mSelectionKey = selectionKey;
            mSocketChannel = (SocketChannel) selectionKey.channel();
            mIp = mSocketChannel.socket().getInetAddress().getHostAddress();
            mPort = mSocketChannel.socket().getPort();
        }

        @Override
        public void run() {
            try {
                StringBuilder sb = new StringBuilder();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (true) {
                    int read = mSocketChannel.read(buffer);
                    if (read <= 0) {
                        log.i("read == " + read);
                        break;
                    }

                    buffer.flip();
                    sb.append(mCharsetDecoder.decode(buffer));
                    buffer.clear();
                }
                log.d(sb.toString());
                mSocketChannel.write(mCharsetEncoder.encode(CharBuffer.wrap("\r\n\r\nhello word!")));

                mSelectionKey.interestOps(SelectionKey.OP_READ);
                mSocketChannel.register(mSelector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return mIp + ":" + mPort;
        }
    }

    private class ClientHandlerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread t = new Thread(runnable);
            if (runnable instanceof WorkRunnable) {
                String name = ((WorkRunnable) runnable).getName();
                t.setName("MaoServer#" + name);
            } else {
                t.setName("MaoServer Client Handler");
            }
            t.setDaemon(true);
            return t;
        }
    }
}
