package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.UrlPollingTimeDao;
import org.ndnm.diffbot.model.UrlPollingTime;
import org.ndnm.diffbot.service.UrlPollingTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UrlPollingTimeServiceImpl implements UrlPollingTimeService {
    private final UrlPollingTimeDao dao;


    @Autowired
    public UrlPollingTimeServiceImpl(UrlPollingTimeDao dao) {
        this.dao = dao;
    }


    @Override
    public UrlPollingTime getLastPollingTime() {
        return dao.getUrlPollingTime();
    }


    @Override
    public UrlPollingTime findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(UrlPollingTime urlPollingTime) {
        dao.save(urlPollingTime);
    }


    @Override
    public void delete(UrlPollingTime urlPollingTime) {
        dao.delete(urlPollingTime);
    }


    @Override
    public void update(UrlPollingTime urlPollingTime) {
        dao.update(urlPollingTime);
    }

}
