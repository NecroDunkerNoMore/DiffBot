package org.ndnm.diffbot.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.HtmlSnapshotDao;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class HtmlSnapshotServiceImpl implements HtmlSnapshotService {
    private final HtmlSnapshotDao dao;


    @Autowired
    public HtmlSnapshotServiceImpl(HtmlSnapshotDao dao) {
        this.dao = dao;
    }


    @Override
    public List<HtmlSnapshot> findAll() {
        return dao.findAll();
    }


    @Override
    public HtmlSnapshot findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(HtmlSnapshot htmlSnapshot) {
        dao.save(htmlSnapshot);
    }


    @Override
    public void delete(HtmlSnapshot htmlSnapshot) {
        dao.delete(htmlSnapshot);
    }


    @Override
    public void update(HtmlSnapshot htmlSnapshot) {
        dao.update(htmlSnapshot);
    }
}
