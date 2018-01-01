package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.ArchivedUrlDao;
import org.ndnm.diffbot.model.ArchivedUrl;
import org.ndnm.diffbot.service.ArchivedUrlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ArchivedUrlServiceImpl implements ArchivedUrlService {
    private ArchivedUrlDao dao;


    public ArchivedUrlServiceImpl(ArchivedUrlDao dao) {
        this.dao = dao;
    }


    @Override
    public List<ArchivedUrl> findAll() {
        return dao.findAll();
    }


    @Override
    public List<ArchivedUrl> findAllByDiffUrlId(BigInteger diffUrlId) {
        return dao.findAllByDiffUrlId(diffUrlId);
    }


    @Override
    public ArchivedUrl findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(ArchivedUrl archivedUrl) {
        dao.save(archivedUrl);
    }


    @Override
    public void delete(ArchivedUrl archivedUrl) {
        dao.delete(archivedUrl);
    }


    @Override
    public void update(ArchivedUrl archivedUrl) {
        dao.update(archivedUrl);
    }
}
