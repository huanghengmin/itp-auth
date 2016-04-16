package com.hzih.itp.auth.dao.impl;

import cn.collin.commons.dao.MyDaoSupport;
import com.hzih.itp.auth.dao.ClientServerDao;
import com.hzih.itp.auth.domain.ClientServer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-12
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class ClientServerDaoImpl extends MyDaoSupport implements ClientServerDao {

    @Override
    public void setEntityClass() {
        this.entityClass = ClientServer.class;
    }

    @Override
    public List<ClientServer> findByProperty(String propertyName, String propertyValue, int option) {
        String hql = new String(" from ClientServer");
        List<ClientServer> clientServers = null;
        if(propertyName != null && propertyName.length() > 0){
            if(option == 0){
                hql += " where " + propertyName + " = '" + propertyValue + "'";
            }else {
                hql += " where " + propertyName + " like '%" + propertyValue + "%'";
            }
            clientServers = getHibernateTemplate().find(hql);
        }
        if(clientServers != null && clientServers.size() > 0 ){
            return clientServers;
        }else {
            return null;
        }

    }

    @Override
    public List<ClientServer> findByProperty(String propertyName, int propertyValue, int option) {
        String hql = new String(" from ClientServer");
        List<ClientServer> clientServers = null;
        if(propertyName != null && propertyName.length() > 0){
            if(option == 0){
                hql += " where " + propertyName + " = " + propertyValue + "";
            }
            clientServers = getHibernateTemplate().find(hql);
        }
        if(clientServers != null && clientServers.size() > 0 ){
            return clientServers;
        }else {
            return null;
        }
    }

    @Override
    public ClientServer findById(int id) {
        String hql = new String(" from ClientServer where id = " + id);
        List<ClientServer> clientServers = null;
        clientServers = getHibernateTemplate().find(hql);
        if(clientServers != null && clientServers.size() > 0 ){
            return clientServers.get(0);
        }else {
            return null;
        }
    }
}
