package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.action.client.ClientAction;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-7-7
 * Time: 上午9:19
 * To change this template use File | Settings | File Templates.
 */
public class AuthConnectService implements Runnable {
    private static Logger logger = Logger.getLogger(AuthConnectService.class);
    private boolean isRunning = false;

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("开始连接. . .");
        isRunning = false;
        while (!isRunning){
            try {
                String result = work();
                if(result != null && result.indexOf("false") > -1){
                    isRunning = false;
                    logger.info("启动 导入前置机 到 单向设备 的认证 失败. . .");
                }else{
                    isRunning = true;
                    logger.info("启动 导入前置机 到 单向设备 的认证 成功. . .");

                }
            } catch (Exception e) {
                logger.error("启动 导入前置机 到 单向设备 的认证 失败. . .",e);
            }
            try {
                Thread.sleep(1000*60*1);
            } catch (InterruptedException e) {
            }
        }
    }

    private String work() throws Exception{
        ClientAction clientAction = new ClientAction();
        String result = clientAction.authconnect();
        logger.info(result);
        return result;
    }
}
