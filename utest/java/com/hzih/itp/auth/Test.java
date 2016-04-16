package com.hzih.itp.auth;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-13
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws Exception{
        String test = "file1,file2,file3";
        test = test.replaceAll("file3","");
        test = test.replaceAll(",,",",");
        if(test.lastIndexOf(",") == test.length()-1){
            test = test.substring(0,test.length()-1);
        }
        System.out.println(test);
//        File file = new File("E:\\vftpsite");
//        System.out.println(file.exists());
        /*if(!file.exists()){
            System.out.print(makeDir(file,null));
        }*/
    }

    private static boolean makeDir(File dir,String ftpUser) throws IOException {
        boolean flag = false;
        if(! dir.getParentFile().exists()) {
            flag = makeDir(dir.getParentFile(),ftpUser);
            if(!flag){
                return flag;
            }
        }
        flag = dir.mkdir();
        if(flag){
//            Runtime.getRuntime().exec("chown " + ftpUser +":" + ftpUser + " " + dir.getParentFile().getAbsolutePath());
//            logger.info("chown " + ftpUser +":" + ftpUser + " " + dir.getParentFile().getAbsolutePath());
        }
        return flag;
    }
}
