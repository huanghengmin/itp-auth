package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.utils.StaticField;
import org.apache.log4j.Logger;
import org.apache.mina.filter.ssl.SslContextFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class RZPortListenService implements Runnable{
    private static Logger logger = Logger.getLogger(RZPortListenService.class);

    private boolean isRunning = false;
    private static int port ;    //认证服务将要监听的端口号
//    static SSLServerSocket server;
    static ServerSocket server;

    public static void setPort(int port) {
        RZPortListenService.port = port;
    }

    public RZPortListenService() {
    }

    /*
    *@param port 监听的端口号
    *@return 返回一个SSLServerSocket对象
    */

    private static void getServerSocket(int thePort)
    {
        try
        {
//            System.setProperty("javax.net.ssl.trustStore", StaticField.SERVER_KEY_STORE);
            SSLContext key = null;

            String fileType=null;
            if(StaticField.SERVER_KEY_STORE.indexOf(".") > -1){
                fileType=StaticField.SERVER_KEY_STORE.substring(StaticField.SERVER_KEY_STORE.lastIndexOf(".")+1) ;
            }

            SslContextFactory sslcontextFactory = new SslContextFactory();
            sslcontextFactory.setProtocol("TLS");
            KeyStore ks = null;
            if(fileType.equals("p12")||fileType.equals("pfx")){
                ks = KeyStore.getInstance("pkcs12");
                ks.load(new FileInputStream(StaticField.SERVER_KEY_STORE), StaticField.SERVER_KEY_STORE_PASSWORD.toCharArray());
            }
            if (fileType.equals("jks")||fileType.equals("keystore")){
                ks = KeyStore.getInstance("JKS");
                ks.load(new FileInputStream(StaticField.SERVER_KEY_STORE), StaticField.SERVER_KEY_STORE_PASSWORD.toCharArray());
            }

            KeyStore trustks = KeyStore.getInstance("JKS");
            trustks.load(new FileInputStream(StaticField.TRUST_KEY_STORE), StaticField.TRUST_KEY_PASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, StaticField.SERVER_KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory tmFact = TrustManagerFactory.getInstance("SunX509");
            tmFact.init(trustks);
            sslcontextFactory.setTrustManagerFactory(tmFact);
            sslcontextFactory.setTrustManagerFactoryKeyStore(trustks);         //可信证书
            sslcontextFactory.setKeyManagerFactoryKeyStore(ks);              //匹配证书
            sslcontextFactory.setKeyManagerFactoryKeyStorePassword(StaticField.SERVER_KEY_STORE_PASSWORD);
            key = sslcontextFactory.newInstance();

            ServerSocketFactory factory = key.getServerSocketFactory();
            server = factory.createServerSocket(port);
            ((SSLServerSocket) server).setNeedClientAuth(false);
            /*String key=System.getProperty("itpauth.home") + "/security/SSLKey";   //要使用的证书名

            char keyStorePass[]="123qwe".toCharArray();   //证书密码

            char keyPassword[]="123qwe".toCharArray();   //证书别称所使用的主要密码

            KeyStore ks=KeyStore.getInstance("JKS");   //创建JKS密钥库

            ks.load(new FileInputStream(key),keyStorePass);

            //创建管理JKS密钥库的X.509密钥管理器
            KeyManagerFactory kmf=KeyManagerFactory.getInstance("SunX509");

            kmf.init(ks,keyPassword);

            SSLContext sslContext=SSLContext.getInstance("SSLv3");

            sslContext.init(kmf.getKeyManagers(),null,null);

            //根据上面配置的SSL上下文来产生SSLServerSocketFactory,与通常的产生方法不同
            SSLServerSocketFactory factory=sslContext.getServerSocketFactory();

            s=(SSLServerSocket)factory.createServerSocket(thePort);*/

        }catch(Exception e)
        {
            System.out.println(e);
            logger.error("获取证书失败",e);
        }
    }

    public void init(){
        Properties properties = null;
        try{
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties != null && properties.containsKey("listenPort")){
                port =Integer.parseInt(properties.getProperty("listenPort"));
            }
            getServerSocket(port);
            logger.info("在"+port+"端口等待连接...");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("开启端口失败",e);
        }
    }

    @Override
    public void run(){
        isRunning = true;
        while (isRunning){
            try{
                SSLSocket socket=(SSLSocket)server.accept();

                //将得到的socket交给CreateThread对象处理,主线程继续监听
                new RZClient(socket);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }
}
