package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.RedditTimeServiceDao;
import org.ndnm.diffbot.model.RedditPollingTime;
import org.ndnm.diffbot.service.RedditTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RedditTimeServiceImpl implements RedditTimeService {
    final RedditTimeServiceDao dao;


    @Autowired
    public RedditTimeServiceImpl(RedditTimeServiceDao dao) {
        this.dao = dao;
    }


    @Override
    public RedditPollingTime getLastPollingTime() {
        return dao.getLastRedditPollingTime();
    }


    @Override
    public RedditPollingTime findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(RedditPollingTime urlPollingTime) {
        dao.save(urlPollingTime);
    }


    @Override
    public void delete(RedditPollingTime urlPollingTime) {
        dao.delete(urlPollingTime);
    }


    @Override
    public void update(RedditPollingTime urlPollingTime) {
        dao.update(urlPollingTime);
    }

}
