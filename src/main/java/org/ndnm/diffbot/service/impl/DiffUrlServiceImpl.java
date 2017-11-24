package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.DiffUrlDao;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.DiffUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class DiffUrlServiceImpl implements DiffUrlService {
    private final DiffUrlDao dao;


    @Autowired
    public DiffUrlServiceImpl(DiffUrlDao dao) {
        this.dao = dao;
    }


    @Override
    public List<DiffUrl> findAll() {
        return dao.findAll();
    }


    @Override
    public DiffUrl findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(DiffUrl diffUrl) {
        dao.save(diffUrl);
    }


    @Override
    public void delete(DiffUrl diffUrl) {
        dao.delete(diffUrl);
    }


    @Override
    public void update(DiffUrl diffUrl) {
        dao.update(diffUrl);
    }

}
