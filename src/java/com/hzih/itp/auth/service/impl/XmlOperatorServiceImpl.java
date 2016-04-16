package com.hzih.itp.auth.service.impl;

import com.hzih.itp.auth.dao.XmlOperatorDAO;
import com.hzih.itp.auth.dao.impl.XmlOperatorDAOImpl;
import com.hzih.itp.auth.service.XmlOperatorService;
import com.inetec.common.config.stp.nodes.SourceFile;
import com.inetec.common.config.stp.nodes.Type;
import com.inetec.common.exception.Ex;
import com.inetec.common.util.OSInfo;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-15
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public class XmlOperatorServiceImpl implements XmlOperatorService {
    private static Logger logger = Logger.getLogger(XmlOperatorServiceImpl.class);
    private XmlOperatorDAO xmlOperatorDAO = new XmlOperatorDAOImpl();
    @Override
    public String getUrl(String serverUser, String clientUser) {
        String url = null;
        try {
            List<Type> externalTypes = xmlOperatorDAO.getTypes("external","file");
            for(Type type:externalTypes){
                SourceFile sourceFile = xmlOperatorDAO.getSourceFiles("external",type.getTypeName());
                if(serverUser.equals(sourceFile.getUserName())){
                    url =  type.getTypeName() + "/" + clientUser + "/" + serverUser ;// 应用名/认证账号/服务账号
                    String absoluteUrl = creatFile(url,sourceFile.getProtocol());    // 建立用户登录根目录
                    logger.info("建立用户登录根目录成功:" + absoluteUrl);
                    //设置权限目录为555权限
//                    logger.info("chmod 755 " + absoluteUrl.substring(0,absoluteUrl.lastIndexOf("/")));
                    //修改权限根目录
//                    modifyPer(serverUser, absoluteUrl);
                    logger.info("修改用户权限根目录成功");
//                    break;
                }
            }
            if(url == null){
                url = "该服务未应用!";
            }
        } catch (Ex ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return url;
    }

    @Override
    public String getUrl(String serverUser, String serverIpPort, String clientUser) {String url = null;
        try {
            List<Type> externalTypes = xmlOperatorDAO.getTypes("external","file");
            for(Type type:externalTypes){
                SourceFile sourceFile = xmlOperatorDAO.getSourceFiles("external",type.getTypeName());
                if(serverUser.equals(sourceFile.getUserName()) || serverIpPort.equals(sourceFile.getServerAddress() + ":" + sourceFile.getPort())){
                    url =  type.getTypeName() + "/" + clientUser + "/" + serverUser ;// 应用名/认证账号/服务账号
                    String absoluteUrl = creatFile(url,sourceFile.getProtocol());    // 建立用户登录根目录
                    logger.info("建立用户登录根目录成功:" + absoluteUrl);
                    //设置权限目录为555权限
//                    logger.info("chmod 755 " + absoluteUrl.substring(0,absoluteUrl.lastIndexOf("/")));
                    //修改权限根目录
//                    modifyPer(serverUser, absoluteUrl);
                    logger.info("修改用户权限根目录成功");
//                    break;
                }
            }
            if(url == null){
                url = "该服务未应用!";
            }
        } catch (Ex ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return url;
    }

    @Override
    public int getPort(String serverUser) {
        int port = -1;
        try {
            List<Type> externalTypes = xmlOperatorDAO.getTypes("external","file");
            for(Type type:externalTypes){
                SourceFile sourceFile = xmlOperatorDAO.getSourceFiles("external",type.getTypeName());
                if(serverUser.equals(sourceFile.getUserName())){
                    port = sourceFile.getPort();
                }
            }
        } catch (Ex ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return port;
    }

    @Override
    public String readTypeNameKeyValue(String plugin, String appType,String protocol) throws Ex {
        String[] appNames = null;
        if("reset".equals(appType)) {
            appNames = xmlOperatorDAO.getTypesByActive("external",true,protocol);
        } else {
            appNames = xmlOperatorDAO.readTypeNameSingle(plugin,appType);
        }
        int total = appNames.length;
        String json = "{success:true,total:"+total+",rows:[";
        for (int i = 0; i < total; i++) {
            json += "{key:'"+appNames[i]+"',value:'"+appNames[i]+"'},";
        }
        json += "]}";
        return json;
    }

    private boolean createFile(File file,String ftpUser) throws IOException {
        if(! file.exists()) {
            makeDir(file.getParentFile(),ftpUser);
        }
        return file.createNewFile();
    }

    private boolean makeDir(File dir,String ftpUser) throws IOException {
        boolean flag = true;
        if(dir.exists()){
            return flag;
        }
        logger.info("dir:" + dir.getAbsolutePath());
        if(! dir.getParentFile().exists()) {
            flag = makeDir(dir.getParentFile(),ftpUser);
            if(!flag){
                return flag;
            }
        }
        flag = dir.mkdirs();
        if(flag){
            Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir.getAbsolutePath());
//            Runtime.getRuntime().exec("chmod 557 " + dir.getAbsolutePath());
//            logger.info("chown " + ftpUser +":" + ftpUser + " " + dir.getAbsolutePath());
        }else{
            logger.error("创建文件夹失败:" + dir.getAbsolutePath()    );
        }
        return flag;
    }

    private void changePwd(String dir,String url,String ftpUser){
        String[] parents = url.split("/");
        String parent = dir;
        int i=0;
        for(i = 0;i< parents.length-1 ;i++){
            if(parents[i].length() == 0){
                continue;
            }
            dir = dir + "/" + parents[i];
            try {
                OSInfo osInfo = OSInfo.getOSInfo();
                if(osInfo.isLinux()){
                    Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir);
                    Runtime.getRuntime().exec("chmod 557 " + dir);
                }else{
                    //用于本地测试
                    System.out.println("chown " + ftpUser +":" + ftpUser + " " + dir);
                    System.out.println("chmod 557 " + dir);
                }
                logger.info("chmod 557 " + dir);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        if(parents[i].length() != 0){
            dir = dir + "/" + parents[i];
            try{
                OSInfo osInfo = OSInfo.getOSInfo();
                if(osInfo.isLinux()){
                    Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir);
                }else{
                    System.out.println("chown " + ftpUser +":" + ftpUser + " " + dir);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePwd(String dir,String url,String ftpUser,String permission){
        String[] parents = url.split("/");
        String parent = dir;
        int i=0;
        for(i = 0;i< parents.length-1 ;i++){
            if(parents[i].length() == 0){
                continue;
            }
            dir = dir + "/" + parents[i];
            try {
                OSInfo osInfo = OSInfo.getOSInfo();
                if(osInfo.isLinux()){
                    Runtime.getRuntime().exec("chown " + ftpUser + " " + dir);
//                    Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir);
                    Runtime.getRuntime().exec("chmod 557 " + dir);
                }else{
                    //用于本地测试
                    System.out.println("chown " + ftpUser +":" + ftpUser + " " + dir);
                    System.out.println("chmod 557 " + dir);
                }
                logger.info("chown " + ftpUser + " " + dir);
                logger.info("chmod 557 " + dir);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        if(parents[i].length() != 0){
            dir = dir + "/" + parents[i];
            try{
                OSInfo osInfo = OSInfo.getOSInfo();
                if(osInfo.isLinux()){
                    Runtime.getRuntime().exec("chown " + ftpUser + " " + dir);
//                    Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir);
                    //分配权限
                    String e = "chmod -R" ;
                    String per = "";
                    //r 读权限
                    if(permission.indexOf("1") > -1){
                        per = per + "+r";
                    }else{
                        per = per + "-r";
                    }
                    //写权限
                    if(permission.indexOf("2") > -1){
                        per = per + "+w";
                    }else {
                        per = per + "-w";
                    }
                    //x执行权限
                    if(permission.indexOf("3") > -1){
                        per = per + "+x";
                    }else{
                        per = per + "-x";
                    }
                    e = e + " u" + per + ",g" + per + ",o" + per;
                    e = e + " "+ dir;
                    logger.info(e);
                    Runtime.getRuntime().exec(e);
                }else{
                    System.out.println("chown " + ftpUser +":" + ftpUser + " " + dir);
                    //分配权限
                    String e = "chmod -R u" ;
                    //r 读权限
                    if(permission.indexOf("1") > -1){
                        e = e + "+r";
                    }else{
                        e = e + "-r";
                    }
                    //写权限
                    if(permission.indexOf("2") > -1){
                        e = e + "+w";
                    }else {
                        e = e + "-w";
                    }
                    //x执行权限
                    if(permission.indexOf("3") > -1){
                        e = e + "+x";
                    }else{
                        e = e + "-x";
                    }
                    e = e + " "+parents[i];
                    logger.info(e);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void creatFile(String dir,String path,String permission,String type){
        Properties properties = null;
        String filePath = null;
        String user = null;
        try{
            InputStream in = new BufferedInputStream(XmlOperatorService.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                user = properties.getProperty("ftpUser");
            }
            File file = new File(dir + path);
            boolean flag = true;
            if(!file.exists()){
                flag = file.mkdirs();
                logger.info("创建文件夹 " + file.getPath() + (flag ? " 成功" : " 失败"));
            }
            if(flag){
                changePwd(dir, path,user,permission);
            }

        } catch (IOException e) {
            logger.error("创建文件夹失败",e);
        }
    }


    public String creatFile(String url,String type){
        Properties properties = null;
        String filePath = null;
        try {
//            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            InputStream in = new BufferedInputStream(XmlOperatorService.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                String ftpDir = properties.getProperty("ftpDir");
                String user = properties.getProperty("ftpUser");
                String smbDir = properties.getProperty("smbDir");
                String smbUser = properties.getProperty("smbuserDir");
                if(type.equalsIgnoreCase("ftp")){
                    filePath = ftpDir + "/" + url;
                }else if(type.equalsIgnoreCase("smb")){
                    filePath = smbDir + "/" + url;
                }
                File file = new File(filePath);
//                File file = new File(ftpDir + "/" + url);
                if(!file.exists()){
//                    createFile(file);
//                    boolean flag = makeDir(file,user);
                    boolean  flag = file.mkdirs();
                    changePwd(ftpDir, url,user);
//                    boolean  flag = file.mkdirs();
                    if(flag){
                        logger.info("创建文件夹 " + filePath + " 成功");
                    }else {
                        logger.info("创建文件夹 " + filePath + " 失败");
                    }
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return filePath;
    }

    private void modifyPer(String userName,String filePath){
        String perDir = null;
        String json = null;
        Properties properties = null;
        try {
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                perDir = properties.getProperty("perDir");
                File file =new File(perDir + "/" + userName);
                //文件不存在,创建文件
                if(!file.exists()){
                    file.createNewFile();
                }
                InputStream in_per = new FileInputStream(file);
                Properties per_prop = new Properties();
                per_prop.load(in_per);
                String root = per_prop.getProperty("local_root");
                if(!(root != null && root.equals(filePath.substring(0,filePath.lastIndexOf("/"))))){
                    //todo 只能建立到上一层,否则无法访问ftp
                    per_prop.setProperty("local_root",filePath.substring(0,filePath.lastIndexOf("/")));
                    OutputStream out = new FileOutputStream(file);
                    per_prop.store(out,null);
                    in_per.close();
                    out.close();
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
