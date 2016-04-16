package com.hzih.itp.auth.dao.impl;

import cn.collin.commons.dao.MyDaoSupport;
import com.hzih.itp.auth.dao.FtpUserDao;
import com.hzih.itp.auth.domain.FtpUser;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-19
 * Time: 下午1:28
 * To change this template use File | Settings | File Templates.
 */
public class FtpUserDaoImpl extends MyDaoSupport implements FtpUserDao {
    @Override
    public void setEntityClass() {
        this.entityClass = FtpUser.class;
    }
}
