package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientServer;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientServerService;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.LogService;
import com.hzih.itp.auth.service.ServerUserService;
import com.hzih.itp.auth.utils.JSONUtils;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.net.NicMac;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public class RZClient extends Thread {
    private static Logger logger = Logger.getLogger(RZClient.class);
    static BufferedReader in;
    static PrintWriter out;
    static Socket s;

    /*
    *构造函数,获得socket连接,初始化in和out对象
    */

    public RZClient(Socket socket)
    {
        try
        {
            s=socket;
            in=new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            out=new PrintWriter(s.getOutputStream(),true);

            start();   //开新线程执行run方法

        }catch(Exception e)
        {
            System.out.println(e);
            logger.error("开启认证服务出错",e);
        }

    }

    /*
    *线程方法,处理socket传递过来的数据
    */

    public void run()
    {
        try
        {
            String msg=in.readLine();
            System.out.println(msg);
            String json = null;
            json = check(msg);
            System.out.println(json);
            out.println(json);
            in.close();
            out.close();
            s.close();

        }catch(Exception e)
        {
            System.out.println(e);
            logger.error("认证出错",e);
            try{
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
                if(s != null){
                    s.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /**
     * 对客户端进行认证
     * @return
     */
    private String check(String msg) throws IOException {
        JSONObject json_user = JSONUtils.toJSONObject(msg);
        String command = json_user.get("command").toString();
        String remoteIp = s.getInetAddress().getHostAddress();
        //todo getMac
//        NicMac nicMac = new NicMac(remoteIp);
//        String remoteMac = nicMac.GetRemoteMacAddr();
        String remoteMac = getMacByArp(remoteIp);
        logger.info("mac:" + remoteMac);
        String json = "认证失败";
        if(command.equals(StaticField.CONNECT) || command.equals(StaticField.KEEPALIVE)){
            String clientUser = json_user.get("clientUser").toString();
            String clientPwd = json_user.get("clientPwd").toString();
            clientUser = new String(new BASE64Decoder().decodeBuffer(clientUser));
            logger.info("clientUser:" + clientUser + ",clientPwd:" + clientPwd);
            ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
            LogService logService = (LogService)SpringContextUtil.getBean("logService");
            if(clientUserService != null && logService != null){
                if(clientUser != null && clientUser.length() > 0 && clientPwd != null && clientPwd.length() > 0){
                    json = clientUserService.checkByNamePwd(clientUser,clientPwd,remoteIp,remoteMac);
                    if(json.indexOf("成功") > -1){
                        //认证成功
                        if(command.equals(StaticField.CONNECT)){
                            logService.newSysLog("INFO","认证模式","认证成功","在 " + remoteIp + " 以" + clientUser + " 身份认证成功");
                            logger.info("在 " + remoteIp + " 以" + clientUser + " 身份认证成功");
                        }
                    }else{
                        json = "{success:false,msg:'" + json + "'}";
                        logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份认证失败");
                        logger.warn("在 " + remoteIp + " 以" + clientUser + " 身份认证失败");
                    }

                }else{
                    System.out.println("用户名或密码为空. . .");
                    json = "{success:false,msg:'用户名或密码不能为空'}";
                    logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份认证失败");
                    logger.warn("用户名或密码为空. . .");
                }

            }else {
                System.out.println("获取service错误. . .");
                logger.error("获取service错误. . .");
                json = "{success:false,msg:'服务端认证失败'}";

            }
        }else if(command.equals(StaticField.SERVER)){
            String clientId = json_user.get("clientId").toString();
            if(clientId != null && clientId.length() > 0){
                ClientUser client = Service.connectClients.get(Integer.parseInt(clientId));
                if(client != null){
                    ClientServerService clientServerService = (ClientServerService)SpringContextUtil.getBean("clientServerService");
                    ServerUserService serverUserService = (ServerUserService)SpringContextUtil.getBean("serverUserService");
                    client.setDisconnectTime(new Date());
                    int count = 0;
                    List<ClientServer> clientServers = clientServerService.findByClientId(client.getId());
                    if(clientServers != null){
                        count = clientServers.size();
                    }
                    json = "{success:true,count:" + count + ",rows:[";
                    if(clientServers != null){
                        for(ClientServer clientServer:clientServers){
                            int serverId = clientServer.getServerId();
                            ServerUser serverUser = serverUserService.findById(serverId);
                            json += "{serverId:" + serverUser.getId() +
                                    ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                    ",serverType:" + serverUser.getServerType()  +
                                    ",ipPort:'" + checkValue(serverUser.getIpPort()) +  "'" +
                                    ",serverUser:'" + checkValue(serverUser.getServerUser()) + "'" +
//                                ",serverPwd:'" + checkValue(serverUser.getServerPwd()) + "'" +
                                    ",permission:'" + serverUser.getPermission() + "'" +
                                    ",dir:'" + checkValue(clientServer.getPath()) + "'" +
                                    "},";
                        }
                    }
                    json += "]}";
                }
            }else {
                json = "{success:false,msg:'请重新认证!'}";
            }
        }else if(command.equals(StaticField.PASSWORD)){
            int serverId = json_user.getInt("serverId");
            int clientId = json_user.getInt("clientId");
            String clientUser = json_user.get("clientUser").toString();
            String clientPwd = json_user.get("clientPwd").toString();
            clientUser = new String(new BASE64Decoder().decodeBuffer(clientUser));
            ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
            LogService logService = (LogService)SpringContextUtil.getBean("logService");
            if(clientUserService != null && logService != null){
                if(clientUser != null && clientUser.length() > 0 && clientPwd != null && clientPwd.length() > 0){
                    json = clientUserService.checkByNamePwd(clientUser,clientPwd,remoteIp,remoteMac);
                    if(json.indexOf("成功") > -1){
                        //认证成功
                        ClientServerService clientServerService = (ClientServerService)SpringContextUtil.getBean("clientServerService");
                        ServerUserService serverUserService = (ServerUserService)SpringContextUtil.getBean("serverUserService");
                        List<ClientServer> clientServers = clientServerService.findByClientId(clientId);
                        if(clientServers != null){
                            boolean isExist = false; //判断该认证账号是否有该服务
                            for(ClientServer clientServer:clientServers){
                                if(clientServer.getServerId() == serverId){
                                    ServerUser serverUser = serverUserService.findById(serverId);
                                    isExist = true;
                                    json = "{success:true,msg:'" + serverUser.getServerPwd()+ "'}";
                                    break;
                                }
                                if(!isExist){
                                    json = "{success:false,msg:'该认证账号没有该服务权限'}";
                                }
                            }
                        }else{
                            json = "{success:false,msg:'该认证账号没有该服务权限'}";
                        }
                    }else{
                        json = "{success:false,msg:'" + json + "'}";
                        logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份获取服务密码失败");
                    }

                }else{
                    System.out.println("用户名或密码为空. . .");
                    json = "{success:false,msg:'用户名或密码不能为空'}";
                    logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份获取服务密码失败");
                }

            }else {
                System.out.println("获取service错误. . .");
                json = "{success:false,msg:'服务端认证失败'}";

            }
        }else if(command.equals(StaticField.DISCONNECT)){
            String clientId = json_user.get("clientId").toString();
            if(clientId != null && clientId.length() > 0){
                ClientUser client = Service.connectClients.get(Integer.parseInt(clientId));
                LogService logService = (LogService)SpringContextUtil.getBean("logService");
                if(client != null){
                    client.setDisconnectTime(new Date());
                    ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
                    clientUserService.modify(client);
                    if(client != null){
                        client.setDisconnectTime(new Date());
                        String value = null;
                        if(Service.iptables.containsKey(client.getIp())){
                            value = Service.iptables.get(client.getIp());
                        }
                        if(value != null && value.length() > 0){
                            String[] ipPorts = value.split(";");
                            for(int i = 0;i<ipPorts.length;i++){
                                if(ipPorts[i].length() > 0 && ipPorts[i].indexOf("_") > -1){
                                    String cIp = ipPorts[i].split("_")[0];
                                    String tIp = ipPorts[i].split("_")[1];
                                    String port = ipPorts[i].split("_")[2];
                                    logger.info("cIp:" + cIp + ",tIp:" + tIp + ",port:" + port);
                                    while (getStatus(cIp)){
                                        delRule(cIp,tIp,port);
                                    }
                                    Service.iptables.remove(client.getIp());
                                }
                            }
                        } else{
                            logger.info("client:" + client.getIp() + " 不在线");
                            Set<String> keys = Service.iptables.keySet();
                            for(String key:keys){
                                logger.info("key:" + key);
                            }
                        }
                    }else{
                        logger.info("不存在clientId:" + clientId);
                    }
                    Service.connectClients.remove(client.getId());
                    logger.info("终端 " + client.getUserName() + " 已下线");
                    logService.newSysLog("INFO","认证模式","认证成功","在 " + remoteIp + " 以" + client.getUserName() + " 身份下线成功");
                    json = "{success:true,msg:'取消认证成功!'}";
                }
            }
        }else if(command.equals(StaticField.DIR)){
            /*String clientUser = json_user.get("clientUser").toString();
            String clientPwd = json_user.get("clientPwd").toString();
            String serverUser = json_user.get("serverUser").toString();
            clientUser = new String(new BASE64Decoder().decodeBuffer(clientUser));
            if(clientUser != null && clientUser.length() > 0 && serverUser != null && serverUser.length() > 0){
//                XmlOperatorService xmlOperatorService = (XmlOperatorService)SpringContextUtil.getBean("xmlOperatorService");
//                ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
                XmlOperatorService xmlOperatorService = new XmlOperatorServiceImpl();
                json = xmlOperatorService.getUrl(serverUser,clientUser);
            }else {
                json = "认证账号或服务账号错误!";
            }*/
            json = "当前没有该应用";
        }
        return json;

    }


    private String getMacByArp(String ip){
        String mac = null;
        OSInfo osInfo = OSInfo.getOSInfo();
        Proc proc = new Proc();
        if(osInfo.isLinux()){
            if(proc.exec("arp -n " + ip)){
                String output = proc.getOutput();
                StringTokenizer tokenizer = new StringTokenizer(output, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    Matcher mc_ip = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(line);
                    Matcher mc_mac = Pattern.compile("([0-9A-Fa-f]{2})((:|-)[0-9A-Fa-f]{2}){5}").matcher(line);
                    String arp_ip = null, arp_mac = null;
                    if (mc_ip.find()) {
                        arp_ip = mc_ip.group();
                    }
                    if (mc_mac.find()) {
                        arp_mac = mc_mac.group();
                    }
                    if(arp_ip != null && ip.equals(arp_ip)){
                        mac = arp_mac;
                        return mac;
                    }
                }
            }
        }else {
            NicMac nicMac = new NicMac(ip);
            mac = nicMac.GetRemoteMacAddr();
        }
        return mac;
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

    private void delRule(String remoteIp,String localIp,String localPort){
        OSInfo osInfo = OSInfo.getOSInfo();
        Proc proc = new Proc();
        if(osInfo.isLinux()){
            //command:   iptables -D INPUT -s 认证地址 -d 本机 -p tcp -m tcp --dport 端口 -j ACCEPT
            if(localPort == null || localPort.equals("null")){
                proc.exec("iptables -D INPUT -s "+remoteIp +  " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 规则成功");
            }else {
                proc.exec("iptables -D INPUT -s " +remoteIp + " -d " + localIp+ " -p tcp -m tcp --dport " + localPort+ " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
            }
            /*if(proc.exec("iptables -D INPUT -s " +remoteIp + " -d " + localIp+ " -p tcp -m tcp --dport " + localPort+ " -j ACCEPT")){
                String output = proc.getOutput();
                if(output != null && output.length() > 0 ){
                    logger.info(output);
                }
                logger.info("删除 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
            }*/
        }else{
            //用于本机测试
            if(localPort == null || localPort.equals("null")){
                System.out.println("iptables -D INPUT -s " + remoteIp + " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 规则成功");
            }else {
                System.out.println("iptables -D INPUT -s " + remoteIp + " -d " + localIp + " -p tcp -m tcp --dport " + localPort + " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
              
            }
        }
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
