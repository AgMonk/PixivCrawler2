package com.gin.pixivcrawler.service.queryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.DetailQueryDao;
import com.gin.pixivcrawler.entity.taskQuery.DetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bx002
 * @date 2021/2/5 11:36
 */
@Service
@Slf4j
public class DetailQueryServiceImpl extends ServiceImpl<DetailQueryDao, DetailQuery> implements DetailQueryService {
    @Override
    public boolean deleteById(Serializable id) {
        return removeById(id);
    }

    @Override
    public boolean saveOne(DetailQuery entity) {
        return save(entity);
    }

    @Override
    public boolean saveList(Collection<DetailQuery> entities) {
        if (entities.size() == 0) {
            return true;
        }
        log.info("添加 {}个详情队列", entities.size());
        List<DetailQuery> oldDetailQuery = listByIds(entities.stream().map(DetailQuery::getPid).collect(Collectors.toList()));
        entities.removeAll(oldDetailQuery);
        return saveBatch(entities);
    }

    @Override
    public DetailQuery findOne(long id) {
        return getById(id);
    }

    @Override
    public List<DetailQuery> findList(Collection<Long> idCollection) {
        if (idCollection.size() == 0) {
            return new ArrayList<>();
        }
        return listByIds(idCollection);
    }

    @Override
    public List<DetailQuery> findSortedList(int limit, Collection<Long> pidNotIn) {
        QueryWrapper<DetailQuery> qw = new QueryWrapper<>();
        if (pidNotIn != null && pidNotIn.size() > 0) {
            qw.notIn("pid", pidNotIn);
        }
        qw.orderByDesc("type", "priority", "callback", "pid");
        qw.last(String.format("limit %d,%d", 0, limit));
        return list(qw);
    }
}
