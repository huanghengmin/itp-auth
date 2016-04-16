package com.hzih.itp.auth.service;

import com.inetec.common.exception.Ex;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-15
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public interface XmlOperatorService {
    /**
     * 根据服务账号和认证账号查询路径
     * @param serverUser
     * @param clientUser
     * @return
     */
    public String getUrl(String serverUser,String clientUser);
    /**
     * 根据服务账号和认证账号查询路径
     * @param serverUser
     * @param clientUser
     * @return
     */
    public String getUrl(String serverUser,String serverIpPort,String clientUser);

    public int getPort(String serverUser);


    public String readTypeNameKeyValue(String plugin, String appType,String protocol) throws Ex;
}
