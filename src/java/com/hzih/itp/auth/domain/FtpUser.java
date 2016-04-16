package com.hzih.itp.auth.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-19
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public class FtpUser {
    private String name;
    private String passwd;

    public FtpUser() {
    }

    public FtpUser(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
