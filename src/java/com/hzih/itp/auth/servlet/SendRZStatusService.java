package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.action.client.ClientAction;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.hzih.itp.auth.utils.StaticField;
import com.hzih.logback.LogLayout;
import com.hzih.logback.entity.LogDataBean;
import com.hzih.logback.filter.MarkerFilter;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-30
 * Time: 上午9:36
 * To change this template use File | Settings | File Templates.
 */
public class SendRZStatusService implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(SendRZStatusService.class);
    private boolean isRunning = false;


    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        isRunning = true;
        while (isRunning){
            System.out.println("开始发送认证状态日志到探针. . .");
            work();
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
            }
        }
    }

    private void work() {
        try{
            if(Service.platformType.equals(StaticField.Platform_Type_EX_ITP) || Service.platformType.equals(StaticField.Platform_Type_IN_STP)){
                Properties properties = null;
                String client = null,server = null;
                boolean status = false;
                try {
                    InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                    properties = new Properties();
                    properties.load(in);
                    if(properties!=null){
                        server = properties.getProperty("serverIp");
                        client = properties.getProperty("clientIp");
                    }
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("查询客户端配置信息失败",e);
                }
                status = ClientAction.connect;
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                LogDataBean logDataBean = new LogDataBean();
                logDataBean.setLogflag(LogDataBean.COMPANY + "-D0402-" + LogDataBean.RZStatus);
                logDataBean.setSourceIp(client);
                logDataBean.setTargetIp(server);
                logDataBean.setResult(status ? 1 : 0);
                logDataBean.setLogTime(sf.format(date));
                logDataBean.setComment(Service.platformType);
                String msg = logDataBean.toRZStatusString();
                LogLayout.info(MarkerFilter.MARKER_NAME_OUT,logger,"itpauth",msg);
            }else if(Service.platformType.equals(StaticField.Platform_Type_EX_STP) || Service.platformType.equals(StaticField.Platform_Type_IN_ITP)){
                Properties properties = null;
                String client = null,server = null;
                boolean status = false;
                try{
                    InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                    properties = new Properties();
                    properties.load(in);
                    if(properties != null && properties.containsKey("listenPort")){
                        server = properties.getProperty("serverIp");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("查询客户端配置信息失败",e);
                }

                ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
                ClientUser clientUser= clientUserService.findByProperty("userName","itp-ex",0);
                if(clientUser == null){
                    clientUser = clientUserService.findByProperty("userName","stp-in",0);
                }
                if(clientUser != null){
                    client = clientUser.getIp();
                    status = getStatus(clientUser.getIp());
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    LogDataBean logDataBean = new LogDataBean();
                    logDataBean.setLogflag(LogDataBean.COMPANY + "-D0402-" + LogDataBean.RZStatus);
                    logDataBean.setSourceIp(client);
                    logDataBean.setTargetIp(server);
                    logDataBean.setResult(status?1:0);
                    logDataBean.setLogTime(sf.format(date));
                    logDataBean.setComment(Service.platformType);
                    String msg = logDataBean.toRZStatusString();
                    LogLayout.info(MarkerFilter.MARKER_NAME_OUT,logger,"itpauth",msg);
                }
            }
        }catch (Exception e){

        }
    }

    private boolean getStatus(String ip){
        OSInfo os = OSInfo.getOSInfo();
        Proc proc = new Proc();
        boolean result = false;
        if(os.isLinux()) {
            if(proc.exec("iptables -S")){
                String output = proc.getOutput();
                StringTokenizer tokenizer = new StringTokenizer(output, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    if(line.indexOf("-A INPUT -s " + ip) > -1){
                        return true;
                    }
                }
            }
        }
        return result;
    }
}
