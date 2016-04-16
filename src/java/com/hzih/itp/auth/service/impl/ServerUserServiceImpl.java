package com.hzih.itp.auth.service.impl;

import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ClientUserDao;
import com.hzih.itp.auth.dao.FtpUserDao;
import com.hzih.itp.auth.dao.ServerUserDao;
import com.hzih.itp.auth.domain.FtpUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ServerUserService;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class ServerUserServiceImpl implements ServerUserService {
    private static Logger logger = Logger.getLogger(ServerUserServiceImpl.class);

    private ServerUserDao serverUserDao;

    private ClientUserDao clientUserDao;

    private FtpUserDao ftpUserDao;

    public ClientUserDao getClientUserDao() {
        return clientUserDao;
    }

    public void setClientUserDao(ClientUserDao clientUserDao) {
        this.clientUserDao = clientUserDao;
    }

    public ServerUserDao getServerUserDao() {
        return serverUserDao;
    }

    public void setServerUserDao(ServerUserDao serverUserDao) {
        this.serverUserDao = serverUserDao;
    }

    public FtpUserDao getFtpUserDao() {
        return ftpUserDao;
    }

    public void setFtpUserDao(FtpUserDao ftpUserDao) {
        this.ftpUserDao = ftpUserDao;
    }

    @Override
    public String findByPage(String serverName, String serverType, String serverUser, String dir, String permission, int pageIndex, int limit) {
        String json = null;
        PageResult ps = null;
        ps = serverUserDao.findByPage(serverName, serverType, serverUser, dir, permission, pageIndex, limit);
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ServerUser> serverUsers = ps.getResults();
            if(serverUsers != null && serverUsers.size() > 0){
                for(ServerUser server:serverUsers){
                    json += "{id:" + server.getId() +
                            ",serverName:'" + checkValue(server.getServerName()) + "'" +
                            ",serverType:" + server.getServerType()  +
                            ",ipPort:'" + checkValue(server.getIpPort()) +  "'" +
                            ",createTime:'" + checkDate(server.getCreateTime()) + "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findByServerName(String serverName, int pageIndex, int limit) {
        String json = null;
        PageResult ps = null;
//        logger.info("serverName:" + serverName);
        ps = serverUserDao.findByServerName(serverName, pageIndex, limit);
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ServerUser> serverUsers = ps.getResults();
            if(serverUsers != null && serverUsers.size() > 0){
                for(ServerUser server:serverUsers){
                    json += "{serverId:" + server.getId() +
                            ",serverName:'" + checkValue(server.getServerName()) + "'" +
                            ",serverUser:'" + checkValue(server.getServerUser())  + "'" +
                            ",permission:'" + checkValue(server.getPermission())  + "'" +
                            ",dir:'" + checkValue(server.getDir())  + "'" +
                            ",createTime:'" + checkDate(server.getCreateTime()) + "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findByServerType(String serverType) {
        String json = null;
        PageResult ps = null;
        ps = serverUserDao.findByServerType(serverType);
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ServerUser> serverUsers = ps.getResults();
            if(serverUsers != null && serverUsers.size() > 0){
                for(ServerUser server:serverUsers){
                    json += "{serverId:" + server.getId() +
                            ",serverName:'" + checkValue(server.getServerName()) + "'" +
                            ",serverUser:'" + checkValue(server.getServerUser())  + "'" +
                            ",permission:'" + checkValue(server.getPermission())  + "'" +
                            ",createTime:'" + checkDate(server.getCreateTime()) + "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    /*@Override
    public String findByServerUser(int id) {
        ServerUser serverUser = null;
        String json = null;
        serverUser = findById(id);
        if(serverUser != null){
            Set<ClientUser> clients = serverUser.getClients();
            if(clients != null){
                json = "{success:true,count:" + clients.size() + ",rows:[";
                if(clients.size() > 0){
                    for(ClientUser client:clients){
                        json += "{id1:" + id +
                                ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                ",clientId:" + client.getId() +
                                ",userName:'" + checkValue(client.getUserName()) + "'" +
                                ",ip:'" + checkValue(client.getIp()) + "'" +
                                ",mac:'" + checkValue(client.getMac()) + "'" +
                                ",connectTime:'" + checkDate(client.getConnectTime()) + "'},";
                    }
                }
                json += "]}";
            }
        }
        return json;
    }*/

    /*@Override
    public String findClientName(int id) {
        ServerUser serverUser = null;
        String json = null;
        serverUser = findById(id);
        List<ClientUser> allClients = clientUserDao.findAll();
        if(serverUser != null){
            Set<ClientUser> clients = serverUser.getClients();
            Map<Integer,ClientUser> existClient = new HashMap<Integer, ClientUser>();
            if(clients != null && clients.size() > 0){
                for(ClientUser client:clients){
                    if(client != null){
                        existClient.put(client.getId(),client);
                    }
                }
            }
            if(allClients != null && allClients.size() > 0){
                json = "{success:true,rows:[";
                int count = 0;
                for(ClientUser client:allClients){
                    if(!existClient.containsKey(client.getId())){
                        count ++;
                        json += "{value:" + client.getId() + ",key:'" + client.getUserName() + "'},";
                    }
                }
                json += "],count:" + count + "}";
            }
        }
        return json;
    }*/

    @Override
    public String findServerName() {
        List<ServerUser> serverUsers = null;
        String json = null;
        serverUsers = serverUserDao.findAllServer();
        if(serverUsers != null){
            json = "{success:true,count:" + serverUsers.size() + ",rows:[";
            for(ServerUser serverUser:serverUsers){
                json += "{serverId:" + serverUser.getId() + ",serverName:'" + checkValue(serverUser.getServerName()) + "'},";
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public ServerUser findById(int id) {
        return (ServerUser)serverUserDao.getById(id);
    }

    @Override
    public List<ServerUser> findByServerName(String serverName) {
        return serverUserDao.findByPorperty("serverName",serverName,0);
    }

    /**
     * 验证该服务名是否可以使用,true表示可以使用,false表示已存在该服务名
     * @param serverName
     * @return
     */
    @Override
    public boolean checkServerName(String serverName) {
        List<ServerUser> serverUsers = null;
        if(serverName != null && serverName.length() > 0){
            serverUsers = serverUserDao.findByPorperty("serverName",serverName,0);
        }
        if(serverUsers == null || serverUsers.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean checkServerUser(String serverName, String serverUser) {
        List<ServerUser> serverUsers = null;
        if(serverName != null && serverName.length() > 0){
            serverUsers = serverUserDao.findByPorperty("serverName",serverName,0);
            if(serverUsers!= null && serverUsers.size() > 0){
                for(int i =0;i<serverUsers.size() ; i++){
                    String user = serverUsers.get(i).getServerUser();
                    if(user != null && user.equals(serverUser) ){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkIpport(String ipport) {
        List<ServerUser> serverUsers = null;
        if(ipport != null && ipport.length() > 0){
            serverUsers = serverUserDao.findByPorperty("ipPort", ipport, 0);
        }
        if(serverUsers == null || serverUsers.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String add(ServerUser serverUser) {
        String json = "新增失败!";
        try{
            if(serverUser.getServerType() == 1 && serverUser.getServerPwd() == null){
                serverUser.setServerPwd(getSmbPwd());
            }
            serverUserDao.create(serverUser);
            //建立实名用户 设置实名用户登录密码
            if(serverUser.getServerUser() != null && serverUser.getServerUser().length() > 0){
                FtpUser ftpUser = new FtpUser(serverUser.getServerUser(),serverUser.getServerPwd());
                ftpUserDao.create(ftpUser);
                //设置用户权限
                if(serverUser.getServerType() == 0){
                    //fpt
//                    setPermission(serverUser.getServerUser(),serverUser.getPermission());
                }else if(serverUser.getServerType() == 1){
                    //smb   todo 数据库中的密码无效
                    //建立用户虚拟账号 smbuser
//                    createSmbUser(serverUser.getServerUser());
                    createlocalUser(serverUser.getServerUser(),serverUser.getServerPwd());
//                    setSmbPermission(serverUser.getServerUser(),serverUser.getDir(),serverUser.getPermission());
                    addSmbPermissioin(serverUser.getServerUser(),serverUser.getDir(),serverUser.getPermission());
                }
            }

            json = "新增成功!";

        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增权限失败",e);
        }
        return json;
    }

    private String getSmbPwd(){
        Properties properties = null;
        String pwd = null;
        try{
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties != null){
                pwd = properties.getProperty("userPwd");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return pwd;
    }

    /**
     * 建立本地用户,密码为admin  ,并将用户添加到smbpasswd中
     * @param userName
     * @param passwd
     */
    private void createlocalUser(String userName,String passwd){
        String fileName = StaticField.systemPath + "/others/pwd.sh";
        try{
            OSInfo osInfo = OSInfo.getOSInfo();
            File file = new File(fileName);
            //添加本地用户
            String sh = new String("echo -e \"admin\\nadmin\"| passwd "+ userName + ";");
            //将本地用户添加到smbpasswd中
            String sh_1 = new String("echo -e \"" + passwd + "\\n" + passwd + "\"| smbpasswd -s -a " + userName + ";");
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.println(sh);
            pw.println(sh_1);
            pw.flush();
            pw.close();
            file.canExecute();
            if(osInfo.isLinux()){
                Proc proc = new Proc();
                String command = "bash " + fileName;
                logger.info(command);
                proc.exec("useradd " + userName);
                if(proc.exec(command)){
                    String output = proc.getOutput();
                    StringTokenizer tokenizer = new StringTokenizer(output, "\n");
                    while (tokenizer.hasMoreTokens()) {
                        String line = tokenizer.nextToken();
                        logger.info(line);
                    }
                }
            }else {
                logger.info("bash " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    /**
     * 删除本地用户
     * @param userName
     */
    private void deletelocalUser(String userName){
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            proc.exec("smbpasswd -x " + userName);
            proc.exec("userdel " + userName);
            logger.info("删除用户 " + userName + " 成功");
        }else {
            logger.info("smbpasswd -x " + userName);
            logger.info("userdel " + userName);
        }
    }

    /**
     * 虚拟用户映射到本地用户 todo delete
     * @param userName
     */
    private void createSmbUser(String userName){
        Proc proc = new Proc();
        Properties properties = null;
        String smbuserDir = null,user = null;
        try{
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                smbuserDir = properties.getProperty("smbuserDir");
                user = properties.getProperty("ftpUser");
            }
            in.close();
            in = new FileInputStream(new File(smbuserDir));
            properties = new Properties();
            properties.load(in);
            String users = properties.getProperty(user);
            if(users == null|| users.length() == 0){
                users = userName;
            }else {
                if(users.indexOf(userName) <= -1){
                    users = users +  "," + userName;
                }
            }
            Properties out_prop = new Properties();
            out_prop.setProperty(user,users);  OutputStream out = new FileOutputStream(new File(smbuserDir));
            out_prop.store(out,null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * 通过脚本增加权限
     * @param userName
     * @param dir
     * @param permission
     */
    private void addSmbPermissioin(String userName,String dir,String permission){
        OSInfo osInfo = OSInfo.getOSInfo();
        String command = null;
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            command = "sh " +StaticField.systemPath+ "/others/smb.sh/delete.sh " + userName;
            logger.info("删除命令 " + command);
            proc.exec(command);
            command = "sh " +StaticField.systemPath+ "/others/smb.sh/update.sh " + userName + " " + dir + " no ";
            if(permission.indexOf("2") > -1){
                command = command + "yes";
            }else{
                command = command + "no";
            }
            command = command + " viruser," + userName;
            logger.info("增加命令 " + command);
            proc.exec(command);
        }else{
            command = "sh " +StaticField.systemPath+ "/others/smb.sh/delete.sh " + userName;
            logger.info("删除命令 " + command);
            command = "sh " +StaticField.systemPath+ "/others/smb.sh/update.sh " + userName + " " + dir + " no ";
            if(permission.indexOf("2") > -1){
                command = command + "yes";
            }else{
                command = command + "no";
            }
            command = command + " viruser," + userName;
            logger.info("增加命令 " + command);
        }

    }

    private String getSmbPermissionResult() {
        String str = "";
        BufferedReader in = null;
        try{
            in = new BufferedReader(new FileReader("/etc/samba/smb.conf"));
            String r = in.readLine();
            while (r != null){
                str += r.trim() + "$net$";
                r = in.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if( in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        return  str;
    }

    private void setSmbPermission(String userName,String dir,String permission){
        String smbperDir = null,user = null;//,dir = null;
        Properties properties = null;
        try {
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                smbperDir = properties.getProperty("smbperDir");
                user = properties.getProperty("ftpUser");
//                dir = properties.getProperty("ftpDir");
                File file =new File(smbperDir + "/" + userName + ".smb.conf");
                //文件不存在,创建文件
                if(!file.exists()){
                    file.createNewFile();
                }
                String read_only = null;   //读权限
                if(permission.indexOf("2") > -1){
                    read_only = "NO";
                }else{
                    read_only = "YES";
                }
                FileWriter fw = new FileWriter(smbperDir + "/" + userName + ".smb.conf");
                fw.write("[" + userName + "]\n");
                fw.write("path=" + dir + "\n");
                fw.write("read only=" + read_only +"\n");
                fw.write("public=no" + "\n");
                fw.write("valid users=" + user + "," + userName + "\n");
                //刷新缓冲区
                fw.flush();
                //关闭文件流对象
                fw.close();
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removePermission(String userName,int serverType){
        //ftp
        String perDir = null;
        //smb
        String smbuserDir = null,smbperDir = null,ftpUser = null;
        Properties properties = null;
        try {
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                perDir = properties.getProperty("perDir");
                smbperDir = properties.getProperty("smbperDir");
                smbuserDir = properties.getProperty("smbuserDir");
                ftpUser = properties.getProperty("ftpUser");
                if(serverType == 0){
                    //ftp
                    File file =new File(perDir + "/" + userName);
                    //文件存在,删除文件
                    if(file.exists()){
                        file.delete();
                    }
                }else if(serverType == 1){
                    //smb
                    File file = new File(smbperDir + "/" + userName + ".smb.conf");
                    if(file.exists()){
                        file.delete();
                    }
                    InputStream smbuser_in = new FileInputStream(new File(smbuserDir));
                    Properties user_prop = new Properties();
                    user_prop.load(smbuser_in);
                    if(user_prop != null){
                        String users = user_prop.getProperty(ftpUser);
                        if(users != null){
                            users = users.trim();
                            users = users.replaceAll("," + userName, "");
                            if(users.indexOf(userName) > -1){
                                if(users.indexOf(userName + ",") > -1){
                                    users = users.replaceAll((userName + ","),"");
                                }else {
                                    users = users.replaceAll(userName,"");
                                }
                            }
                            user_prop.setProperty(ftpUser,users);
                        }
                        OutputStream out = new FileOutputStream(new File(smbuserDir));
                        user_prop.store(out, null);
                        out.close();
                    }

                }

            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void setPermission(String userName,String permission){
        String perDir = null,ftpDir = null;
        Properties properties = null;
        try {
            InputStream in = new BufferedInputStream(ServerUserServiceImpl.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                perDir = properties.getProperty("perDir");
                ftpDir = properties.getProperty("ftpDir");
                File file =new File(perDir + "/" + userName);
                //文件不存在,创建文件
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                Properties per_prop = new Properties();
                String anon_world_readable_only = null;   //读权限
                String write_enable = null;  //写权限
                String anon_upload_enable = null; //上传权限
                String anon_mkdir_write_enable = null; //是否可以建目录
                String anon_other_write_enable = null; //是否可以删除文件及目录
                if(permission.indexOf("1") > -1){
                    anon_world_readable_only = "NO";
                }else{
                    anon_world_readable_only = "YES";
                }
                if(permission.indexOf("2") > -1){
                    write_enable = "YES";
                    anon_upload_enable = "YES";
                }else {
                    write_enable = "NO";
                    anon_upload_enable = "NO";
                }
                if(permission.indexOf("4") > -1){
                    anon_mkdir_write_enable = "YES";
                }else {
                    anon_mkdir_write_enable = "NO";
                }
                if(permission.indexOf("5") > -1){
                    anon_other_write_enable = "YES";
                }else {
                    anon_other_write_enable = "NO";
                }
                per_prop.setProperty("local_root",ftpDir );
                per_prop.setProperty("anon_world_readable_only",anon_world_readable_only);
                per_prop.setProperty("write_enable",write_enable);
                per_prop.setProperty("anon_upload_enable",anon_upload_enable);
                per_prop.setProperty("anon_mkdir_write_enable",anon_mkdir_write_enable);
                per_prop.setProperty("anon_other_write_enable",anon_other_write_enable);
                OutputStream out = new FileOutputStream(file);
                per_prop.store(out,null);
                out.close();
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //todo delete
    public String getFtpDir(){
        String dir = null;
        Properties properties = null;
        try {
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                dir = properties.getProperty("ftpDir");
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return dir;
    }

    /**
     * 创建ftp账号         todo delete
     * @param userName
     * @param userPwd
     */
    private void createFtpUser(String userName,String userPwd){
        //通过jdbc 连接mysql数据库中vftpuser
        Connection conn = null;
        String sql;
        String url,driverClass,user,password;
        Properties properties = null;
        try{
            InputStream in = new BufferedInputStream(ServerUserService.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            url = properties.getProperty("ftp.jdbc.url");
            driverClass =properties.getProperty("jdbc.driverClass");
            user =properties.getProperty("ftp.jdbc.user");
            password =properties.getProperty("ftp.jdbc.password");
            Class.forName(driverClass);// 动态加载mysql驱动
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            sql = "insert into users (name,passwd) values ('" + userName + "', '" + userPwd+  "');";
            int result = stmt.executeUpdate(sql);

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    @Override
    public String modify(ServerUser serverUser) {
        String json = "修改失败!";
        try{
            if(serverUser.getServerUser() != null && serverUser.getServerUser().length() > 0){
                FtpUser ftpUser = (FtpUser) ftpUserDao.getById(serverUser.getServerUser());
                if(ftpUser != null){
                    if(!serverUser.getServerPwd().equals(ftpUser.getPasswd())){
                        ftpUser.setPasswd(serverUser.getServerPwd());
                        ftpUserDao.update(ftpUser);
                        if(serverUser.getServerType() == 1){
                            //SMB CHANGE PASSWD
                            deletelocalUser(serverUser.getServerUser());
                            createlocalUser(serverUser.getServerUser(),serverUser.getServerPwd());
                        }
                    }
                }
            }
            serverUserDao.update(serverUser);
            //设置用户权限
            if(serverUser.getServerType() == 0){
                setPermission(serverUser.getServerUser(), serverUser.getPermission());
            }else {
                setSmbPermission(serverUser.getServerUser(),serverUser.getDir(),serverUser.getPermission());
            }
            json = "修改成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String remove(int id) {
        String json = "删除失败!";
        try{
            ServerUser serverUser = (ServerUser) serverUserDao.getById(id);
            if(serverUser.getServerUser() != null && serverUser.getServerUser().length() > 0){
                FtpUser ftpUser = (FtpUser) ftpUserDao.getById(serverUser.getServerUser());
                if(ftpUser != null){
                    ftpUserDao.delete(serverUser.getServerUser());
                }
            }
            if(serverUser.getServerType() == 0){
                //ftp
            }else {
                //smb
                deletelocalUser(serverUser.getServerUser());
            }
            serverUserDao.delete(id);
            //删除用户权限
//            removePermission(serverUser.getServerUser(),serverUser.getServerType());
            json = "删除成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    private String checkValue(String json) {
        if(json == null){
            return "";
        }else {
            return json;
        }
    }

    private String checkDate(Date date){
        if(date == null){
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(date);
    }
}
