package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivIllustDetailDao;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivErrorException;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTagsInDetail;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUser;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import com.gin.pixivcrawler.utils.requestUtils.RequestBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author bx002
 * @date 2021/2/2 13:17
 */
//@SuppressWarnings("TryWithIdenticalCatches")
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PixivIllustDetailServiceImpl extends ServiceImpl<PixivIllustDetailDao, PixivIllustDetail> implements PixivIllustDetailService {
    /**
     * 更新详情周期
     */
    private final static long EXPIRED = 60 * 60 * 24 * 30;
    /**
     * 最小收藏数
     */
    public static final int MIN_BOOKMARK_COUNT = 200;
    private final HashMap<Long, PixivIllustDetail> detailCache = new HashMap<>();
    private final ThreadPoolTaskExecutor detailExecutor;

    public PixivIllustDetailServiceImpl(ThreadPoolTaskExecutor detailExecutor) {
        this.detailExecutor = detailExecutor;
    }

    @Override
    @Async("detailExecutor")
    public Future<PixivIllustDetail> getDetail(long pid) throws PixivErrorException {
        return AsyncResult.forValue(findOne(pid));
    }

    @Override
    public void setIllustBookmarked(long pid) {
        PixivIllustDetail detail = new PixivIllustDetail();
        detail.setId(pid).setBookmarked(1);
        updateById(detail);
    }

    @Override
    public void remove(long pid) {
        detailCache.remove(pid);
        removeById(pid);
    }

    @Override
    public PixivIllustDetail findOne(Serializable id) throws PixivErrorException {
        log.debug("请求详情 pid = {} ", id);
        long start = System.currentTimeMillis();
        long pid = id instanceof String ? Long.parseLong((String) id) : (long) id;
        PixivIllustDetail detail = detailCache.get(pid);
        if (isAvailable(detail)) {
            return detail;
        }
//        缓存中 无数据/过期/或收藏数过低
        detail = getById(pid);
        if (isAvailable(detail)) {
            detailCache.put(pid, detail);
            return detail;
        }
//        数据库 无数据/过期/或收藏数过低
        try {
            PixivIllustDetail newDetail = PixivPost.getIllustDetail(pid, null);
            if (newDetail != null) {
                if (detail == null) {
                    saveOrUpdate(newDetail);
                } else {
                    updateById(newDetail);
                }
                detail = newDetail;
                detailCache.put(pid, detail);

                saveUserAndTags(detail);
            }
        } catch (PixivErrorException e) {

//            String message = e.getMessage();
//            if (message != null && message.contains("删除")) {
////            向旧系统请求
//                log.info("尝试向旧系统请求详情数据 pid = {}", id);
//                String result = String.valueOf(PostRequest.create().post("http://localhost:8888/ill/get?id=" + id));
//                Illustration detailFromOldSystem = JSONObject.parseObject(result, Illustration.class);
//                if (detailFromOldSystem != null) {
//                    log.info("获得旧系统数据 pid = {}", id);
//                    detail = new PixivIllustDetail();
//                    detail.setBookmarkCount(detailFromOldSystem.getBookmarkCount())
//                            .setCheckSeconds(System.currentTimeMillis() / 1000)
//                            .setTagString(detailFromOldSystem.getTag())
//                            .setUrlPrefix(detailFromOldSystem.getUrlPrefix())
//                            .setUrlSuffix(detailFromOldSystem.getUrlSuffix())
//                            .setUserId(detailFromOldSystem.getUserId())
//                            .setUserName(detailFromOldSystem.getUserName())
//                            .setId(detailFromOldSystem.getId())
//                            .setIllustTitle(detailFromOldSystem.getIllustTitle())
//                            .setIllustType(detailFromOldSystem.getIllustType())
//                            .setPageCount(detailFromOldSystem.getPageCount())
//
//                    ;
//                    saveOrUpdate(detail);
//                    saveUserAndTags(detail);
//                }
//            }
            if (detail == null) {
                throw e;
            }
        }
        if (detail != null) {
            log.debug("获得详情 pid = {} 耗时：{}", id, RequestBase.timeCost(start));
        }
        return detail;
    }

    private static void saveUserAndTags(PixivIllustDetail detail) {
        //            保存用户
        PixivUserService pixivUserService = SpringContextUtil.getBean(PixivUserService.class);
        PixivUser newUser = new PixivUser(detail.getUserId(), detail.getUserAccount(), detail.getUserName());
        pixivUserService.saveOne(newUser);
        //            保存标签
        PixivTagService pixivTagService = SpringContextUtil.getBean(PixivTagService.class);
        PixivTagsInDetail tagsInDetail = detail.getTagsInDetail();
        if (tagsInDetail != null) {
            List<PixivTag> tags = tagsInDetail.getTags();
            pixivTagService.saveList(tags);
        }
    }

    /**
     * 判断该详情是否可用
     *
     * @param detail 详情
     * @return boolean
     * @author bx002
     * @date 2021/2/5 11:51
     */
    private static boolean isAvailable(PixivIllustDetail detail) {
        long now = System.currentTimeMillis() / 1000;
        return detail != null
//                未过期
                && detail.getCheckSeconds() > now - EXPIRED
//                收藏数较多或已收藏的
                && (detail.getBookmarkCount() > MIN_BOOKMARK_COUNT || detail.getBookmarked() != null);
    }


    @Override
    public List<PixivIllustDetail> findList(Collection<Serializable> idCollection) {
        List<PixivIllustDetail> oldDetails = listByIds(idCollection);
        oldDetails.forEach(detail -> detailCache.put(detail.getId(), detail));
        HashMap<Long, Future<PixivIllustDetail>> taskMap = new HashMap<>();
        idCollection.forEach(pid -> taskMap.put(Long.parseLong(String.valueOf(pid)), detailExecutor.submit(() -> {
            try {
                return findOne(pid);
            } catch (PixivErrorException e) {
                e.printStackTrace();
            }
            return null;
        })));

//        List<Future<PixivIllustDetail>> tasks = new ArrayList<>();
//        idCollection.forEach(pid -> tasks.add(detailExecutor.submit(() -> findOne(pid))));

        List<PixivIllustDetail> results = new ArrayList<>();
        taskMap.forEach((pid, future) -> {
            try {
                PixivIllustDetail detail = future.get(120, TimeUnit.SECONDS);
                if (detail != null) {
                    results.add(detail);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                future.cancel(true);
            } catch (ExecutionException e) {
                e.printStackTrace();
                future.cancel(true);
            } catch (TimeoutException e) {
                log.warn("请求超时 pid = {}", pid);
                future.cancel(true);
            }
        });
        return results;
    }


}
