package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.dao.FanboxCookieDao;
import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.entity.Config;
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
import com.gin.pixivcrawler.utils.fanboxUtils.FanboxCookie;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItemsBodyImage;
import com.gin.pixivcrawler.utils.fileUtils.FileUtils;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.*;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivDetailBase;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetailInBookmarks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.entity.ConstantValue.*;
import static com.gin.pixivcrawler.service.PixivIllustDetailServiceImpl.MIN_BOOKMARK_COUNT;
import static com.gin.pixivcrawler.utils.ariaUtils.Aria2Request.*;
import static com.gin.pixivcrawler.utils.pixivUtils.PixivPost.deleteIllustBookmark;
import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_GIF_FULL_NAME;
import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;
import static com.gin.pixivcrawler.utils.timeUtils.TimeUtils.DATE_FORMATTER;

/**
 * @author bx002
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
    private final PixivFileService pixivFileService;
    private final PixivUserService pixivUserService;
    private final ConfigService configService;
    private final FanboxItemService fanboxItemService;

    private final HashMap<Long, AddTagQuery> addTagQueryMap = new HashMap<>();
    private final HashMap<Long, DetailQuery> detailQueryMap = new HashMap<>();

    private final ThreadPoolTaskExecutor tagExecutor;
    private final ThreadPoolTaskExecutor detailExecutor;
    private final PixivCookieDao pixivCookieDao;
    private final FanboxCookieDao fanboxCookieDao;
    /**
     * 未分类作品数量
     */
    private int untaggedTotalCount = 0;
    /**
     * 周期任务开关
     */
    private final HashMap<String, Boolean> scheduledTasksSwitch = new HashMap<>();

    public ScheduledTasksServiceImpl(PixivIllustDetailService pixivIllustDetailService,
                                     PixivUserBookmarksService pixivUserBookmarksService,
                                     PixivTagService pixivTagService,
                                     DownloadQueryService downloadQueryService,
                                     AddTagQueryService addTagQueryService,
                                     DetailQueryService detailQueryService,
                                     SearchQueryService searchQueryService,
                                     PixivSearchService pixivSearchService,
                                     PixivFileService pixivFileService,
                                     PixivUserService pixivUserService, ConfigService configService,
                                     FanboxItemService fanboxItemService, ThreadPoolTaskExecutor tagExecutor,
                                     ThreadPoolTaskExecutor detailExecutor,
                                     PixivCookieDao pixivCookieDao, FanboxCookieDao fanboxCookieDao) {
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivUserBookmarksService = pixivUserBookmarksService;
        this.pixivTagService = pixivTagService;
        this.downloadQueryService = downloadQueryService;
        this.addTagQueryService = addTagQueryService;
        this.detailQueryService = detailQueryService;
        this.searchQueryService = searchQueryService;
        this.pixivSearchService = pixivSearchService;
        this.pixivFileService = pixivFileService;
        this.pixivUserService = pixivUserService;
        this.configService = configService;
        this.fanboxItemService = fanboxItemService;
        this.tagExecutor = tagExecutor;
        this.detailExecutor = detailExecutor;
        this.pixivCookieDao = pixivCookieDao;
        this.fanboxCookieDao = fanboxCookieDao;

        turnSwitch("search", false);

    }

    /**
     * 获取用户uid
     * @return long
     */
    private long getUserId() {
        return configService.getConfig().getUserId();
    }

    /**
     * 获取根目录
     * @return java.lang.String
     */
    private String getRootPath() {
        return configService.getConfig().getRootPath();
    }

    @Override
    public HashMap<String, Boolean> getScheduledTasksSwitch() {
        return scheduledTasksSwitch;
    }

    @Override
    public void turnSwitch(String key, boolean status) {
        scheduledTasksSwitch.put(key, status);
        log.info("周期任务开关 {} 设置为 {}", key, status);
    }

    /**
     * 请求未分类作品，添加TAG，添加到下载队列
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
        List<PixivIllustDetailInBookmarks> bmkIllust = bookmarks.getDetails();
//      总数量大于请求总上限 请求更多
        if (total >= singleLimit) {
            List<Future<PixivBookmarks>> tasksFuture = new ArrayList<>();
            for (int i = 1; i < (totalLimit / singleLimit); i++) {
                tasksFuture.add(pixivUserBookmarksService.get(getUserId(), tag, i * singleLimit, singleLimit));
            }
            for (Future<PixivBookmarks> future : tasksFuture) {
                try {
                    PixivBookmarks bookmark = future.get(60, TimeUnit.SECONDS);
                    bmkIllust.addAll(bookmark.getDetails());
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    future.cancel(true);
                }
            }
        }
//        在添加tag队列里的pid不进行请求
        bmkIllust.removeIf(d -> addTagQueryMap.containsKey(d.getId()));
//        添加到详情队列
        List<DetailQuery> detailQueries = bmkIllust.stream()
                .map(d -> new DetailQuery(d.getId()
                        , getUserId()
                        , "3.未分类"
                        , 5
                        , CALLBACK_TASK_ADD_TAG + "," + CALLBACK_TASK_DOWNLOAD
                        , d.getBookmarkId()
                ))
                .collect(Collectors.toList());
        detailQueryService.saveList(detailQueries);
    }


    public void downloadFanbox() {
        List<FanboxItem> itemList = fanboxItemService.listSupporting(20);
        if (itemList != null || itemList.size() == 0) {
            return;
        }
        for (int i = 0; i < itemList.size(); i++) {
            FanboxItem fanboxItem = itemList.get(i);
            List<FanboxItemsBodyImage> images = fanboxItem.getBody().getImages();
            for (FanboxItemsBodyImage image : images) {
                downloadQueryService.saveOne(image.getId(),
                        String.format("%s/fanbox/%s/%s/", getRootPath(), fanboxItem.getCreatorId(), fanboxItem.getTitle()),
                        String.format("%d - %s.%s", i, image.getId(), image.getExtension()),
                        image.getOriginalUrl(),
                        "fanbox",
                        10
                );
            }
        }
    }

    /**
     * 下载文件
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
                questsInQuery.stream()
                        .map(quest -> quest.getFiles().get(0).getUris().get(0).getUri().replace(NGINX_I_PIXIV_CAT, DOMAIN_I_PXIMG_NET))
                        .collect(Collectors.toList()));
        if (sortedList.size() == 0) {
            return;
        }
        int count = 0;
        for (DownloadQuery downloadQuery : sortedList) {
            Aria2UriOption option = new Aria2UriOption();
            option.setDir(downloadQuery.getPath() + "/" + DATE_FORMATTER.format(ZonedDateTime.now()))
                    .setFileName(downloadQuery.getFileName())
                    .setReferer("*")
            ;
            if ("fanbox".equals(downloadQuery.getType())) {
                FanboxCookie fanboxCookie = fanboxCookieDao.selectById(1);
                String cookie = fanboxCookie.getCookie();
                option.addHeader("Cookie", cookie);
            } else {
                Integer mode = configService.getConfig().getDownloadMode();
                switch (mode) {
                    case 2:
                        downloadQuery.setUrl(downloadQuery.getUrl().replace(DOMAIN_I_PXIMG_NET, NGINX_I_PIXIV_CAT));
                        break;
                    case 1:
                        option.setHttpsProxy("http://127.0.0.1:10809/");
                        break;
                    default:
                        break;
                }
            }

            if (addUri(downloadQuery.getUrl(), option).getError() == null) {
                count++;
            }
        }
        log.info("添加 {} 个下载任务到 Aria2", count);
    }

    /**
     * 移除停止和完成的任务
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void removeCompletedQueryInAria2() {
        //        获取停止任务
        List<Aria2Quest> stoppedQuest = tellStopped().getResult().stream()
                .filter(quest -> {
                    String uri = quest.getFiles().get(0).getUris().get(0).getUri();
                    return PIXIV_ILLUST_FULL_NAME.matcher(uri).find() || PIXIV_GIF_FULL_NAME.matcher(uri).find();
                })
                .collect(Collectors.toList());
        //        完成Url
        List<String> completedUrlList = stoppedQuest.stream()
                .filter(Aria2Quest::isCompleted)
                .map(quest -> quest.getFiles().get(0).getUris().get(0).getUri())
                .collect(Collectors.toList());

        List<String> error3List = stoppedQuest.stream()
                .filter(q -> q.getErrorCode() == 3)
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
            if (error3List.size() > 0) {
                downloadQueryService.deleteByUrl(error3List);
                for (String url : error3List) {
                    Matcher matcherIllust = PIXIV_ILLUST_FULL_NAME.matcher(url);
                    Matcher matcherGif = PIXIV_GIF_FULL_NAME.matcher(url);
                    boolean b1 = matcherIllust.find();
                    boolean b2 = matcherGif.find();
                    if (b1 || b2) {
                        String pidStr = b1 ? (matcherIllust.group().split(DELIMITER_PIXIV_NAME)[0]) : (matcherGif.group().split("_")[0]);
                        long pid = Long.parseLong(pidStr);
                        pixivIllustDetailService.remove(pid);
                        Config config = configService.getConfig();
                        detailQueryService.saveOne(new DetailQuery(pid,
                                config.getUserId(),
                                "6.重新详情",
                                6,
                                CALLBACK_TASK_DOWNLOAD,
                                null));
                    }
                }
                log.info("重新请求作品详情 {}个", error3List.size());
            }
        }
    }

    /**
     * 添加tag
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
     */
    @Scheduled(cron = "3/5 * * * * ?")
    public void detail() {
        int count = configService.getConfig().getQueryMaxOfDetail() - detailQueryMap.size();
        if (count == 0) {
            log.info("{}", detailQueryMap.keySet());
            return;
        }
        List<DetailQuery> newDetailQuery = detailQueryService
                .findSortedList(count, detailQueryMap.keySet());
        newDetailQuery.forEach(dq -> {
            Long pid = dq.getPid();
            String dqType = dq.getType();
            List<String> callbacks = Arrays.asList(dq.getCallback().split(DELIMITER_COMMA));
            detailQueryMap.put(pid, dq);
            detailExecutor.execute(() -> {
                try {
                    PixivIllustDetail detail = pixivIllustDetailService.findOne(pid);
                    if (detail == null) {
                        log.warn("详情请求失败 pid = {}", pid);
                        return;
                    } else {
                        detailQueryService.deleteById(pid);
                    }
//                  回调任务中有添加tag 添加
                    if (callbacks.contains(CALLBACK_TASK_ADD_TAG)) {
                        pixivTagService.addTag(detail, dq.getUserId());
                    }
//                    回调任务中有下载 下载
                    if (callbacks.contains(CALLBACK_TASK_DOWNLOAD)) {
                        for (String url : detail.getUrlList()) {
                            addPixivDownloadQuery(url, "未分类", 5, TYPE_OF_QUERY_UNTAGGED);

//                            Matcher matcherIllust = PIXIV_ILLUST_FULL_NAME.matcher(url);
//                            Matcher matcherGif = PIXIV_GIF_FULL_NAME.matcher(url);
//                            boolean b1 = matcherIllust.find();
//                            boolean b2 = matcherGif.find();
//                            if (b1 || b2) {
//                                String uuid = b1 ? matcherIllust.group() : matcherGif.group();
//                                String path = getRootPath() + "/未分类";
//                                String fileName = b1 ? (url.substring(url.lastIndexOf("/") + 1)) : (matcherGif.group() + "0.zip");
//                                int priority = 5;
//                                downloadQueryService.saveOne(uuid,
//                                        path,
//                                        fileName,
//                                        url,
//                                        TYPE_OF_QUERY_UNTAGGED,
//                                        priority);
//                            }
                        }
                    }
//                回调任务中有 搜索下载
                    if (callbacks.contains(CALLBACK_TASK_SEARCH_DOWNLOAD)) {
                        //                    详情未收藏，且收藏数大于规定值
                        if (detail.getBookmarked() == null && detail.getBookmarkCount() > MIN_BOOKMARK_COUNT) {
                            //                        设置为已收藏
                            pixivIllustDetailService.setIllustBookmarked(detail.getId());
                            for (String url : detail.getUrlList()) {
                                addPixivDownloadQuery(url, "搜索下载/" + dqType.split(":")[1], 4, TYPE_OF_QUERY_SEARCH);
//                                Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(url);
//                                if (matcher.find()) {
//                                    String uuid = matcher.group();
//                                    String path = getRootPath() + "/搜索下载/" + dqType.split(":")[1];
//                                    String fileName = url.substring(url.lastIndexOf("/") + 1);
//                                    int priority = 4;
//                                    downloadQueryService.saveOne(uuid,
//                                            path,
//                                            fileName,
//                                            url,
//                                            TYPE_OF_QUERY_SEARCH,
//                                            priority);
//                                }
                            }
                        }
                    }
//                    回调任务中有移动到未分类
                    if (callbacks.contains(CALLBACK_TASK_MOVE_TO_UNTAGGED)) {
                        TreeMap<String, File> map = pixivFileService.getFilesWithoutDetailMap();
                        List<String> keys = map.keySet().stream().filter(p -> p.startsWith(detail.getId() + "_")).collect(Collectors.toList());
                        if (keys.size() > 0) {
                            FileUtils.moveFiles(map, keys, getRootPath() + "/未分类/");
                        }
                    }


//                    删除队列中的详情任务
                } catch (PixivErrorException e) {
                    String message = e.getMessage();
                    if (message != null && message.contains("删除")) {
//                        删除队列
                        detailQueryService.deleteById(pid);
                        if (callbacks.contains(CALLBACK_TASK_MOVE_TO_UNTAGGED)) {
                            TreeMap<String, File> map = pixivFileService.getFilesWithoutDetailMap();
                            List<String> keys = map.keySet().stream().filter(p -> p.startsWith(pid + "_")).collect(Collectors.toList());
                            FileUtils.moveFiles(map, keys, getRootPath() + "/已删除作品/");
                        }
//                        作品被删除
                        Long bookmarkId = dq.getBookmarkId();
                        if (bookmarkId != null) {
                            PixivCookie pixivCookie = pixivCookieDao.selectById(dq.getUserId());
                            deleteIllustBookmark(pixivCookie.getCookie(), pixivCookie.getTt(), bookmarkId);
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
                detailQueryMap.remove(pid);
            });
        });


    }

    private void addPixivDownloadQuery(String url, String dirName, int priority, String queryType) {
        Matcher matcherIllust = PIXIV_ILLUST_FULL_NAME.matcher(url);
        Matcher matcherGif = PIXIV_GIF_FULL_NAME.matcher(url);
        boolean b1 = matcherIllust.find();
        boolean b2 = matcherGif.find();
        if (b1 || b2) {
            String uuid = b1 ? matcherIllust.group() : matcherGif.group();
            String path = String.format("%s/%s", getRootPath(), dirName);
            String fileName = b1 ? (url.substring(url.lastIndexOf("/") + 1)) : (matcherGif.group() + "0.zip");
            downloadQueryService.saveOne(uuid,
                    path,
                    fileName,
                    url,
                    queryType,
                    priority);
        }
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

    @Scheduled(cron = "10/30 * * * * ?")
    public void findDetailOfOldFiles() {
        TreeMap<String, File> filesWithoutDetailMap = pixivFileService.getFilesWithoutDetailMap();
        List<Long> pidList = filesWithoutDetailMap.keySet().stream()
                .map(pid -> pid.split(DELIMITER_PIXIV_NAME)[0]).map(Long::parseLong).distinct().collect(Collectors.toList());
        List<Long> existsPid = detailQueryService.findList(pidList).stream().map(DetailQuery::getPid).collect(Collectors.toList());
        pidList.removeAll(existsPid);
        if (pidList.size() > 0) {
            List<DetailQuery> tasks = pidList.stream().map(pid -> new DetailQuery(
                    pid
                    , getUserId()
                    , "2.录入无详情文件"
                    , 2
                    , CALLBACK_TASK_MOVE_TO_UNTAGGED
                    , null
            )).collect(Collectors.toList());
            log.info("发现无详情文件 {}个", pidList.size());
            detailQueryService.saveList(tasks);
        }
    }

    /**
     * 归档
     * 文件名模板
     * $uid$  用户id
     * $uname$ 用户名
     * $uac$ 用户账号
     * $pid$ pid
     * $bmc$ 收藏数
     * $title$
     * $tags$
     */
//    @Scheduled(cron = "20/30 * * * * ?")
    public void archive() {
        HashMap<String, File> map = new HashMap<>();
        String rootPath = getRootPath();
        String template = configService.getConfig().getFilePathTemplate();
        FileUtils.listFiles(new File(rootPath + "/待归档/"), map);
        if (map.size() == 0) {
            return;
        }
        List<PixivIllustDetail> details = pixivIllustDetailService.findList(
                map.keySet().stream()
                        .map(pid -> Long.parseLong(pid.split(DELIMITER_PIXIV_NAME)[0]))
                        .distinct()
                        .collect(Collectors.toList())
        );
        List<PixivUser> userList = pixivUserService.findList(details.stream().map(PixivDetailBase::getUserId).collect(Collectors.toList()));
        details.forEach(detail -> {
            PixivUser user = userList.stream().filter(u -> u.getId().equals(detail.getUserId())).findFirst().orElse(null);
            map.keySet().stream()
                    .filter(key -> key.startsWith(detail.getId() + DELIMITER_PIXIV_NAME))
                    .forEach(pid -> {
                        File file = map.get(pid);
                        String oldPath = file.getPath();
                        String newPath = rootPath + template + oldPath.substring(oldPath.lastIndexOf("."));
                        if (user != null) {
                            newPath = newPath
                                    .replace("$uid$", String.valueOf(user.getId()))
                                    .replace("$uname$", replaceIllegalChar(user.getName()))
                                    .replace("$uac$", replaceIllegalChar(user.getAccount()))
                            ;
                        }
                        newPath = newPath
                                .replace("$pid$", pid)
                                .replace("$bmc$", String.valueOf(detail.getBookmarkCount()))
                                .replace("$title$", replaceIllegalChar(detail.getIllustTitle()))
                                .replace("$tags$", replaceIllegalChar(pixivTagService.translate(detail.getTagString(), DELIMITER_COMMA)))
                        ;
                    });

        });

    }

    @Async("searchExecutor")
    @Scheduled(cron = "5 0/5 * * * ?")
    @Override
    public void autoSearch() {
        if (!scheduledTasksSwitch.getOrDefault("search", true)) {
            return;
        }
        SearchQuery query = searchQueryService.findOne();
        if (query == null) {
            return;
        }
        PixivSearchResults results = pixivSearchService.search(query.getKeyword(), query.getUid(), query.getPage());
        if (results == null) {
            return;
        }
        List<DetailQuery> list = results.getIllustManga().getDetails().stream()
                .filter(d -> d.getBookmarked() == null)
                .map(d -> new DetailQuery(
                        d.getId(), getUserId()
                        , TYPE_OF_QUERY_SEARCH + query.getName()
                        , 4
                        , CALLBACK_TASK_SEARCH_DOWNLOAD
                        , d.getBookmarkId()
                )).collect(Collectors.toList());
        List<DetailQuery> existsList = detailQueryService.findList(list.stream().map(DetailQuery::getPid).collect(Collectors.toList()));
        list.removeAll(existsList);
        detailQueryService.saveList(list);
        searchQueryService.delById(query.getUuid());
    }

    @Override
    public StatusReport getStatusReport() {
        return StatusReport.create()
                .setUntaggedTotalCount(untaggedTotalCount)
                .setAddTagQuery(addTagQueryMap.values())
                .setDownloadQuery(downloadQueryService.findSortedList(999, null))
                .setDetailQuery(detailQueryService.findSortedList(999, null))
                .setSearchQuery(searchQueryService.findAll())
                .setScheduledTasksSwitch(scheduledTasksSwitch)
                ;
    }
}
