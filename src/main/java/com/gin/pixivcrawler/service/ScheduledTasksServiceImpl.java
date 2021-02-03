package com.gin.pixivcrawler.service;

import com.alibaba.druid.sql.visitor.functions.If;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivDetailBase;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author bx002
 * @date 2021/2/3 11:28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
public class ScheduledTasksServiceImpl implements ScheduledTasksService {
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivUserBookmarksService pixivUserBookmarksService;

    public ScheduledTasksServiceImpl(PixivIllustDetailService pixivIllustDetailService, PixivUserBookmarksService pixivUserBookmarksService) {
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivUserBookmarksService = pixivUserBookmarksService;
    }

    @Override
    public void downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException {
        int userId = 57680761;
        String tag = "未分類";
        PixivBookmarks bookmarks = pixivUserBookmarksService.get(userId, tag, 0, 5).get(60, TimeUnit.SECONDS);
        if (bookmarks==null) {
            log.warn("未获取到收藏数据 userID = {}",userId);
            return ;
        }
        log.info("用户 userID = {} 收藏中 tag:{} 下总计有作品 {} 个",userId,tag,bookmarks.getTotal());
        List<Long> pidList = bookmarks.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList());
        List<Future<PixivIllustDetail>> tasks = new ArrayList<>();
        pidList.forEach(pid -> tasks.add(pixivIllustDetailService.getDetail(pid)));
        for (Future<PixivIllustDetail> task : tasks) {
            PixivIllustDetail detail = task.get(60, TimeUnit.SECONDS);
            detail.getUrlList().forEach(System.err::println);
        }
    }
}
