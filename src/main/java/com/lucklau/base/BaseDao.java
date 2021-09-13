package com.lucklau.base;

import com.lucklau.model.IncrementId;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface BaseDao<T extends IncrementId> {
    /**
     * @param entity 实例
     * @return 插入数量
     */
    int insert(T entity);

    /**
     * @param entities 批量插入实例
     * @return 插入数量
     */
    int insertBatch(@Param("entities") Collection<T> entities);

    /**
     * @param entity 实例
     * @return 插入数量
     */
    int replaceInsert(T entity);

    /**
     * @param entities 替代插入实例
     * @return 插入数量
     */
    int replaceInsertBatch(@Param("entities") Collection<T> entities);

    /**
     * @param entity 更新实例
     * @return 更新数量
     */
    int update(T entity);

    /**
     * @param entity 批量更新实例
     * @param ids    批量更新id
     * @return 更新数量
     */
    int updateBatch(@Param("entity") T entity, @Param("ids") Collection<Long> ids);

    /**
     * @param entity 更新所有字段的实例
     * @return 更新数量
     */
    int updateAll(T entity);

    /**
     * @param entity 批量更新所有字段的实例
     * @param ids    批量更新id
     * @return 更新数量
     */
    int updateAllBatch(@Param("entity") T entity, @Param("ids") Collection<Long> ids);

    /**
     * @param entity 删除实例
     * @return 删除数量
     */
    int delete(T entity);

    /**
     * @param id 删除id
     * @return 删除数量
     */
    int deleteById(Long id);

    /**
     * @param ids 批量删除id
     * @return 删除数量
     */
    int deleteByIds(@Param("ids") Collection<Long> ids);

    /**
     * @param id 查找id
     * @return 实例
     */
    T findById(Long id);

    /**
     * @param ids 批量查找id
     * @return 实例列表
     */
    List<T> findByIds(@Param("ids") Collection<Long> ids);

    /**
     * @param conditions 根据条件查找
     * @return 实例列表
     */
    List<T> find(@Param("conditions") Collection<Condition> conditions);

    /**
     * @param conditions 根据条件查找
     * @return id列表
     */
    List<Long> findIds(@Param("conditions") Collection<Condition> conditions);

    /**
     * @return 所有实例
     */
    List<T> findAll();
}
