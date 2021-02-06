package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivIllustDetailDao;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
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
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PixivIllustDetailServiceImpl extends ServiceImpl<PixivIllustDetailDao, PixivIllustDetail> implements PixivIllustDetailService {
    /**
     * 更新详情周期
     */
    private final static long EXPIRED = 60 * 60 * 24 * 15;
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
    public Future<PixivIllustDetail> getDetail(long pid) {
        return AsyncResult.forValue(findOne(pid));
    }

    @Override
    public PixivIllustDetail findOne(Serializable id) {
        log.info("请求详情 pid = {} ", id);
        long start = System.currentTimeMillis();
        long pid = (long) id;
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
        PixivIllustDetail newDetail = PixivPost.getIllustDetail(pid, null);
        if (newDetail != null) {
            if (detail == null) {
                save(newDetail);
            } else {
                updateById(newDetail);
            }
            detail = newDetail;
            detailCache.put(pid, detail);

//            保存用户
            PixivUserService pixivUserService = SpringContextUtil.getBean(PixivUserService.class);
            PixivUser newUser = new PixivUser(detail.getUserId(), detail.getUserAccount(), detail.getUserName());
            pixivUserService.saveOne(newUser);
//            保存标签
            PixivTagService pixivTagService = SpringContextUtil.getBean(PixivTagService.class);
            List<PixivTag> tags = detail.getTagsInDetail().getTags();
            pixivTagService.saveList(tags);
        }
        log.info("获得详情 pid = {} 耗时：{}", id, RequestBase.timeCost(start));
        return detail;
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
        return detail != null && detail.getCheckSeconds() > now - EXPIRED && detail.getBookmarkCount() > MIN_BOOKMARK_COUNT;
    }


    @Override
    public List<PixivIllustDetail> findList(Collection<Serializable> idCollection) {
        List<PixivIllustDetail> oldDetails = listByIds(idCollection);
        oldDetails.forEach(detail -> detailCache.put(detail.getId(), detail));

        List<Future<PixivIllustDetail>> tasks = new ArrayList<>();
        idCollection.forEach(pid -> tasks.add(detailExecutor.submit(() -> findOne(pid))));

        List<PixivIllustDetail> results = new ArrayList<>();
        tasks.forEach(t -> {
            try {
                PixivIllustDetail detail = t.get(60, TimeUnit.SECONDS);
                if (detail != null) {
                    results.add(detail);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
        return results;
    }


}
