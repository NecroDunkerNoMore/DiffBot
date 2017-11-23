package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.HtmlSnapshotDao;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.springframework.stereotype.Repository;


@Repository
public class HtmlSnapshotDaoImpl extends AbstractDao<BigInteger, HtmlSnapshot> implements HtmlSnapshotDao {
    private static final String SELECT_ALL_QUERY = "SELECT h FROM HtmlSnapshot h";

    @SuppressWarnings("unchecked")
    @Override
    public List<HtmlSnapshot> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_QUERY)
                .getResultList();
    }


    @Override
    public HtmlSnapshot findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(HtmlSnapshot htmlSnapshot) {
        persist(htmlSnapshot);
    }


    @Override
    public void delete(HtmlSnapshot htmlSnapshot) {
        htmlSnapshot = getEntityManager().contains(htmlSnapshot) ? htmlSnapshot : getEntityManager().merge(htmlSnapshot);
        super.delete(htmlSnapshot);
    }


    @Override
    public void update(HtmlSnapshot htmlSnapshot) {
        super.update(htmlSnapshot);
    }

}
