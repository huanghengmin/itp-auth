package com.hzih.itp.auth.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-12
 * Time: 下午6:25
 * To change this template use File | Settings | File Templates.
 */
public class ClientServer {
    private int id;
    private int clientId;//认证账号
    private int serverId;//服务账号
    private String appName;//应用名
    private String path;//路径

    public ClientServer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
