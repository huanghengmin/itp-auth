package com.hzih.itp.auth.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-26
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientInfo {

    private String clientName;

    private String serverName;

    private String ip;

    public ClientInfo() {
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


}
