package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.dao.DiffResultDao;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.service.DiffResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class DiffResultServiceImpl implements DiffResultService {
    private static final Logger LOG = LogManager.getLogger(DiffResultServiceImpl.class);

    private final DiffResultDao dao;


    @Autowired
    public DiffResultServiceImpl(DiffResultDao dao) {
        this.dao = dao;
    }


    @Override
    public DiffResult findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(DiffResult diffResult) {
        LOG.info("Saving DiffResult...");
        dao.save(diffResult);
        LOG.info("Successfully saved DiffResult(id: %d)", diffResult.getId());
    }


    @Override
    public void update(DiffResult diffResult) {
        dao.update(diffResult);
    }


    @Override
    public void delete(DiffResult diffResult) {
        dao.delete(diffResult);
    }

}
