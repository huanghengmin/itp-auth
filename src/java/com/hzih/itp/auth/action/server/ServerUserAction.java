package com.hzih.itp.auth.action.server;

import com.hzih.itp.auth.action.ActionBase;
import com.hzih.itp.auth.domain.ClientServer;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientServerService;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.ServerUserService;
import com.hzih.itp.auth.service.impl.XmlOperatorServiceImpl;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */
public class ServerUserAction extends ActionSupport {
    private static Logger logger = Logger.getLogger(ServerUserAction.class);
    private ServerUserService serverUserService;
    private ClientUserService clientUserService;
    private ClientServerService clientServerService;
    private int start;
    private int limit;
    private ServerUser serverUser;

    public ServerUserService getServerUserService() {
        return serverUserService;
    }

    public void setServerUserService(ServerUserService serverUserService) {
        this.serverUserService = serverUserService;
    }


    public ClientUserService getClientUserService() {
        return clientUserService;
    }

    public void setClientUserService(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    public ClientServerService getClientServerService() {
        return clientServerService;
    }

    public void setClientServerService(ClientServerService clientServerService) {
        this.clientServerService = clientServerService;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ServerUser getServerUser() {
        return serverUser;
    }

    public void setServerUser(ServerUser serverUser) {
        this.serverUser = serverUser;
    }

    /**
     * 获取所有的服务
     * @return
     * @throws Exception
     */
    public String select() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            String serverName = request.getParameter("serverName");
            String serverType = request.getParameter("serverType");
            String serverUser = request.getParameter("serverUser");
            String dir = request.getParameter("dir");
            String permission = request.getParameter("permission");
            json = serverUserService.findByPage(serverName,serverType,serverUser,dir,permission,start/limit+1,limit);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务失败", e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下所有的账号
     * @return
     * @throws Exception
     */
    public String selectUsers() throws Exception{

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            logger.info("serverName:" + serverName);
            if(serverName != null && serverName.length() > 0){
                json = serverUserService.findByServerName(serverName,start/limit+1,limit);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务下所有的账号失败", e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }


    /**
     * 获取服务下所有的账号
     * @return
     * @throws Exception
     */
    public String selectUsersByType() throws Exception{

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String serverType = request.getParameter("serverType");
            if(serverType != null && serverType.length() > 0){
                json = serverUserService.findByServerType(serverType);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务下所有的账号失败", e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下账号对应的认证账号  todo 暂时不用 表改了,待修改
     * @return
     * @throws Exception
     */
    /*public String selectClients() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = serverUserService.findByServerUser(Integer.parseInt(id));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务下账号对应的认证账号失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }*/

    /**
     * 获取能够添加的认证账号    todo 暂时不用 表改了,待修改
     * @return
     * @throws Exception
     */
    /*public String selectClientName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = serverUserService.findClientName(Integer.parseInt(id));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务下能够添加的认证账号失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }*/

    /**
     * 获取所有的服务
     * @return
     * @throws Exception
     */
    public String selectServerName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            json = serverUserService.findServerName();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询所有的服务失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下的所有账号
     * @return
     * @throws Exception
     */
    public String selectServerUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            if(serverName != null && serverName.length() > 0){
                json = serverUserService.findServerName();
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询服务下的所有账号失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }

    public String checkServerName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            boolean flag = serverUserService.checkServerName(serverName);
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该服务名已存在'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("验证服务名称失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String checkServerUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String serverUser = request.getParameter("userName");
            String serverName = request.getParameter("serverName");
            String id = request.getParameter("id");
            if((serverName == null || serverName.length() <= 0) &&(id != null && id.length() > 0)){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                serverName = server.getServerName();
            }
            boolean flag = true;
            if(serverName != null){
                 flag= serverUserService.checkServerUser(serverName,serverUser);
            }
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该账号已存在'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("验证服务账号失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String insert() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'新增失败!'}";
        try{
            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
            if(ip != null && ip.length() > 0 && port != null && port.length() > 0){
                serverUser.setIpPort(ip + ":" + port);
            }
            serverUser.setCreateTime(new Date());
            json = serverUserService.add(serverUser);
            if(json != null){
                json = "{success:true,msg:'" + json + "'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String insertUser() throws  Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'新增失败!'}";
        try{
            String serverId = request.getParameter("id");
            String per_add = request.getParameter("per_read");
            String per_write = request.getParameter("per_write");
            String per_modify = request.getParameter("per_do");
//            String per_rename = request.getParameter("per_rename");
//            String per_delete = request.getParameter("per_delete");

            if(serverId != null && serverId.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(serverId));
                String permission = "";
                if(per_add != null && per_add.indexOf("on") > -1){             //读
                    permission += "1;";
                }
                if(per_write != null && per_write.indexOf("on") > -1){        //写
                    permission += "2;";
                }
                if(per_modify != null && per_modify.indexOf("on") > -1){        //追加
                    permission += "3;";
                }
                /*if(per_rename != null && per_rename.indexOf("on") > -1){       //重命名  ->新建文件夹
                    permission += "4;";
                }
                if(per_delete != null && per_delete.indexOf("on") > -1){       //删除 ->重命名和删除
                    permission += "5;";
                }*/
                serverUser.setPermission(permission);
                serverUser.setServerName(server.getServerName());
                serverUser.setServerType(server.getServerType());
                serverUser.setIpPort(server.getIpPort());
                serverUser.setCreateTime(new Date());
                if(serverUser.getDir() == null){
                    serverUser.setDir(server.getServerType() == 1?"/data/smbsource":"/data/ftpsource");
                }
//                serverUser.setDir(server.getDir() != null? server.getDir() : "/data/ftpsource");
                json = serverUserService.add(serverUser);
                if(json != null){
                    json = "{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String insertClient() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'新增失败!'}";
        try{
            String serverId = request.getParameter("id");
            String clientId = request.getParameter("clientId");
            String appName = request.getParameter("appName");
            if(serverId != null && serverId.length() > 0 && clientId != null && clientId.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(serverId));
                ClientUser client = clientUserService.findById(Integer.parseInt(clientId));
                ClientServer clientServer = new ClientServer();
                clientServer.setClientId(Integer.parseInt(clientId));
                clientServer.setServerId(Integer.parseInt(serverId));
                clientServer.setAppName(appName);
                clientServer.setPath("/" + appName + "/" + client.getUserName() + "/" +  server.getServerUser());
                clientServerService.add(clientServer);
                json = "{success:true,msg:'新增成功'}";
                //建立文件夹
                XmlOperatorServiceImpl xmlOperatorService = new XmlOperatorServiceImpl();
                //todo
                xmlOperatorService.creatFile(server.getDir(),clientServer.getPath() ,server.getPermission() , (server.getServerType() ==0 ?"ftp":"smb"));
//                String filePath = xmlOperatorService.creatFile(clientServer.getPath().substring(1),(server.getServerType() ==0 ?"ftp":"smb"));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增失败", e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    private String getFtpDir(){
        String dir = null;
        Properties properties = null;
        try {
            InputStream in = new BufferedInputStream(ServerUserAction.class.getResourceAsStream("/config.properties"));
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

    private void creatFtpFile(String ftpPath){
        String dir = getFtpDir();
        File file = new File(dir + "/" + ftpPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    private void deleteFtpFile(String ftpPath){
        String dir = getFtpDir();
        File file = new File(dir + "/" + ftpPath);
        file.delete();
    }

    public String update() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'修改失败!'}";
        try{
            String id = request.getParameter("id");
            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
            if(ip != null && ip.length() > 0 && port != null && port.length() > 0){
                serverUser.setIpPort(ip + ":" + port);
            }
            if(id != null && id.length() > 0 && serverUser != null){
                serverUser.setId(Integer.parseInt(id));
                ServerUser s_server = serverUserService.findById(Integer.parseInt(id));
                List<ServerUser> servers = serverUserService.findByServerName(s_server.getServerName());

                for(ServerUser server:servers){
                    if(serverUser.getServerName() != null && serverUser.getServerName().length() > 0){
                        server.setServerUser(serverUser.getServerUser());
                    }
                    server.setServerType(serverUser.getServerType());
                    if(serverUser.getIpPort() != null && serverUser.getIpPort().length() > 0){
                        server.setIpPort(serverUser.getIpPort());
                    }
                    json = serverUserService.modify(server);
                }
                if(json != null){
                    json = "{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改失败", e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String updateUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'修改失败!'}";
        try{
            String id = request.getParameter("id");
            String option = request.getParameter("option");
            String per_add = request.getParameter("per_read");
            String per_write = request.getParameter("per_write");
            String per_modify = request.getParameter("per_do");
//            String per_rename = request.getParameter("per_rename");
//            String per_delete = request.getParameter("per_delete");
            if(id != null && id.length() > 0 ){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                //修改密码
                if(option != null && option.equals("changePwd")){
                    if(serverUser != null && serverUser.getServerPwd() != null && serverUser.getServerPwd().length() > 0 && !(serverUser.getServerPwd().indexOf("请输入") > -1)){
                        server.setServerPwd(serverUser.getServerPwd());
                    }
                }else {
                    //修改其他
                    String permission = "";
                    if(per_add != null && per_add.indexOf("on") > -1){             //读
                        permission += "1;";
                    }
                    if(per_write != null && per_write.indexOf("on") > -1){        //写
                        permission += "2;";
                    }
                    if(per_modify != null && per_modify.indexOf("on") > -1){        //追加
                        permission += "3;";
                    }
                    /*if(per_rename != null && per_rename.indexOf("on") > -1){       //重命名
                        permission += "4;";
                    }
                    if(per_delete != null && per_delete.indexOf("on") > -1){       //删除
                        permission += "5;";
                    }*/
                    server.setPermission(permission);
                    if(serverUser != null && serverUser.getServerUser() != null && serverUser.getServerUser().length() > 0){
                        server.setServerUser(serverUser.getServerUser());
                    }
                }
                json = serverUserService.modify(server);
                if(json != null){
                    json = "{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String delete() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'删除失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                List<ClientServer> clientServers = clientServerService.findByServerId(Integer.parseInt(id));
                if(clientServers != null){
                    for(ClientServer clientServer:clientServers){
                        clientServerService.remove(clientServer.getId());
                    }
                }
                serverUser = serverUserService.findById(Integer.parseInt(id));
                if(serverUser != null && serverUser.getServerType() == 1){
                    //smb delete the user and the conf
                    deleteUser(serverUser.getServerUser());
                }
                json = serverUserService.remove(Integer.parseInt(id));
                if(json != null){
                    json ="{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("删除失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    private void deleteUser(String userName){
        OSInfo osInfo = OSInfo.getOSInfo();
        String command = null;
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            command = "sh " + StaticField.systemPath+ "/others/smb.sh/delete.sh " + userName;
            logger.info("删除命令 " + command);
            proc.exec(command);
        }else{
            command = "sh " +StaticField.systemPath+ "/others/smb.sh/delete.sh " + userName;
            logger.info("删除命令 " + command);
        }
    }

    public String deleteClient() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'删除失败!'}";
        try{
            String clientServerId = request.getParameter("clientServerId");
            if(clientServerId != null && clientServerId.length() > 0){
                ClientServer clientServer = clientServerService.findById(Integer.parseInt(clientServerId));
                String path = clientServer.getPath();
                clientServerService.remove(Integer.parseInt(clientServerId));
                deleteFtpFile(path);
            }
            json = "{success:true,msg:'删除成功!'}";
            //删除ftp文件夹
        }catch (Exception e){
            e.printStackTrace();
            logger.error("删除失败",e);
            json = "{success:true,msg:'删除失败!'}";
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String checkIpport() throws Exception{

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String ipport = request.getParameter("ipport");
            boolean flag = serverUserService.checkIpport(ipport);
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该IP端口已被占用'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("验证端口失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String checkPwd() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        boolean flag = false;
        try{
            String password = request.getParameter("password");
            String id = request.getParameter("id");
            if(id != null && id.length() > 0 && password != null && password.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                if(server != null && server.getServerPwd().equals(password)){
                    flag = true;
                }
            }
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'密码错误!'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("验证密码失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }


    /**
     * 获取当前设备的所有IP
     * @return
     * @throws Exception
     */
    public String getIps() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:false,totoal:0,rows:[]}";
        json = getIpFromNetwork();
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;


    }

    private String getIpFromNetwork(){
        // 根据网卡取本机配置的IP
        Enumeration<NetworkInterface> netInterfaces = null;
        String json = "{success:true,rows:[";
        int count = 0;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    String ip = ips.nextElement().getHostAddress();
                    if(ip.indexOf("127.0.0.1") > -1){
                        continue;
                    }
                    json += "{ip:'" + ip + "'},";
                    count ++;
                }
            }
            json += "],count:" + count + "}";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取本机IP失败",e);
        }
        return json;
    }

    public String restart() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'重启成功'}";
        String serverType = request.getParameter("serverType");
        if(serverType != null && serverType.length() > 0){
            json = restartServer(Integer.parseInt(serverType));
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }
    private String restartServer(int serverType){
        OSInfo osInfo = OSInfo.getOSInfo();
        Proc proc = new Proc();
        String json = null;
        if(osInfo.isLinux()){
            String command = null;
            if(serverType ==0 ){
                command = "server pure-ftpd-mysql restart";
            }else{
                command = "server samba restart";
            }
             proc.exec(command);
            json = "{success:true,msg:'重启成功!'}";
        }
        return json;
    }
}
