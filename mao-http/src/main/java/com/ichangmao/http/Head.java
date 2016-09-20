package com.ichangmao.http;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by yangchangmao on 2016/9/13.
 */
public class Head {
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HOST = "Host";
    public static final String DNT = "DNT";
    public static final String CONNECTION = "Connection";
    public static final String COOKIE = "Cookie";

    private BufferedReader br;
    private String method;
    private String requestURL;
    private String protocol;
    private String agent;
    private String host;
    private int port;
    private String encoding;
    private String language;
    private String accept;
    private String dnt;
    private String connection;
    private String cookie;

    public Head(BufferedReader br) {
        this.br = br;
    }

    public void parseHead() {

        String s = null;
        try {
            s = br.readLine();//第一行
            String[] firstLine = s.split(" ");
            if (firstLine.length == 3) {
                this.method = firstLine[0].trim();
                this.requestURL = firstLine[1].trim();
                this.protocol = firstLine[2].trim();
            }
            //继续读剩下的头信息
            s = br.readLine();
            while (s != null) {
                String[] split = s.split(":");

                switch (split[0].trim()) {
                    case ACCEPT: {
                        this.accept = split[1].trim();
                    }
                    case ACCEPT_LANGUAGE: {
                        this.language = split[1].trim();
                        break;
                    }
                    case USER_AGENT: {
                        this.agent = split[1].trim();
                        break;
                    }
                    case ACCEPT_ENCODING: {
                        this.encoding = split[1].trim();
                        break;
                    }
                    case HOST: {
                        this.host = split[1].trim();
                        break;
                    }
                    case DNT: {
                        this.dnt = split[1].trim();
                        break;
                    }
                    case CONNECTION: {
                        this.connection = split[1].trim();
                        break;
                    }
                    case COOKIE: {
                        this.cookie = split[1].trim();
                        break;
                    }
                }
                s = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
