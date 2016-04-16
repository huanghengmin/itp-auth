package com.hzih.itp.auth.utils;

import com.inetec.common.config.stp.nodes.Plugin;
import com.inetec.common.config.stp.nodes.SourceFile;
import com.inetec.common.config.stp.nodes.Type;
import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configuration {
    private static Logger logger = Logger.getLogger(Configuration.class);
    private Document document;
    public String confPath;

    public Configuration(Document doc) {
        this.document = doc;
    }

    public Configuration(String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(path);
        } catch (DocumentException e) {
            logger.error( e.getMessage(),e);
        }
    }

    public Configuration(InputStream is, String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            logger.error( e.getMessage(),e);
        }
    }

    public List<Type> getTypesList(String appType) throws Ex{
        List<Type> typeLists = new ArrayList<Type>();
        List types = document.selectNodes("/configuration/system/ichange/types/type[@apptype='" + appType + "']");
        if(types.size() >0){
            for( Iterator it = types.iterator();it.hasNext();){
                Element type = (Element)it.next();
                String appName = type.attribute("value").getText();
                Type t = getType(appName);
                typeLists.add(t);
            }
        }
        return typeLists;
    }

    public Type getType(String appName) throws Ex {
        Element typeNode = (Element) document.selectSingleNode("/configuration/system/ichange/types/type[@value='" + appName + "']");
        if (typeNode != null) {
            Type type = new Type();
            type.setTypeName(typeNode.attribute("value").getText());
            type.setStatus(typeNode.attribute("status").getText() != null ? typeNode.attribute("status").getText() : "0");
            type.setDescription(typeNode.attribute("desc") != null ? typeNode.attribute("desc").getText() : "");
            type.setAppType(typeNode.attribute("apptype") != null ? typeNode.attribute("apptype").getText() : "");
            type.setLocal(typeNode.element("islocal") != null ? typeNode.element("islocal").getText() : "false");
            type.setActive(typeNode.element("isactive") != null ? typeNode.element("isactive").getText() : "false");
            type.setAllow(typeNode.element("isallow") != null ? typeNode.element("isallow").getText() : "false");
            type.setDataPath(typeNode.element("datapath") != null ? typeNode.element("datapath").getText() : "");
            type.setDeleteFile(typeNode.element("deletefile") != null ? typeNode.element("deletefile").getText() : "false");
            type.setRecover(typeNode.element("isrecover") != null ? typeNode.element("isrecover").getText() : "false");
            type.setFilter(typeNode.element("isfilter") != null ? typeNode.element("isfilter").getText() : "false");
            type.setVirusScan(typeNode.element("isvirusscan") != null ? typeNode.element("isvirusscan").getText() : "false");
            type.setInfoLevel(typeNode.element("infolevel") != null ? typeNode.element("infolevel").getText() : "0");
            type.setSecurityFlag(typeNode.element("securityFlag") != null ? typeNode.element("securityFlag").getText() : "");
            type.setSpeed(String.valueOf(Integer.parseInt(typeNode.element("speed") != null ? typeNode.element("speed").getText() : "0")));
            type.setChannel(typeNode.element("channel") != null ? typeNode.element("channel").getText() : "");
            type.setChannelPort(typeNode.element("channelport") != null ? typeNode.element("channelport").getText() : "");
            return type;
        }
        return null;
    }

    public SourceFile getSourceFile(String appName, String pluginType) throws Ex {
        if (appName == null)
            throw new Ex().set(E.E_NullPointer, E.KEY_NULLPOINTER, "appName 应用名为空");
        if (pluginType == null)
            throw new Ex().set(E.E_NullPointer, E.KEY_NULLPOINTER, "pluginType");
        Element file = null;
        if (pluginType.equals(Plugin.s_source_plugin))
            file = (Element) document.selectSingleNode("/configuration/system/ichange/types/type[@value='" + appName + "']/plugin/sourceplugin/sourcefile");
        if (file != null) {
            SourceFile sourceFile = new SourceFile();
            sourceFile.setServerAddress(file.element("serverAddress").getText());
            sourceFile.setPort(file.element("port").getText());
            sourceFile.setCharset(file.element("charset").getText());
            sourceFile.setDeletefile(file.element("deletefile").getText());
            sourceFile.setDir(file.element("dir").getText());
            sourceFile.setFiltertypes(file.element("filtertypes").getText());
            sourceFile.setInterval(file.element("interval").getText());
            sourceFile.setIsincludesubdir(file.element("isincludesubdir").getText());
            sourceFile.setIstwoway(file.element("istwoway").getText());
            sourceFile.setNotfiltertypes(file.element("notfiltertypes").getText());
            sourceFile.setUserName(file.element("userName").getText());
            sourceFile.setPassword(file.element("password").getText());
            sourceFile.setProtocol(file.element("protocol").getText());
            sourceFile.setFileListSize(file.element("filelistsize").getText());
            sourceFile.setPacketSize(file.element("packetsize").getText());
            sourceFile.setThreads(file.element("threads").getText());
            return sourceFile;
        }
        return null;
    }

    public String[] getTypeNamesByActive(boolean active,String protocol) throws Ex {
        List typeNodes = document.selectNodes("/configuration/system/ichange/types/type");
        if (typeNodes != null) {
            List types = new ArrayList();
            for (Iterator it = typeNodes.iterator(); it.hasNext();) {
                Element tp = (Element) it.next();
                if(Boolean.valueOf(tp.element("isactive").getText())==active && ((Element)tp.selectSingleNode("plugin/sourceplugin/sourcefile")).element("protocol").getText().toLowerCase().equals(protocol.toLowerCase())) {
                    types.add(tp.attribute("value").getText());
                }
            }
            return (String[]) types.toArray(new String[types.size()]);
        } else {
            throw new Ex().set(E.E_NullPointer, E.KEY_NULLPOINTER, "应用为空");
        }
    }

    public String[] getTypeNamesThisT(String appType) throws Ex {
        List<Element> typeNodes = document.selectNodes("/configuration/system/ichange/types/type[@apptype='"+appType+"']");
        if (typeNodes != null) {
            List<String> types = new ArrayList<String>();
            for (Iterator<Element> it = typeNodes.iterator(); it.hasNext();) {
                Element tp = (Element) it.next();
                Element sourceplugin = (Element)tp.selectSingleNode("plugin/sourceplugin");
                Element targetplugin = (Element)tp.selectSingleNode("plugin/targetplugin");
                if(targetplugin != null){
                    types.add(tp.attribute("value").getText());
                }
            }
            return (String[]) types.toArray(new String[types.size()]);
        } else {
            throw new Ex().set(E.E_NullPointer, E.KEY_NULLPOINTER, "应用为空");
        }
    }


}