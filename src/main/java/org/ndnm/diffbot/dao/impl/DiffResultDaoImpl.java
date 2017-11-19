package org.ndnm.diffbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.ndnm.diffbot.dao.DiffResultDao;
import org.ndnm.diffbot.model.DiffResult;
import org.springframework.stereotype.Repository;


@Repository
public class DiffResultDaoImpl extends AbstractDao<BigInteger, DiffResult> implements DiffResultDao {
    private static final String TARGET_DIFF_ID = "targetDiffId";
    private static final String SELECT_BY_TARGET_ID = String.format("select d from DiffResult d where target_postable_id = :%s", TARGET_DIFF_ID);


    @Override
    public DiffResult findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(DiffResult diffResult) {
        persist(diffResult);
    }


    @Override
    public void update(DiffResult diffResult) {
        super.update(diffResult);
    }


    @Override
    public void delete(DiffResult diffResult) {
        diffResult = getEntityManager().contains(diffResult) ? diffResult : getEntityManager().merge(diffResult);
        super.delete(diffResult);
    }


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public DiffResult findByTargetCommentId(String targetCommentId) {
        List<DiffResult> diffResultList =  getEntityManager()
                .createQuery(SELECT_BY_TARGET_ID)
                .setParameter(TARGET_DIFF_ID, targetCommentId)
                .getResultList();

        if (diffResultList == null || diffResultList.size() < 1) {
            return null;
        }

        return diffResultList.get(0);
    }


    @Override
    public boolean existsByTargetCommentId(String targetCommentId) {
        DiffResult diffResult = findByTargetCommentId(targetCommentId);
        return diffResult != null;
    }

}
