package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.entity.ConstantValue;
import com.gin.pixivcrawler.entity.SearchKeyword;
import com.gin.pixivcrawler.entity.StatusReport;
import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import com.gin.pixivcrawler.entity.taskQuery.DetailQuery;
import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import com.gin.pixivcrawler.entity.taskQuery.SearchQuery;
import com.gin.pixivcrawler.service.queryService.AddTagQueryService;
import com.gin.pixivcrawler.service.queryService.DetailQueryService;
import com.gin.pixivcrawler.service.queryService.DownloadQueryService;
import com.gin.pixivcrawler.service.queryService.SearchQueryService;
import com.gin.pixivcrawler.utils.ariaUtils.Aria2Quest;
import com.gin.pixivcrawler.utils.ariaUtils.Aria2UriOption;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivDetailBase;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.entity.ConstantValue.*;
import static com.gin.pixivcrawler.service.PixivIllustDetailServiceImpl.MIN_BOOKMARK_COUNT;
import static com.gin.pixivcrawler.utils.ariaUtils.Aria2Request.*;
import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_COMMA;
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
    private final PixivTagService pixivTagService;
    private final DownloadQueryService downloadQueryService;
    private final AddTagQueryService addTagQueryService;
    private final DetailQueryService detailQueryService;
    private final SearchQueryService searchQueryService;
    private final PixivSearchService pixivSearchService;
    private final ConfigService configService;

    private final HashMap<Long, AddTagQuery> addTagQueryMap = new HashMap<>();
    private final HashMap<Long, DetailQuery> detailQueryMap = new HashMap<>();

    private final ThreadPoolTaskExecutor tagExecutor;
    private final ThreadPoolTaskExecutor detailExecutor;
    private final PixivCookieDao pixivCookieDao;
    /**
     * 未分类作品数量
     */
    private int untaggedTotalCount = 0;

    public ScheduledTasksServiceImpl(PixivIllustDetailService pixivIllustDetailService,
                                     PixivUserBookmarksService pixivUserBookmarksService,
                                     PixivTagService pixivTagService,
                                     DownloadQueryService downloadQueryService,
                                     AddTagQueryService addTagQueryService,
                                     DetailQueryService detailQueryService,
                                     SearchQueryService searchQueryService, PixivSearchService pixivSearchService, ConfigService configService, ThreadPoolTaskExecutor tagExecutor,
                                     ThreadPoolTaskExecutor detailExecutor,
                                     PixivCookieDao pixivCookieDao) {
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivUserBookmarksService = pixivUserBookmarksService;
        this.pixivTagService = pixivTagService;
        this.downloadQueryService = downloadQueryService;
        this.addTagQueryService = addTagQueryService;
        this.detailQueryService = detailQueryService;
        this.searchQueryService = searchQueryService;
        this.pixivSearchService = pixivSearchService;
        this.configService = configService;
        this.tagExecutor = tagExecutor;
        this.detailExecutor = detailExecutor;
        this.pixivCookieDao = pixivCookieDao;
    }
    /**
     * 获取用户uid
     * @return long
     * @author Gin
     * @date 2021/2/17 14:56
     */
    private long getUserId(){
        return configService.getConfig().getUserId();
    }
    /**
     * 获取根目录
     * @return java.lang.String
     * @author Gin
     * @date 2021/2/17 15:00
     */
    private String getRootPath(){
        return configService.getConfig().getRootPath();
    }

    /**
     * 请求未分类作品，添加TAG，添加到下载队列
     *
     * @author bx002
     * @date 2021/2/3 17:21
     */
    @Async("mainExecutor")
    @Override
    @Scheduled(cron = "3 0/10 * * * ?")
    public void downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException {
        String tag = "未分類";
//        单次请求作品数量上限
        int singleLimit = 5;
//        一次操作请求作品数量上限
        int totalLimit = 10;
        PixivBookmarks bookmarks = pixivUserBookmarksService.get(getUserId(), tag, 0, singleLimit).get(60, TimeUnit.SECONDS);
        if (bookmarks == null) {
            log.warn("未获取到收藏数据 userID = {}", getUserId());
            return;
        }
        Integer total = bookmarks.getTotal();
        log.info("用户 userID = {} 收藏中 tag:{} 下总计有作品 {} 个", getUserId(), tag, total);
        untaggedTotalCount = total;
        List<Long> pidList = bookmarks.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList());
