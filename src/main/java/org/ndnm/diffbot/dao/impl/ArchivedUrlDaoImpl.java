package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.ArchivedUrlDao;
import org.ndnm.diffbot.model.ArchivedUrl;
import org.springframework.stereotype.Repository;


@Repository
public class ArchivedUrlDaoImpl extends AbstractDao<BigInteger, ArchivedUrl> implements ArchivedUrlDao {
    private static final String SELECT_ALL_QUERY = "SELECT a FROM ArchivedUrl a ORDER BY A.dateArchived desc";
    private static final String SELECT_BY_DIFF_URL_FK_QUERY = "SELECT a FROM ArchivedUrl a WHERE a.diffResult.id = :diffResultId ORDER BY a.dateArchived desc";


    @SuppressWarnings("unchecked")
    @Override
    public List<ArchivedUrl> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_QUERY)
                .getResultList();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<ArchivedUrl> findAllByDiffUrlId(BigInteger diffResultId) {
        return getEntityManager()
                .createQuery(SELECT_BY_DIFF_URL_FK_QUERY)
                .setParameter("diffResultId", diffResultId)
                .getResultList();
    }


    @Override
    public ArchivedUrl findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(ArchivedUrl archivedUrl) {
        super.persist(archivedUrl);
    }


    @Override
    public void delete(ArchivedUrl archivedUrl) {
        super.delete(archivedUrl);
    }


    @Override
    public void update(ArchivedUrl archivedUrl) {
        super.update(archivedUrl);
    }
}
