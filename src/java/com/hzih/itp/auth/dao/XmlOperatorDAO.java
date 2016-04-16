package com.hzih.itp.auth.dao;

import com.inetec.common.config.stp.nodes.SourceFile;
import com.inetec.common.config.stp.nodes.Type;
import com.inetec.common.exception.Ex;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-15
 * Time: 上午10:50
 * To change this template use File | Settings | File Templates.
 */
public interface XmlOperatorDAO {
    public List<Type> getTypes(String xmlType, String appType) throws Ex;
    public SourceFile getSourceFiles(String xmlType, String appName)throws Ex;
    /**
     * 查找所有启动的外网应用
     * @param typeXml
     * @param b
     * @return
     * @throws com.inetec.common.exception.Ex
     */
    public String[] getTypesByActive(String typeXml, boolean b,String protocol) throws Ex;       /**
     * 按类型查找所有应用名
     * @param plugin
     * @param appType
     * @return
     * @throws com.inetec.common.exception.Ex
     */
    public String[] readTypeNameSingle(String plugin, String appType) throws Ex;
}
