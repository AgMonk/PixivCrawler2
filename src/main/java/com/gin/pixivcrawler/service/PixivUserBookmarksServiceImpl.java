package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

/**
 * @author bx002
 * @date 2021/2/3 11:06
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
public class PixivUserBookmarksServiceImpl implements PixivUserBookmarksService {
    private final PixivCookieDao pixivCookieDao;

    public PixivUserBookmarksServiceImpl(PixivCookieDao pixivCookieDao) {
        this.pixivCookieDao = pixivCookieDao;
    }

    @Async("bookmarksExecutor")
    @Override
    public Future<PixivBookmarks> get(long userId, String tag, int offset, int limit) {
        QueryWrapper<PixivCookie> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        PixivCookie pixivCookie = pixivCookieDao.selectOne(qw);
        return AsyncResult.forValue(PixivPost.getBookmarks(pixivCookie.getCookie(),pixivCookie.getUserId(),offset,limit,tag));
    }
}
