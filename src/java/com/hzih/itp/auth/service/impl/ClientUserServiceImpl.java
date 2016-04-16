package com.hzih.itp.auth.service.impl;

import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ClientServerDao;
import com.hzih.itp.auth.dao.ClientUserDao;
import com.hzih.itp.auth.dao.ServerUserDao;
import com.hzih.itp.auth.dao.XmlOperatorDAO;
import com.hzih.itp.auth.dao.impl.XmlOperatorDAOImpl;
import com.hzih.itp.auth.domain.ClientServer;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.servlet.Service;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.config.stp.ConfigParser;
import com.inetec.common.config.stp.nodes.Channel;
import com.inetec.common.config.stp.nodes.IChange;
import com.inetec.common.exception.Ex;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientUserServiceImpl implements ClientUserService {
    private static Logger logger = Logger.getLogger(ClientUserServiceImpl.class);

    private ClientUserDao clientUserDao;
    private ClientServerDao clientServerDao;
    private ServerUserDao serverUserDao;

    public ClientUserDao getClientUserDao() {
        return clientUserDao;
    }

    public void setClientUserDao(ClientUserDao clientUserDao) {
        this.clientUserDao = clientUserDao;
    }

    public ClientServerDao getClientServerDao() {
        return clientServerDao;
    }

    public void setClientServerDao(ClientServerDao clientServerDao) {
        this.clientServerDao = clientServerDao;
    }

    public ServerUserDao getServerUserDao() {
        return serverUserDao;
    }

    public void setServerUserDao(ServerUserDao serverUserDao) {
        this.serverUserDao = serverUserDao;
    }

    @Override
    public String add(ClientUser clientUser) {
        String json = "新增失败!";
        try{
            clientUserDao.create(clientUser);
            json = "新增成功!";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("新增失败",e);
        }
        return json;
    }

    @Override
    public String modify(ClientUser clientUser) {
        String json = "修改失败!";
        try{
            clientUserDao.update(clientUser);
            json = "修改成功!";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("修改失败",e);
        }
        return json;
    }

    @Override
    public String remove(int userId) {
        String json = "删除失败!";
        try{
            clientUserDao.delete(userId);
            json = "删除成功!";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("删除失败",e);
        }
        return json;
    }

    @Override
    public String findByPage(String userName, int pageIndex, int limit) {
        PageResult ps = null;
        String json = null;
        try{
            ps = clientUserDao.findByPage(userName, pageIndex, limit);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            logger.error("查询失败",e);

        }
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ClientUser> clientUserList = ps.getResults();
            if(clientUserList != null && clientUserList.size() > 0){
                for(ClientUser clientUser:clientUserList){
                    json += "{id:" + clientUser.getId() +
                            ",userName:'" + checkValue(clientUser.getUserName()) + "'" +
                            ",ip:'" + checkValue(clientUser.getIp()) + "'" +
                            ",mac:'" + checkValue(clientUser.getMac()) + "'" +
                            ",createTime:'" + checkTime(clientUser.getCreateTime())+ "'" +
                            ",isconnect:" + (Service.connectClients.containsKey(clientUser.getId())?true:false) +
                            ",connectTime:'" + checkTime(clientUser.getConnectTime())+ "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findServers(int userId) {
        String json = null;
        int count = 0;
        ClientUser clientUser = null;
        clientUser = findById(userId);
        if(clientUser != null){
            List<ClientServer> clientServers = clientServerDao.findByProperty("clientId",userId,0);
            json = "{success:true,rows:[";
            if(clientServers != null){
                for(ClientServer clientServer:clientServers){
                    int serverId = clientServer.getServerId();
                    ServerUser serverUser = serverUserDao.findById(serverId);
                    if(serverUser != null){
                        json += "{clientServerId:" + clientServer.getId() +
                                ",serverId:" + serverUser.getId() +
                                ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                ",appName:'" + checkValue(clientServer.getAppName()) + "'" +
                                ",serverType:" + serverUser.getServerType() +
                                ",ipPort:'" + checkValue(serverUser.getIpPort()) + "'" +
                                ",serverUser:'" + checkValue(serverUser.getServerUser()) + "'" +
                                ",serverPwd:'" + checkValue(serverUser.getServerPwd()) + "'" +
                                ",permission:'" + serverUser.getPermission()  + "'"+
                                ",dir:'" + checkValue(clientServer.getPath()) + "'},";
                        count ++;
                    }
                }
            }
            json += "],count:" + count + "}";
        }
        return json;
    }

    @Override
    public ClientUser findById(int id) {
        return clientUserDao.findById(id);
    }

    @Override
    public String checkByNamePwd(String userName, String userPwd,String clientIp,String clientMac) {
//        logger.info("ip:" + clientIp + ",mac:" + clientMac);
        ClientUser clientUser = null;
        String json = "认证失败,用户名不存在";
        clientUser = clientUserDao.findByProperty("userName",userName,0);
        if(clientUser != null){
            if(userPwd != null && userPwd.equals(clientUser.getUserPwd())){
                if(clientIp != null && clientMac != null && clientUser.getIp() != null && clientUser.getMac() != null &&
                        clientIp.toLowerCase().equals(clientUser.getIp().toLowerCase()) &&
                        clientMac.replaceAll("-|:","").toLowerCase().equals(clientUser.getMac().replaceAll("-|:","").toLowerCase())){
                    if(!Service.connectClients.containsKey(clientUser.getId())){       //第一次认证
                        clientUser.setConnectTime(new Date());
                        clientUser.setDisconnectTime(new Date()); //断开连接时间=最后连接的时间
                        modify(clientUser);
                        Service.connectClients.put(clientUser.getId(), clientUser);
                        //建立规则
                        String platformType = getPlatformType();         //主机名字
                        //itp-ex,itp-in通过ftp或smb建立规则
                        if(platformType.equals(StaticField.Platform_Type_EX_ITP)){
                            List<ClientServer> clientServers = clientServerDao.findByProperty("clientId",clientUser.getId(),0);
                            Map<String,String> ipPorts = new HashMap<String, String>();
                            if(clientServers != null){
                                for(ClientServer clientServer:clientServers){
                                    int serverId = clientServer.getServerId();
                                    ServerUser serverUser = serverUserDao.findById(serverId);
                                    String ipPort = serverUser.getIpPort();
                                    if(ipPort != null && ipPort.length() > 0 && !ipPorts.containsKey(ipPort)){
                                        ipPorts.put(ipPort,ipPort);
//                                        makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],serverUser.getIpPort().split(":")[1]);
                                        if(!getStatus(clientIp)){
                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
                                        }
                                        setMap(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
                                        /*if(serverUser.getServerType() == 0){
                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
//                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],"20");
                                        }else if(serverUser.getServerType() == 1){
//                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],"137");
//                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],"138");
//                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],"139");
                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
                                            makeRule(clientIp,clientIp,serverUser.getIpPort().split(":")[0],null);
                                        }*/
                                    }
                                }
                            }
                        }else if(platformType.equals(StaticField.Platform_Type_EX_STP)){
                            //stp-ex 读取 /usr/app/itp/repository/external/config.xml port
                            logger.info("从config.xml中获取端口");
                            XmlOperatorDAO xmlOperatorDAO = new XmlOperatorDAOImpl();
                            try {
                                //ge remote ip

                                ConfigParser configParser = new ConfigParser(StaticField.EXTERNALXML);
                                IChange ichange = configParser.getRoot();
                                Channel channel = ichange.getChannel(StaticField.ChannelName);
                                String remoteIp = channel.getRemoteIp();
                                String serverIp = channel.getLocalIp();
                                if(!getStatus(clientIp)){
                                    makeRule(clientIp,remoteIp,serverIp,null);
                                }
                                setMap(clientIp,remoteIp,serverIp,null);
                                /*List<Type> externalTypes = xmlOperatorDAO.getTypes("external","httpproxy");
                                for(Type type:externalTypes){
                                    *//*SourceFile sourceFile = xmlOperatorDAO.getSourceFiles("external",type.getTypeName());
                                    if(sourceFile != null){
                                    }*//*
//                                    makeRule(clientIp,remoteIp,serverIp,null);
                                    makeRule(clientIp,remoteIp,serverIp,type.getChannelPort());
                                    makeRule(clientIp,remoteIp,serverIp,String.valueOf(Integer.parseInt(type.getChannelPort()) + 1));
                                }*/
                            } catch (Ex ex) {
                                ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }else if(platformType.equals(StaticField.Platform_Type_IN_STP)){
                            //目前不需要认证
                        }else if(platformType.equals(StaticField.Platform_Type_IN_ITP)){
                            XmlOperatorDAO xmlOperatorDAO = new XmlOperatorDAOImpl();
                            try {
                                //ge remote ip

                                ConfigParser configParser = new ConfigParser(StaticField.EXTERNALXML);
                                IChange ichange = configParser.getRoot();
                                Channel channel = ichange.getChannel(StaticField.ChannelName);
                                String remoteIp = channel.getRemoteIp();
                                String serverIp = channel.getLocalIp();
//                                List<Type> externalTypes = xmlOperatorDAO.getTypes("external","file");
                                if(remoteIp != null && clientIp.equals(remoteIp)){
                                    /*for(Type type:externalTypes){
                                    *//*SourceFile sourceFile = xmlOperatorDAO.getSourceFiles("external",type.getTypeName());
                                    if(sourceFile != null){
                                    }*//*
                                        makeRule(clientIp,remoteIp,serverIp,type.getChannelPort());
                                        makeRule(clientIp,remoteIp,serverIp,String.valueOf(Integer.parseInt(type.getChannelPort()) + 1));
                                    }*/

                                    if(!getStatus(clientIp)){
                                        makeRule(clientIp,remoteIp,serverIp,null);
                                    }
                                    setMap(clientIp,remoteIp,serverIp,null);
                                }else{
                                    if(!getStatus(clientIp)){
                                        makeRule(clientIp,clientIp,null,null);
                                    }
                                    setMap(clientIp,clientIp,null,null);
                                }
                            } catch (Ex ex) {
                                ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                        }
                    }else{       //发送脉冲认证
                        clientUser = Service.connectClients.get(clientUser.getId());
                        if(clientUser != null){
                            clientUser.setDisconnectTime(new Date()); //断开连接时间=最后连接的时间
                            modify(clientUser);
                            logger.info("客户端用户[" + clientUser.getUserName() +"]在[" + clientUser.getDisconnectTime() + "]发送脉冲成功");
                        }else {
                            logger.info("客户端用户[" + userName +"]已下线");
                        }
                    }
                    //返回认证成功,和客户端的ID用于再次认证
                    json = "{success:true,msg:'认证成功!',clientId:" + clientUser.getId()+ "}";
                }else{
                    logger.info("clientIp"+clientIp);
                    logger.info("clientMac" + clientMac);
                    logger.info("clientuserIp:" + clientUser.getIp());
                    logger.info("clientusermac:" + clientUser.getMac());
//                    logger.info(clientIp.toLowerCase().equals(clientUser.getIp().toLowerCase()));
//                    logger.info(clientMac.replaceAll("-|:","").toLowerCase().equals(clientUser.getMac().replaceAll("-|:","").toLowerCase()));
                    json = "IP或MAC地址不正确,请联系管理员!";
                }
            }else{
                json = "密码错误!认证失败!";
            }
        }
        return json;
    }

    @Override
    public ClientUser findByProperty(String propertyName, String propertyValue,int option) {
        return  clientUserDao.findByProperty(propertyName,propertyValue,option);
    }

    private String getPlatformType() {
        OSInfo os = OSInfo.getOSInfo();
        if(os.isLinux()) {
            return os.getHostName();
        }
        return StaticField.Platform_Type_IN_ITP;
    }

    private boolean getStatus(String ip){
        OSInfo os = OSInfo.getOSInfo();
        Proc proc = new Proc();
        boolean result = false;
        if(os.isLinux()) {
            if(proc.exec("iptables -S")){
                String output = proc.getOutput();
                StringTokenizer tokenizer = new StringTokenizer(output, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    if(line.indexOf("-A INPUT -s " + ip) > -1){
                        return true;
                    }
                }
            }
        }
        return result;
    }

    private void makeRule(String clientIp,String remoteIp,String localIp,String localPort){
        OSInfo osInfo = OSInfo.getOSInfo();
        Proc proc = new Proc();
        if(osInfo.isLinux()){
            //command:   iptables -A INPUT -s 认证地址 -d 本机 -p tcp -m tcp --dport 端口 -j ACCEPT
            logger.info("localPort:" + localPort);
            if(localPort == null || localPort.equals("null")){
                localPort = "null";
                localIp = "null";
                proc.exec("iptables -A INPUT -s " +remoteIp + " -j ACCEPT");
                logger.info("建立 源IP " + remoteIp + " 规则成功");
            }else{
                proc.exec("iptables -A INPUT -s " +remoteIp + " -d " + localIp+ " -p tcp -m tcp --dport " + localPort+ " -j ACCEPT");
                logger.info("建立 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
            }

        }else{
            //用于本地测试
            logger.info("localPort:" + localPort);
            if(localPort == null || localPort.equals("null")){
                localPort = "null";
                localIp = "null";
                System.out.println("iptables -A INPUT -s " + remoteIp + " -j ACCEPT");
                logger.info("建立 源IP " + remoteIp + " 规则成功");
            }else{
                System.out.println("iptables -A INPUT -s " + remoteIp + " -d " + localIp + " -p tcp -m tcp --dport " + localPort + " -j ACCEPT");
                logger.info("建立 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
            }
            if(Service.iptables.containsKey(clientIp)){
                String value = Service.iptables.get(clientIp);
                if(value.indexOf(remoteIp + "_" + localIp + "_" + localPort + ";") <= -1){
                    value = value + remoteIp + "_"+ localIp + "_" + localPort + ";";
                }
                Service.iptables.remove(clientIp);
                Service.iptables.put(clientIp,value);
            }else {
                Service.iptables.put(clientIp,remoteIp + "_" + localIp + "_"+localPort + ";");
            }

        }
    }

    private void setMap(String clientIp,String remoteIp,String localIp,String localPort){
        if(Service.iptables.containsKey(clientIp)){
            String value = Service.iptables.get(clientIp);
            if(value.indexOf(remoteIp + "_" + localIp + "_" + localPort + ";") <= -1){
                value = value + remoteIp + "_"+ localIp + "_" + localPort + ";";
            }
            Service.iptables.remove(clientIp);
            Service.iptables.put(clientIp,value);
            logger.info("iptable map change key:" + clientIp + ",value:" + Service.iptables.get(clientIp));
        }else {
            Service.iptables.put(clientIp,remoteIp + "_" + localIp + "_"+localPort + ";");
            logger.info("iptable map put key:" + clientIp + ",value:" + Service.iptables.get(clientIp));
        }
    }

    private String checkValue(String json){
        if(json == null){
            return "";
        }else {
            return json;
        }
    }
    public String checkTime(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(date == null){
            return "";
        }else {
            return sf.format(date);
        }

    }
}
