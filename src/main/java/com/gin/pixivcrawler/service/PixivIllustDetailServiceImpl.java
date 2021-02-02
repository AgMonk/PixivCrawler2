package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivIllustDetailDao;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.TasksUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivIllustDetail;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

/**
 * @author bx002
 * @date 2021/2/2 13:17
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PixivIllustDetailServiceImpl extends ServiceImpl<PixivIllustDetailDao, PixivIllustDetail> implements PixivIllustDetailService {
    private final static long EXPIRED = 60 * 60 * 24 * 15;
    private final HashMap<Long, PixivIllustDetail> detailCache = new HashMap<>();
    private final ThreadPoolTaskExecutor detailExecutor = TasksUtil.getExecutor("detail", 10);


    @Override
    public PixivIllustDetail findOne(Serializable id) {
        long now = System.currentTimeMillis() / 1000;
        long pid = (long) id;
        PixivIllustDetail detail = detailCache.get(pid);
        if (detail != null && detail.getCheckSeconds() > now - EXPIRED) {
            return detail;
        }
//        缓存中无数据
        detail = getById(pid);
        if (detail != null && detail.getCheckSeconds() > now - EXPIRED) {
            detailCache.put(pid, detail);
            return detail;
        }
//        数据库无数据或过期
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
            PixivUser oldUser = pixivUserService.findOne(detail.getUserId());
            if (oldUser == null) {
                pixivUserService.saveOne(newUser);
            } else if (!newUser.equals(oldUser)) {
                pixivUserService.updateOne(newUser);
            }
//            保存标签
            PixivTagService pixivTagService = SpringContextUtil.getBean(PixivTagService.class);
            List<PixivTag> tags = detail.getTagsInDetail().getTags();
            List<PixivTag> oldTags = pixivTagService.findList(tags.stream().map(PixivTag::getTag).collect(Collectors.toList()));
            tags.removeAll(oldTags);
            if (tags.size() > 0) {
                pixivTagService.saveList(tags);
            }
        }
        return detail;
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
                if (detail!=null) {
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
