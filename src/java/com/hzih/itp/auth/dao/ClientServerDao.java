package com.hzih.itp.auth.dao;

import cn.collin.commons.dao.BaseDao;
import com.hzih.itp.auth.domain.ClientServer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public interface ClientServerDao extends BaseDao {

    List<ClientServer> findByProperty(String propertyName,String propertyValue,int option);
    List<ClientServer> findByProperty(String propertyName,int propertyValue,int option);


    ClientServer findById(int id);
}
