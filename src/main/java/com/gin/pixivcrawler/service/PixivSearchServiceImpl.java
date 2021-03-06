package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.dao.SearchKeywordDao;
import com.gin.pixivcrawler.entity.SearchKeyword;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_COMMA;

/**
 * @author Gin
 * @date 2021/2/14 17:19
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PixivSearchServiceImpl extends ServiceImpl<SearchKeywordDao, SearchKeyword> implements PixivSearchService {
    private final PixivCookieDao pixivCookieDao;
    private final PixivTagService pixivTagService;


    public PixivSearchServiceImpl(PixivCookieDao pixivCookieDao, PixivTagService pixivTagService) {
        this.pixivCookieDao = pixivCookieDao;
        this.pixivTagService = pixivTagService;
    }

    @Override
    public PixivSearchResults search(SearchKeyword keyword, long uid, int page) {
        return search(keyword.getKeywords(), uid, page);
    }

    @Override
    public PixivSearchResults search(String keyword, long uid, int page) {
        PixivCookie pixivCookie = pixivCookieDao.selectById(uid);
        PixivSearchResults results = PixivPost.search(keyword, page, pixivCookie.getCookie(), false, "all");
        if (results != null) {
            results.getIllustManga().getDetails().forEach(detail -> {
                String translate = pixivTagService.translate(detail.getTagString(), DELIMITER_COMMA);
                detail.setTagsList(Arrays.asList(translate.split(DELIMITER_COMMA)));
            });
            log.info("获得搜索结果 {} 个 关键字 : {}", results.getIllustManga().getTotal(), keyword);
        }
        return results;
    }


    @Override
    public List<SearchKeyword> findAll() {
        return list();
    }

    @Override
    public void saveKeyword(SearchKeyword keyword) {
        saveOrUpdate(keyword);
    }
}
