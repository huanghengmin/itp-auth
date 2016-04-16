package com.hzih.itp.auth.service;

import com.hzih.itp.auth.domain.ClientServer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-12
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public interface ClientServerService {
    public void add(ClientServer clientServer);

    public void remove(int id);
    public List<ClientServer> findByClientId(int clientId);

    public ClientServer findById(int id);

    public List<ClientServer> findAll();

    public List<ClientServer> findByServerId(int serverId);
}
