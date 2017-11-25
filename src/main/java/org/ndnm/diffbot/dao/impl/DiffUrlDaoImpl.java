package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.DiffUrlDao;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.springframework.stereotype.Repository;


@Repository
public class DiffUrlDaoImpl extends AbstractDao<BigInteger, DiffUrl> implements DiffUrlDao {
    private static final String SELECT_ALL_DIFF_URL_QUERY = "SELECT d FROM DiffUrl d where d.active = true";


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public List<DiffUrl> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_DIFF_URL_QUERY)
                .getResultList();
    }


    @Override
    public DiffUrl findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(DiffUrl diffUrl) {
        persist(diffUrl);
    }


    @Override
    public void delete(DiffUrl diffUrl) {
        diffUrl = mergeCheck(diffUrl);
        super.delete(diffUrl);
    }


    @Override
    public void update(DiffUrl diffUrl) {
        super.update(diffUrl);
    }


    private DiffUrl mergeCheck(DiffUrl diffUrl) {
        return getEntityManager().contains(diffUrl) ? diffUrl : getEntityManager().merge(diffUrl);
    }

}
