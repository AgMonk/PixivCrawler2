package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import com.gin.pixivcrawler.utils.ariaUtils.Aria2Quest;
import com.gin.pixivcrawler.utils.ariaUtils.Aria2UriOption;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivDetailBase;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

import static com.gin.pixivcrawler.utils.ariaUtils.Aria2Request.*;
import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;

/**
 * @author bx002
 * @date 2021/2/3 11:28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class ScheduledTasksServiceImpl implements ScheduledTasksService {
    public static final int ARIA_2_QUERY_MAX_COUNT = 30;
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivUserBookmarksService pixivUserBookmarksService;
    private final PixivTagService pixivTagService;
    private final DownloadQueryService downloadQueryService;

    public ScheduledTasksServiceImpl(PixivIllustDetailService pixivIllustDetailService,
                                     PixivUserBookmarksService pixivUserBookmarksService,
                                     PixivTagService pixivTagService, DownloadQueryService downloadQueryService) {
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivUserBookmarksService = pixivUserBookmarksService;
        this.pixivTagService = pixivTagService;
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
        long userId = 57680761;
        String tag = "未分類";
//        单次请求作品数量上限
        int singleLimit = 5;
//        一次操作请求作品数量上限
        int totalLimit = 10;
        PixivBookmarks bookmarks = pixivUserBookmarksService.get(userId, tag, 0, singleLimit).get(60, TimeUnit.SECONDS);
        if (bookmarks == null) {
            log.warn("未获取到收藏数据 userID = {}", userId);
            return;
        }
        Integer total = bookmarks.getTotal();
        log.info("用户 userID = {} 收藏中 tag:{} 下总计有作品 {} 个", userId, tag, total);
        List<Long> pidList = bookmarks.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList());
//      总数量大于请求总上限 请求更多
        if (total >= totalLimit) {
            List<Future<PixivBookmarks>> tasksFuture = new ArrayList<>();
            for (int i = 1; i < (totalLimit / singleLimit); i++) {
                tasksFuture.add(pixivUserBookmarksService.get(userId, tag, i * singleLimit, singleLimit));
            }
            for (Future<PixivBookmarks> future : tasksFuture) {
                PixivBookmarks bookmark = future.get(60, TimeUnit.SECONDS);
                pidList.addAll(bookmark.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList()));
            }
        }

        List<Future<PixivIllustDetail>> tasks = new ArrayList<>();
        pidList.forEach(pid -> tasks.add(pixivIllustDetailService.getDetail(pid)));
        for (Future<PixivIllustDetail> task : tasks) {
            PixivIllustDetail detail = task.get(60, TimeUnit.SECONDS);
            for (String url : detail.getUrlList()) {
                Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(url);
                if (matcher.find()) {
//                    添加tag
                    pixivTagService.addTag(detail, userId);
//                    添加下载队列
                    String uuid = matcher.group();
                    String path = "f:/illust/未分类/";
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    String type = "3.未分类";
                    int priority = 5;
                    downloadQueryService.saveOne(new DownloadQuery(uuid, path, fileName, url, type, priority));
                }
            }
        }
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void addDownloadQuery2Aria2() {
//        获取停止任务
        List<Aria2Quest> stoppedQuest = tellStopped().getResult().stream()
                .filter(quest -> PIXIV_ILLUST_FULL_NAME.matcher(quest.getFiles().get(0).getUris().get(0).getUri()).find()).collect(Collectors.toList());
        //        完成Url
        List<String> completedUrlList = stoppedQuest.stream()
                .filter(Aria2Quest::isCompleted)
                .map(quest -> quest.getFiles().get(0).getUris().get(0).getUri())
                .collect(Collectors.toList());
//        停止gid
        List<String> gidList = stoppedQuest.stream().map(Aria2Quest::getGid).collect(Collectors.toList());
//        移除完成任务
        if (stoppedQuest.size() > 0) {
//            移除所有停止任务
            for (String gid : gidList) {
                removeDownloadResult(gid);
            }
            if (completedUrlList.size() > 0 && downloadQueryService.deleteByUrl(completedUrlList)) {
                log.info("从数据库移除 {} 个已完成任务", completedUrlList.size());
            }
        }
//        获取当前正在进行的任务数量
        List<Aria2Quest> activeQuests = tellActive().getResult();
        List<Aria2Quest> waitingQuests = tellWaiting().getResult();
        List<Aria2Quest> questsInQuery = new ArrayList<>();
        questsInQuery.addAll(activeQuests);
        questsInQuery.addAll(waitingQuests);
        int limit = ARIA_2_QUERY_MAX_COUNT - activeQuests.size() - waitingQuests.size();
//        添加新任务
        if (limit <= 0) {
            return;
        }
        List<DownloadQuery> sortedList = downloadQueryService.findSortedList(limit,
                questsInQuery.stream().map(quest -> quest.getFiles().get(0).getUris().get(0).getUri()).collect(Collectors.toList()));
        if (sortedList.size() == 0) {
            return;
        }
        int count = 0;
        for (DownloadQuery downloadQuery : sortedList) {
            Aria2UriOption option = new Aria2UriOption();
            option.setDir(downloadQuery.getPath())
                    .setFileName(downloadQuery.getFileName())
                    .setReferer("*")
                    .setHttpsProxy("http://127.0.0.1:10809/")
            ;
            if (addUri(downloadQuery.getUrl(), option).getError() == null) {
                count++;
            }
        }
        log.info("添加 {} 个下载任务到 Aria2", count);

    }
}
