package com.gin.pixivcrawler.service.queryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.SearchQueryDao;
import com.gin.pixivcrawler.entity.SearchKeyword;
import com.gin.pixivcrawler.entity.taskQuery.SearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gin
 * @date 2021/2/15 13:50
 */
@Service
@Slf4j
public class SearchQueryServiceImpl extends ServiceImpl<SearchQueryDao,SearchQuery> implements SearchQueryService {
    @Override
    public SearchQuery findOne() {
        QueryWrapper<SearchQuery> qw = new QueryWrapper<>();
        qw.orderByAsc("timestamp").last("limit 0,1");
        return getOne(qw);
    }

    @Override
    public void saveList(Collection<SearchKeyword> searchKeywords, long uid, int page) {
        List<SearchQuery> list = searchKeywords.stream()
                .map(k -> new SearchQuery(k.getName(),k.getKeywords(), uid, page))
                .collect(Collectors.toList());
        saveBatch(list);
    }

    @Override
    public List<SearchQuery> findAll() {
        QueryWrapper<SearchQuery> qw = new QueryWrapper<>();
        qw.orderByAsc("timestamp");
        return list(qw);
    }

    @Override
    public void delById(String uuid) {
        removeById(uuid);
    }
}
