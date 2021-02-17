package com.gin.pixivcrawler.service.queryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.DownloadQueryDao;
import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/3 14:33
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)

public class DownloadQueryServiceImpl extends ServiceImpl<DownloadQueryDao, DownloadQuery> implements DownloadQueryService {

    @Override
    public boolean saveOne(DownloadQuery entity) {
        return save(entity);
    }

    @Override
    public boolean saveList(Collection<DownloadQuery> entities) {
        return saveBatch(entities);
    }

    @Override
    public DownloadQuery findOne(Serializable id) {
        return getById(id);
    }

    @Override
    public List<DownloadQuery> findList(Collection<Serializable> idCollection) {
        return listByIds(idCollection);
    }

    @Override
    public boolean updateOne(DownloadQuery entity) {
        return updateById(entity);
    }

    @Override
    public boolean deleteByUrl(Collection<String> url) {
        QueryWrapper<DownloadQuery> qw = new QueryWrapper<>();
        qw.in("url", url);
        return remove(qw);
    }

    @Override
    public List<DownloadQuery> findSortedList(int limit, Collection<String> urlNotIn) {
        QueryWrapper<DownloadQuery> qw = new QueryWrapper<>();
        if (urlNotIn != null && urlNotIn.size() > 0) {
            qw.notIn("url", urlNotIn);
        }
        qw.orderByDesc("type", "priority", "uuid");
        qw.last(String.format("limit %d,%d", 0, limit));
        return list(qw);
    }

    @Override
    public void saveOne(String uuid, String path, String fileName, String url, String type, Integer priority) {
        save(new DownloadQuery(uuid, path, fileName, url, type, priority));
    }
}
