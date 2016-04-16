package com.hzih.itp.auth.client;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
public class SSLClient_hhm
{
    static int port=6666;
    static SSLSocket socket;
    private static String CLIENT_KEY_STORE = "D:/cert/fJuly1_666612345678945612.pfx";
    private static String CLIENT_trustStore = "D:/cert/fjuly.keystore";
    private static String CLIENT_KEY_STORE_PASSWORD = "123qwe";
    private static final String KEY_MANAGER_FACTORY_ALGORITHM;


    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }
        KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
    }

    public static void main(String args[])
    {
        try
        {


//            System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);
//            System.setProperty("javax.net.debug", "ssl,handshake");

            SSLClient_hhm client = new SSLClient_hhm();
            Socket s = client.clientWithCert();

            PrintWriter writer = new PrintWriter(s.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer.println("hello");
            writer.flush();
            System.out.println(reader.readLine());
            s.close();


        }catch(Exception e)
        {
            System.out.println(e);
        }


    }

    private Socket clientWithCert() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        KeyStore ks = getKeyStore(CLIENT_KEY_STORE);

        ks.load(new FileInputStream(CLIENT_KEY_STORE), null);
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
        context.init(kf.getKeyManagers(), null, null);

        SocketFactory factory = context.getSocketFactory();
        Socket s = factory.createSocket("localhost", port);
        return s;
    }

    private KeyStore getKeyStore(String filePath) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        String fileType = null;
        if(filePath.indexOf(".") > -1){
            fileType = filePath.substring(filePath.lastIndexOf(".") +1);
        }
        KeyStore ks = null;
        if(fileType.equals("p12")||fileType.equals("pfx")){
            ks = KeyStore.getInstance("pkcs12");
        }
        if (fileType.equals("jks")||fileType.equals("keystore")){
            ks = KeyStore.getInstance("JKS");
        }
        return ks;
    }
}
