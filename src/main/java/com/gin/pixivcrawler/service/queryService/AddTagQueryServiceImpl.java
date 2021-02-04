package com.gin.pixivcrawler.service.queryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.AddTagQueryDao;
import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/4 15:37
 */
@Service
@Slf4j
public class AddTagQueryServiceImpl extends ServiceImpl<AddTagQueryDao, AddTagQuery> implements AddTagQueryService {
    @Override
    public boolean saveOne(AddTagQuery entity) {
        return save(entity);
    }

    @Override
    public boolean saveList(Collection<AddTagQuery> entities) {
        return saveBatch(entities);
    }

    @Override
    public AddTagQuery findOne(Serializable id) {
        return getById(id);
    }

    @Override
    public List<AddTagQuery> findList(Collection<Serializable> idCollection) {
        return listByIds(idCollection);
    }

    @Override
    public List<AddTagQuery> findAllNotIn(Collection<Long> pidCollection) {
        QueryWrapper<AddTagQuery> queryWrapper = new QueryWrapper<>();
        if (pidCollection.size() > 0) {
            queryWrapper.notIn("pid", pidCollection);
        }
        return list(queryWrapper);
    }

    @Override
    public boolean delete(Long pid) {
        return removeById(pid);
    }
}
