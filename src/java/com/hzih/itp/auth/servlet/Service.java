package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.util.OSInfo;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
public class Service extends HttpServlet {


    private static Logger logger = Logger.getLogger(Service.class);
    private static boolean isRunPortListen = false;
    private static boolean isRunRZPortListen = false;
    private static boolean isRunCheckAliveService = false;
    private static boolean isRunClientInfoService = false;
    private static boolean isRunSendRZStatus = false;
    private static boolean isRunAuthConnectService = false;
    private static PortListenService portListenService = new PortListenService();

    private static RZPortListenService rzPortListenService = new RZPortListenService();
    private static CheckAliveService checkAliveService = new CheckAliveService();
    private ClientInfoService clientInfoService = new ClientInfoService();
    public static Map<Integer,ClientUser> connectClients ;
    public static Map<String,String> iptables;  //key:remoteIp,value: serverip_serverport;serverip_serverport;
    public static String platformType;
    private SendRZStatusService sendRZStatusService = new SendRZStatusService();
    private AuthConnectService authConnectService = new AuthConnectService();

    public Service() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>ITP Service Page</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white>");
        writer.println("<table border=\"0\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<h1>ITP Service  Status Page</h1>");
        writer.println("<P>Service is running.<P><BR>");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String command = request.getParameter(StaticField.Command);

        /*if(StaticField.SendConfig.equals(command)) {
            Service.configService.query.offer(command);
        } else if(StaticField.AlertConfig.equals(command)) {

        } else if(StaticField.SendChannelTest.equals(command)) {

        }*/

        byte[] data = (command + " is ok!").getBytes();
        response.setContentLength(data.length);
        response.getOutputStream().write(data);

        response.flushBuffer();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void init() {
        //初始化已连接的客户端
        connectClients = new HashMap<Integer, ClientUser>();
        iptables = new HashMap<String, String>();
        platformType = getPlatformType();         //主机名字
//        logger.info("platformName:" + platformType);
        String platformName = null;
        if(StaticField.Platform_Type_EX_ITP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            logger.info("启动 导入前置机 认证端口监听 开始. . .");
            runRZPortListen();
            logger.info("启动 导入前置机 认证端口监听 成功. . .");

            logger.info("启动 导入前置机 监听端口 开始. . .");
            runPortListen();
            logger.info("启动 导入前置机 监听端口 成功. . .");
            logger.info("启动 导入前置机 客户端在线校验 开始. . .");
            runCheckAliveService();
            logger.info("启动 导入前置机 客户端在线校验 成功. . .");
            logger.info("启动 导入前置机 发送认证用户信息到 iptplatform 开始. . .");
            runClientInfoService();
            logger.info("启动 服务器 发送认证用户信息到 iptplatform 成功. . .");
            logger.info("启动 导入前置机 到 单向设备 的认证 开始. . .");
            runAuthConnectService();
            logger.info("启动 导入前置机 到 探针 的认证状态发送 开始. . .");
            runSendRZStatus();
            logger.info("启动 导入前置机 到 探针 的认证状态发送 成功. . .");
        } else if(StaticField.Platform_Type_EX_STP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            logger.info("启动 单向前置机 认证端口监听 开始. . .");
            runRZPortListen();
            logger.info("启动 单向前置机 认证端口监听 成功. . .");

            logger.info("启动 单向前置机 监听端口 开始. . .");
            runPortListen();
            logger.info("启动 单向前置机 监听端口 成功. . .");
            logger.info("启动 单向前置机 客户端在线校验 开始. . .");
            runCheckAliveService();
            logger.info("启动 单向前置机 客户端在线校验 成功. . .");
//            logger.info("启动 单向前置机 到 探针 的认证状态发送 开始. . .");
//            runSendRZStatus();
//            logger.info("启动 单向前置机 到 探针 的认证状态发送 成功. . .");
        } else if(StaticField.Platform_Type_IN_STP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            /*logger.info("启动 服务器 认证端口监听 开始. . .");
            runRZPortListen();
            logger.info("启动 服务器 认证端口监听 成功. . .");

            logger.info("启动 服务端 监听端口 开始. . .");
            runPortListen();
            logger.info("启动 服务端 监听端口 成功. . .");
            logger.info("启动 服务器 客户端在线校验 开始. . .");
            runCheckAliveService();
            logger.info("启动 服务器 客户端在线校验 成功. . .");*/
            logger.info("启动 单向后置机 到 导入服务器 的认证 开始. . .");
            runAuthConnectService();
            logger.info("启动 单向后置机 到 探针 的认证状态发送 开始. . .");
            runSendRZStatus();
            logger.info("启动 单向后置机 到 探针 的认证状态发送 成功. . .");
        } else if(StaticField.Platform_Type_IN_ITP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            logger.info("启动 导入服务器 认证端口监听 开始. . .");
            runRZPortListen();
            logger.info("启动 导入服务器 认证端口监听 成功. . .");

            logger.info("启动 导入服务器 监听端口 开始. . .");
            runPortListen();
            logger.info("启动 导入服务器 监听端口 成功. . .");
            logger.info("启动 导入服务器 客户端在线校验 开始. . .");
            runCheckAliveService();
            logger.info("启动 导入服务器 客户端在线校验 成功. . .");
            logger.info("启动 导入服务器 到 探针 的认证状态发送 开始. . .");
            runSendRZStatus();
            logger.info("启动 导入服务器 到 探针 的认证状态发送 成功. . .");
        } else{
            platformName = StaticField.Auth_type_C;
        }

    }

    private void runSendRZStatus() {
        if(Service.isRunSendRZStatus){
            return;
        }else {
            Thread thread = new Thread(sendRZStatusService);
            thread.start();
            Service.isRunSendRZStatus = true;
        }
    }

    private String getPlatformType() {
        OSInfo os = OSInfo.getOSInfo();
        if(os.isLinux()) {
            return os.getHostName();
        }
//        return StaticField.Platform_Type_EX_STP;
        return "client";
    }

    private void runPortListen(){
        if(Service.isRunPortListen){
            return;
        }else {
            portListenService.init();
            Thread thread = new Thread(portListenService);
            thread.start();
            Service.isRunPortListen = true;
        }
    }

    private void runRZPortListen(){
        if(Service.isRunRZPortListen){
            return;
        }else {
            rzPortListenService.init();
            Thread thread = new Thread(rzPortListenService);
            thread.start();
            Service.isRunRZPortListen = true;
        }
    }

    private void runCheckAliveService(){
        if(Service.isRunCheckAliveService){
            return;
        }else {
            Thread thread = new Thread(checkAliveService);
            thread.start();
            Service.isRunCheckAliveService = true;
        }
    }

    private void runClientInfoService(){
        if(Service.isRunClientInfoService){
            return;
        }else {
            clientInfoService.init();
            Thread thread = new Thread(clientInfoService);
            thread.start();
            Service.isRunClientInfoService = true;
        }
    }

    private void runAuthConnectService(){
        if(Service.isRunAuthConnectService){
            return;
        }else {
            Thread thread = new Thread(authConnectService);
            thread.start();
            Service.isRunAuthConnectService = true;
        }
    }

}
