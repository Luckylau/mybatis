package com.luckylau.base;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luckylau.model.IncrementId;
import com.luckylau.model.Pager;
import com.luckylau.utils.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Created by luckylau on 2018/5/10 上午10:13
 */
public abstract class BaseService<T extends IncrementId, K extends BaseDao<T>> {
    @Autowired
    protected K baseDao;
    protected Class<T> entityClass;


    public int insert(T entity) {
        notNull(entity, "实体不能为空");
        return baseDao.insert(entity);
    }

    public int insertBatch(Collection<T> entities) {
        notEmpty(entities, "实体列表不能为空");
        return baseDao.insertBatch(entities);
    }

    public int replaceInsert(T entity) {
        notNull(entity, "实体不能为空");
        return baseDao.replaceInsert(entity);
    }

    public int replaceInsertBatch(Collection<T> entities) {
        notEmpty(entities, "实体列表不能为空");
        return baseDao.replaceInsertBatch(entities);
    }

    public int update(T entity) {
        notNull(entity, "实体不能为空");
        notNull(entity.getId(), "实体Id不能为空");
        return baseDao.update(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(Collection<T> entities) {
        notEmpty(entities, "实体列表不能为空");
        int count = 0;
        for (T entity : entities) {
            count += update(entity);
        }
        return count;
    }

    public int updateBatch(T entity, Collection<Long> ids) {
        notNull(entity, "实体不能为空");
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("execute sql " + baseDao.getClass() + ".updateBatch[ids cannot be empty]");
        }
        return baseDao.updateBatch(entity, ids);
    }

    public int updateAll(T entity) {
        notNull(entity, "实体不能为空");
        notNull(entity.getId(), "实体Id不能为空");
        return baseDao.updateAll(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateAllBatch(Collection<T> entities) {
        notEmpty(entities, "实体列表不能为空");
        int count = 0;
        for (T entity : entities) {
            count += updateAll(entity);
        }
        return count;
    }

    public int updateAllBatch(T entity, Collection<Long> ids) {
        notNull(entity, "实体不能为空");
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("execute sql " + baseDao.getClass() + ".updateAllBatch[ids cannot be empty]");
        }
        return baseDao.updateAllBatch(entity, ids);
    }


    public int delete(T entity) {
        notNull(entity, "实体不能为空");
        notNull(entity.getId(), "实体Id不能为空");
        return baseDao.delete(entity);
    }

    public int deleteById(Long id) {
        notNull(id, "Id不能为空");
        return baseDao.deleteById(id);
    }

    public int deleteByIds(Collection<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("execute sql " + baseDao.getClass() + ".deleteByIds[ids cannot be null]");
        }
        return baseDao.deleteByIds(ids);
    }

    public int save(T entity) {
        notNull(entity, "实体不能为空");
        return entity.getId() == null ? baseDao.insert(entity) : baseDao.update(entity);
    }

    public int saveAll(T entity) {
        notNull(entity, "实体不能为空");
        return entity.getId() == null ? baseDao.insert(entity) : baseDao.updateAll(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int saveBatch(Collection<T> entities) {
        int count = 0;
        if (!isEmpty(entities)) {
            for (T entity : entities) {
                count += save(entity);
            }
        }
        return count;
    }

    public T findById(Long id) {
        notNull(id, "Id不能为空");
        return baseDao.findById(id);
    }

    public List<T> findByIds(Collection<Long> ids) {
        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("execute sql " + baseDao.getClass() + ".findByIds[ids cannot be null]");
        }
        return baseDao.findByIds(ids);
    }

    public List<T> findAll() {
        return baseDao.findAll();
    }

    public List<T> find(Condition condition) {
        return find(condition == null ? null : Arrays.asList(condition));
    }

    public PageInfo<T> find(Condition condition, Pager pager) {
        return find(condition == null ? null : Arrays.asList(condition), pager);
    }

    public List<T> find(Collection<Condition> conditions) {
        return baseDao.find(conditions);
    }

    public PageInfo<T> find(Collection<Condition> conditions, Pager pager) {
        dealPager(pager);
        return new PageInfo<>(find(conditions));
    }

    public List<Long> findIds(Condition condition) {
        return findIds(condition == null ? null : Arrays.asList(condition));
    }

    public List<Long> findIds(Collection<Condition> conditions) {
        return baseDao.findIds(conditions);
    }

    public void dealPager(Pager pager) {
        PageHelper.startPage(pager.getPageNum(), pager.getPageSize(), pager.isCount());
        if (!StringUtils.isEmpty(pager.getOrderBy())) {
            PageHelper.orderBy(pager.getOrderBy());
        }
    }

    protected Class<T> getEntityClass() {
        if (this.entityClass == null) {
            entityClass = ClassUtil.getGenericsClass(getClass());
        }
        return this.entityClass;
    }

}
