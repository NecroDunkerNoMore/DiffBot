package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.HtmlSnapshotDao;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.springframework.stereotype.Repository;


@Repository
public class HtmlSnapshotDaoImpl extends AbstractDao<BigInteger, HtmlSnapshot> implements HtmlSnapshotDao {
    private static final String SELECT_ALL_QUERY = "SELECT h FROM HtmlSnapshot h";
    private static final String SELECT_BY_DIFF_ID_FK_QUERY =
            "SELECT h FROM HtmlSnapshot h " +
            "WHERE h.diffUrl.id = :diffUrlId " +
            "and h.captureType = :captureType " +
            "and h.dateCaptured in " +
            "(select max(h.dateCaptured) from HtmlSnapshot)";


    @SuppressWarnings("unchecked")
    @Override
    public List<HtmlSnapshot> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_QUERY)
                .getResultList();
    }


    @SuppressWarnings("unchecked")
    @Override
    public HtmlSnapshot findLatest(DiffUrl diffUrl) {
        List<HtmlSnapshot> htmlSnapshots = (List<HtmlSnapshot>) getEntityManager()
                .createQuery(SELECT_BY_DIFF_ID_FK_QUERY)
                .setParameter("diffUrlId", diffUrl.getId())
                .setParameter("captureType", CaptureType.POST_EVENT)
                .getResultList();

        if (htmlSnapshots.size() == 0) {
            // This will be the case for a DiffUrl that is brand new, so doesn't
            // have a snapshot yet.
            return null;
        }

        return htmlSnapshots.get(0);
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
