package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;

import org.ndnm.diffbot.dao.RedditUserDao;
import org.ndnm.diffbot.model.RedditUser;
import org.ndnm.diffbot.service.RedditUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RedditRedditUserServiceImpl implements RedditUserService {
    private RedditUserDao dao;


    @Autowired
    public RedditRedditUserServiceImpl(RedditUserDao dao) {
        this.dao = dao;
    }


    @Override
    public boolean isUserBlacklisted(String username) {
        return dao.isUserBlacklisted(username);
    }


    @Override
    public RedditUser getRedditUserbyUsername(String username) {
        return dao.getRedditUserbyUsername(username);
    }


    @Override
    public RedditUser findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(RedditUser user) {
        dao.save(user);
    }


    @Override
    public void delete(RedditUser user) {
        dao.delete(user);
    }


    @Override
    public void update(RedditUser redditUser) {
        dao.update(redditUser);
    }

}
