package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.LogService;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-9
 * Time: 下午5:11
 * 遍历在线终端信息,若当前时间-登陆时间>8分钟,则判断用户下线,并保存用户登陆时长
 */
public class CheckAliveService implements Runnable {
    private static Logger logger = Logger.getLogger(CheckAliveService.class);
    private boolean isRunning = false;

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("开始遍历认证成功的客户端. . .");
        isRunning = true;
        while (isRunning){
            work();
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
            }
        }
    }

    private void work() {
        try{
            Set<Integer> clientIds = Service.connectClients.keySet();
            for(Integer clientId:clientIds){
                ClientUser client = Service.connectClients.get(clientId);
                if(client != null){
                    if(new Date().getTime()-client.getDisconnectTime().getTime() > 10*60*1000){
                        LogService logService = (LogService) SpringContextUtil.getBean("logService");
                        logService.newSysLog("ERROR","认证模式","认证超时","在 " + client.getIp() + " 以" + client.getUserName() + " 身份认证超时");

                        ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
                        clientUserService.modify(client);
                        /*Set<ServerUser> servers =  client.getServers();
                        Map<String,String> ipPorts = new HashMap<String, String>();
                        for(ServerUser serverUser:servers){
                            if(!ipPorts.containsKey(serverUser.getIpPort())){
                                ipPorts.put(serverUser.getIpPort(),serverUser.getIpPort());
                                delRule(client.getIp(), serverUser.getIpPort().split(":")[0], serverUser.getIpPort().split(":")[1]);
                                if(serverUser.getServerType() == 0){
                                    delRule(client.getIp(), serverUser.getIpPort().split(":")[0],"20");      //ftp-data
                                }else if(serverUser.getServerType() == 1){
                                    delRule(client.getIp(), serverUser.getIpPort().split(":")[0],"137");      //ftp-data
                                    delRule(client.getIp(), serverUser.getIpPort().split(":")[0],"138");      //ftp-data
                                    delRule(client.getIp(), serverUser.getIpPort().split(":")[0],"139");      //ftp-data

                                }
                            }
                        }*/
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
                                    delRule(cIp,tIp,port);
                                }
                            }
                        }
                        Service.iptables.remove(client.getIp());
                        Service.connectClients.remove(clientId);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
        }else{
            //用于本地测试
            if(localPort == null || localPort.equals("null")){
                System.out.println("iptables -D INPUT -s "+remoteIp +  " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 规则成功");
            }else {
                System.out.println("iptables -D INPUT -s " +remoteIp + " -d " + localIp+ " -p tcp -m tcp --dport " + localPort+ " -j ACCEPT");
                logger.info("删除 源IP " + remoteIp + " 目标IP " +localIp + " 目标端口 " + localPort + " 规则成功");
            }
        }
    }
}
