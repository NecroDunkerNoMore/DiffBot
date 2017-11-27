package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.AuthPollingTimeDao;
import org.ndnm.diffbot.model.AuthPollingTime;
import org.ndnm.diffbot.service.AuthTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthTimeServiceImpl implements AuthTimeService {
    final AuthPollingTimeDao dao;


    @Autowired
    public AuthTimeServiceImpl(AuthPollingTimeDao dao) {
        this.dao = dao;
    }


    @Override
    public AuthPollingTime getLastSuccessfulAuth() {
        return dao.getLastSuccessfulAuth();
    }


    @Override
    public AuthPollingTime findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(AuthPollingTime authPollingTime) {
        dao.save(authPollingTime);
    }


    @Override
    public void delete(AuthPollingTime authPollingTime) {
        dao.delete(authPollingTime);
    }


    @Override
    public void update(AuthPollingTime authPollingTime) {
        dao.update(authPollingTime);
    }

}
