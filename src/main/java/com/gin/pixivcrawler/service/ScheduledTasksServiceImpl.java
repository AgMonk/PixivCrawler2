package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
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
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;

/**
 * @author bx002
 * @date 2021/2/3 11:28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ScheduledTasksServiceImpl implements ScheduledTasksService {
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivUserBookmarksService pixivUserBookmarksService;
    private final DownloadQueryService downloadQueryService;

    public ScheduledTasksServiceImpl(PixivIllustDetailService pixivIllustDetailService,
                                     PixivUserBookmarksService pixivUserBookmarksService,
                                     DownloadQueryService downloadQueryService) {
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivUserBookmarksService = pixivUserBookmarksService;
        this.downloadQueryService = downloadQueryService;
    }

    /**
     * 请求未分类作品，添加TAG，添加到下载队列
     *
     * @author bx002
     * @date 2021/2/3 17:21
     */
    @Override
    public void downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException {
        int userId = 57680761;
        String tag = "未分類";
        PixivBookmarks bookmarks = pixivUserBookmarksService.get(userId, tag, 0, 5).get(60, TimeUnit.SECONDS);
        if (bookmarks == null) {
            log.warn("未获取到收藏数据 userID = {}", userId);
            return;
        }
        log.info("用户 userID = {} 收藏中 tag:{} 下总计有作品 {} 个", userId, tag, bookmarks.getTotal());
        List<Long> pidList = bookmarks.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList());
        List<Future<PixivIllustDetail>> tasks = new ArrayList<>();
        pidList.forEach(pid -> tasks.add(pixivIllustDetailService.getDetail(pid)));
        for (Future<PixivIllustDetail> task : tasks) {
            PixivIllustDetail detail = task.get(60, TimeUnit.SECONDS);
            for (String url : detail.getUrlList()) {
                Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(url);
                if (matcher.find()) {
                    /*todo 添加tag*/
                    

//                    添加下载队列
                    String uuid = matcher.group();
                    String path = "D:/illust";
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    String type = "test";
                    int priority = 5;
                    downloadQueryService.saveOne(new DownloadQuery(uuid, path, fileName, url, type, priority));
                }
            }
        }
        log.info("完成");
    }
}
