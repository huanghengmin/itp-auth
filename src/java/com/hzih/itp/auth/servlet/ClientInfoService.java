package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientServer;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientServerService;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.ServerUserService;
import com.hzih.itp.auth.utils.ClientInfo;
import com.hzih.itp.auth.utils.ServiceResponse;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.hzih.itp.auth.utils.StaticField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * 定时发送认证用户信息到itpplatform service
 * User: Administrator
 * Date: 14-6-26
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class ClientInfoService implements Runnable {
    private static Logger logger = Logger.getLogger(ClientInfoService.class);
    private boolean isRunning = false;

    private static String platformUrl;


    public void init(){
        InputStream in = null;
        Properties properties = null;
        try {
            in = new BufferedInputStream(ClientInfoService.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                platformUrl = properties.getProperty("platformUrl");
            }
        } catch (FileNotFoundException e) {
            logger.error("获取配置文件失败",e);
        } catch (IOException e) {
            logger.error("加载配置文件失败",e);
        }
    }
    @Override
    public void run() {
        System.out.println("开始发送客户端信息到itpplatform. . .");
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
        String json = "";
        try{
            ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
            ServerUserService serverUserService = (ServerUserService)SpringContextUtil.getBean("serverUserService");
            ClientServerService clientServerService = (ClientServerService)SpringContextUtil.getBean("clientServerService");
            List<ClientServer> clientServerList= clientServerService.findAll();
            json = "[";
            for(ClientServer clientServer:clientServerList){
                ClientUser clientUser = clientUserService.findById(clientServer.getClientId());
                ServerUser serverUser = serverUserService.findById(clientServer.getServerId());
                json = json + "{clientName:'" + clientUser.getUserName() + "',serverName:'" + serverUser.getServerUser() + "'," +
                        "ip:'" + clientUser.getIp() + "'},";
            }
            json = json + "]";
            if(json.contains(",]")){
                json = json.replaceAll(",\\]","]");
            }
            send(json);
            logger.info("发送认证用户信息成功");
        }catch (Exception e){
            logger.error("发送认证用户信息错误",e);
        }
    }

    private void send(String json) {
        json = new String(new BASE64Encoder().encode(json.getBytes()));
        String[][] params = new String[][] {
                { StaticField.Command, StaticField.SEND_CLIENT_INFO },
                {"clientInfo",json}
        };
        ServiceResponse serviceResponse = callPlatform(params);
        logger.info("code:" + serviceResponse.getCode());
        logger.info("data:" + serviceResponse.getData());
    }

    private ServiceResponse callPlatform(String[][] params) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5 * 1000);
        client.getHttpConnectionManager().getParams().setSoTimeout(5 * 1000);
        if(platformUrl == null){
            init();
        }
        PostMethod post = new PostMethod(platformUrl);
        post.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5 * 1000);
        post.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");

        for (String[] param : params) {
            post.addParameter(param[0], param[1]);
        }

        ServiceResponse response = new ServiceResponse();

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(post);
            //LogLayout.info(logger,"itp","statusCode:" + statusCode);
            response.setCode(statusCode);
            if (statusCode == 200) {
                String data = post.getResponseBodyAsString();
                //LogLayout.info(logger,"itp","data:" + data);
                response.setData(data);
            }
        } catch (Exception e) {
            //LogLayout.error(logger,"itp","访问接口失败", e);
            logger.error("访问接口失败",e);
        }
        return response;
    }

    public static void main(String[] args) throws Exception{
        String json = "[{clientName:'clientztt',serverName:'ftpztt',ip:'192.168.1.128'}]";
        JSONArray array  = JSONArray.fromObject(json);
        for(int i = 0;i<array.size();i++){
            JSONObject jsonObject = array.getJSONObject(i);
            ClientInfo clientInfo = (ClientInfo) JSONObject.toBean(jsonObject, ClientInfo.class);
            String clientName = clientInfo.getClientName().trim();
            String serverName = clientInfo.getServerName().trim();
            System.out.println("clientName:" + clientInfo.getClientName() + ",serverName:" + clientInfo.getServerName() + ",ip:" + clientInfo.getIp());
        }
//        JSONObject jsonObject = array.getJSONObject(0);
//        ClientInfo clientInfo = (ClientInfo) JSONObject.toBean(jsonObject, ClientInfo.class);
    }
}
