package com.hzih.itp.auth;

import org.apache.mina.filter.ssl.SslContextFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class SSLServer extends Thread {
    private Socket socket;

    public SSLServer(Socket socket) {
        this.socket = socket;
    }



    private static String TRUST_KEY_STORE = "D:\\cert\\TrustKey.keystore";

    private static String TRUST_KEY_PASSWORD = "123qwe";

    private static String SERVER_KEY_STORE = "D:\\cert\\vpnson_192.168.1.212.pfx";
    private static String SERVER_KEY_STORE_PASSWORD = "123qwe";

    public static void main(String[] args) throws Exception {
//        System.setProperty("javax.net.ssl.trustStore", SERVER_KEY_STORE);
        SSLContext key = null;

        int x=SERVER_KEY_STORE.lastIndexOf(".") ;
        String s=SERVER_KEY_STORE.substring(x+1,SERVER_KEY_STORE.length()) ;

        SslContextFactory sslcontextFactory = new SslContextFactory();
        sslcontextFactory.setProtocol("TLS");
        KeyStore ks = null;
        if(s.equals("p12")||s.equals("pfx")){
            ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(SERVER_KEY_STORE), SERVER_KEY_STORE_PASSWORD.toCharArray());
        }
        if (s.equals("jks")||s.equals("keystore")){
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(SERVER_KEY_STORE), SERVER_KEY_STORE_PASSWORD.toCharArray());
        }

        KeyStore jks = KeyStore.getInstance("JKS");
        jks.load(new FileInputStream(TRUST_KEY_STORE), TRUST_KEY_PASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
        TrustManagerFactory tmFact = TrustManagerFactory.getInstance("SunX509");
        tmFact.init(jks);
        sslcontextFactory.setTrustManagerFactory(tmFact);
        sslcontextFactory.setTrustManagerFactoryKeyStore(jks);         //可信证书
        sslcontextFactory.setKeyManagerFactoryKeyStore(ks);              //匹配证书
        sslcontextFactory.setKeyManagerFactoryKeyStorePassword(SERVER_KEY_STORE_PASSWORD);
        key = sslcontextFactory.newInstance();


        ServerSocketFactory factory = key.getServerSocketFactory();
        ServerSocket _socket = factory.createServerSocket(6666);
        ((SSLServerSocket) _socket).setNeedClientAuth(false);




        while (true) {
            new SSLServer(_socket.accept()).start();
        }
    }


    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            String data = reader.readLine();
            writer.println("你妈的反馈啊!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            writer.close();
            socket.close();
        } catch (IOException e) {

        }
    }
}
