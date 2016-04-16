package com.hzih.itp.auth.dao.impl;

import com.hzih.itp.auth.dao.XmlOperatorDAO;
import com.hzih.itp.auth.utils.Configuration;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.config.stp.nodes.Plugin;
import com.inetec.common.config.stp.nodes.SourceFile;
import com.inetec.common.config.stp.nodes.Type;
import com.inetec.common.exception.Ex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-15
 * Time: 上午10:51
 * To change this template use File | Settings | File Templates.
 */
public class XmlOperatorDAOImpl implements XmlOperatorDAO {
    @Override
    public List<Type> getTypes(String xmlType, String appType) throws Ex {
        List<Type> types = new ArrayList<Type>();
        Configuration config = null;
        if("internal".equals(xmlType)){
            config = new Configuration(StaticField.INTERNALXML);
            types = config.getTypesList(appType);
        }else if("external".equals(xmlType)){
            config = new Configuration(StaticField.EXTERNALXML);
            types = config.getTypesList(appType);
        }
        return types;
    }
    /**
     * 读取源端文件
     */
    public SourceFile getSourceFiles(String xmlType, String typeName) throws Ex {
        SourceFile sourceFile = null;
        Configuration config = null;
        if("internal".equals(xmlType)){
            config = new Configuration(StaticField.INTERNALXML);
            sourceFile = config.getSourceFile(typeName, Plugin.s_source_plugin);
        }else if("external".equals(xmlType)){
            config = new Configuration(StaticField.EXTERNALXML);
            sourceFile = config.getSourceFile(typeName, Plugin.s_source_plugin);
        }
        return sourceFile;
    }

    @Override
    public String[] getTypesByActive(String typeXml, boolean isActive,String protocol) throws Ex {
        Configuration config = getConfigTypeXmlHead(typeXml);
        return config.getTypeNamesByActive(isActive,protocol);
    }

    @Override
    public String[] readTypeNameSingle(String typeXml, String appType) throws Ex {
        Configuration config = null;
        if("internal".equals(typeXml)){
            config = new Configuration(StaticField.INTERNALXML);
        }else if("external".equals(typeXml)){
            config = new Configuration(StaticField.EXTERNALXML);
        }
        return config.getTypeNamesThisT(appType);
    }

    private Configuration getConfigTypeXmlHead(String typeXml) throws Ex {
        Configuration config = null;
        if("internal".equals(typeXml)){
            config = new Configuration(StaticField.INTERNALXML);
        }else if("external".equals(typeXml)){
            config = new Configuration(StaticField.EXTERNALXML);
        }
        return config;
    }


}