//      总数量大于请求总上限 请求更多
        if (total >= singleLimit) {
            List<Future<PixivBookmarks>> tasksFuture = new ArrayList<>();
            for (int i = 1; i < (totalLimit / singleLimit); i++) {
                tasksFuture.add(pixivUserBookmarksService.get(getUserId(), tag, i * singleLimit, singleLimit));
            }
            for (Future<PixivBookmarks> future : tasksFuture) {
                PixivBookmarks bookmark = future.get(60, TimeUnit.SECONDS);
                pidList.addAll(bookmark.getDetails().stream().map(PixivDetailBase::getId).collect(Collectors.toList()));
            }
        }
//        在添加tag队列里的pid不进行请求
        pidList.removeAll(addTagQueryMap.keySet());
//        添加到详情队列
        List<DetailQuery> detailQueries = pidList.stream()
                .map(pid -> new DetailQuery(pid,
                        getUserId(),
                        "3.未分类",
                        5,
                        CALLBACK_TASK_ADD_TAG + "," + CALLBACK_TASK_DOWNLOAD))
                .collect(Collectors.toList());
        detailQueryService.saveList(detailQueries);
    }

    /**
     * 下载文件
     *
     * @author bx002
     * @date 2021/2/4 16:37
     */
    @Scheduled(cron = "1/8 * * * * ?")
    public void addDownloadQuery2Aria2() {
//        获取当前正在进行的任务数量
        List<Aria2Quest> activeQuests = tellActive().getResult();
        List<Aria2Quest> waitingQuests = tellWaiting().getResult();
        List<Aria2Quest> questsInQuery = new ArrayList<>();
        questsInQuery.addAll(activeQuests);
        questsInQuery.addAll(waitingQuests);
        int limit = configService.getConfig().getQueryMaxOfAria2() - activeQuests.size() - waitingQuests.size();
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
//                    .setHttpsProxy("http://127.0.0.1:10809/")
            ;
            if (addUri(downloadQuery.getUrl(), option).getError() == null) {
                count++;
            }
        }
        log.info("添加 {} 个下载任务到 Aria2", count);
    }

    /**
     * 移除停止和完成的任务
     *
     * @author bx002
     * @date 2021/2/5 15:37
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void removeCompletedQueryInAria2() {
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
    }

    /**
     * 添加tag
     *
     * @author bx002
     * @date 2021/2/4 16:37
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void addTag() {
//      同步任务
        List<AddTagQuery> newTasks = addTagQueryService.findAllNotIn(addTagQueryMap.keySet());
        newTasks.forEach(query -> addTagQueryMap.put(query.getPid(), query));
        if (addTagQueryMap.size() == 0 || newTasks.size() == 0) {
            return;
        }

        newTasks.forEach(query -> {
            Long pid = query.getPid();
            tagExecutor.execute(() -> {
                PixivCookie pixivCookie = pixivCookieDao.selectById(query.getUserId());
                PixivPost.addTags(pid, pixivCookie.getCookie(), pixivCookie.getTt(), query.getTag());
                if (addTagQueryService.delete(pid)) {
                    addTagQueryMap.remove(pid);
                    untaggedTotalCount--;
                }
            });
        });

    }

    /**
     * 获取详情
     *
     * @author bx002
     * @date 2021/2/5 12:15
     */
    @Scheduled(cron = "3/5 * * * * ?")
    public void detail() {
        List<DetailQuery> newDetailQuery = detailQueryService
                .findSortedList(configService.getConfig().getQueryMaxOfDetail(), detailQueryMap.keySet());
        newDetailQuery.forEach(dq -> {
            Long pid = dq.getPid();
            String dqType = dq.getType();
            detailQueryMap.put(pid, dq);
            detailExecutor.execute(() -> {
                List<String> callbacks = Arrays.asList(dq.getCallback().split(DELIMITER_COMMA));
                PixivIllustDetail detail = pixivIllustDetailService.findOne(pid);
//                  回调任务中有添加tag 添加
                if (callbacks.contains(CALLBACK_TASK_ADD_TAG)) {
                    pixivTagService.addTag(detail, dq.getUserId());
                }
//                    回调任务中有下载 下载
                if (callbacks.contains(CALLBACK_TASK_DOWNLOAD)) {
                    for (String url : detail.getUrlList()) {
                        Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(url);
                        if (matcher.find()) {
                            String uuid = matcher.group();
                            String path = getRootPath()+"/未分类";
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            int priority = 5;
                            downloadQueryService.saveOne(uuid,
                                    path,
                                    fileName,
                                    url,
                                    TYPE_OF_QUERY_UNTAGGED,
                                    priority);
                        }
                    }
                }
//                回调任务中有 搜索下载
                if (callbacks.contains(CALLBACK_TASK_SEARCH_DOWNLOAD)) {
//                    详情未收藏，且收藏数大于规定值
                    if (detail.getBookmarked() == null && detail.getBookmarkCount() > MIN_BOOKMARK_COUNT) {
//                        设置为已收藏
                        pixivIllustDetailService.setIllustBookmarked(detail.getId());
                        for (String url : detail.getUrlList()) {
                            Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(url);
                            if (matcher.find()) {
                                String uuid = matcher.group();
                                String path = getRootPath()+"/搜索下载/" + dqType.split(":")[1];
                                String fileName = url.substring(url.lastIndexOf("/") + 1);
                                int priority = 4;
                                downloadQueryService.saveOne(uuid,
                                        path,
                                        fileName,
                                        url,
                                        TYPE_OF_QUERY_SEARCH,
                                        priority);
                            }
                        }
                    }
                }
//                    删除队列中的详情任务
                if (detailQueryService.deleteById(pid)) {
                    detailQueryMap.remove(pid);
                }
            });
        });


    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void addSearchQuery() {
        if (searchQueryService.findOne() != null) {
            return;
        }
        List<SearchKeyword> keywordList = pixivSearchService.findAll();
        searchQueryService.saveList(keywordList, getUserId(), 1);
        searchQueryService.saveList(keywordList, getUserId(), 2);
    }

    @Async("searchExecutor")
    @Scheduled(cron = "5 0/5 * * * ?")
    @Override
    public void autoSearch() {
        SearchQuery query = searchQueryService.findOne();
        if (query == null) {
            return;
        }
        PixivSearchResults results = pixivSearchService.search(query.getKeyword(), query.getUid(), query.getPage());
        List<DetailQuery> list = results.getIllustManga().getDetails().stream()
                .filter(d -> d.getBookmarked() == null)
                .map(PixivDetailBase::getId)
                .map(pid -> new DetailQuery(
                        pid, getUserId()
                        , TYPE_OF_QUERY_SEARCH + query.getName()
                        , 4
                        , CALLBACK_TASK_SEARCH_DOWNLOAD
                )).collect(Collectors.toList());
        detailQueryService.saveList(list);
        searchQueryService.delById(query.getUuid());
    }

    @Override
    public StatusReport getStatusReport() {
        return StatusReport.create()
                .setUntaggedTotalCount(untaggedTotalCount)
                .setAddTagQuery(addTagQueryMap.values())
                .setDownloadQuery(downloadQueryService.findSortedList(99, null))
                .setDetailQuery(detailQueryService.findSortedList(99, null))
                .setSearchQuery(searchQueryService.findAll())
                ;
    }
}
