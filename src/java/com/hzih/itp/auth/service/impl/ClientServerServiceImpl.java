package com.hzih.itp.auth.service.impl;

import com.hzih.itp.auth.dao.ClientServerDao;
import com.hzih.itp.auth.domain.ClientServer;
import com.hzih.itp.auth.service.ClientServerService;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-12
 * Time: 下午6:45
 * To change this template use File | Settings | File Templates.
 */
public class ClientServerServiceImpl implements ClientServerService {
    private static Logger logger = Logger.getLogger(ClientServerServiceImpl.class);

    private ClientServerDao clientServerDao ;

    public ClientServerDao getClientServerDao() {
        return clientServerDao;
    }

    public void setClientServerDao(ClientServerDao clientServerDao) {
        this.clientServerDao = clientServerDao;
    }

    @Override
    public void add(ClientServer clientServer) {
        clientServerDao.create(clientServer);
    }

    @Override
    public void remove(int id) {
        clientServerDao.delete(id);
    }

    @Override
    public List<ClientServer> findByClientId(int clientId) {
        return clientServerDao.findByProperty("clientId",clientId,0);
    }

    @Override
    public ClientServer findById(int id) {
        return clientServerDao.findById(id);
    }

    @Override
    public List<ClientServer> findAll() {
        return clientServerDao.findAll();
    }

    @Override
    public List<ClientServer> findByServerId(int serverId) {
        return clientServerDao.findByProperty("serverId",serverId,0);
    }
}
