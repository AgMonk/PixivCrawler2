package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivIllustDetailDao;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivIllustDetail;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public PixivIllustDetail findOne(Serializable id) {
        long pid = (long) id;
        PixivIllustDetail detail = detailCache.get(pid);
        if (detail != null) {
            return detail;
        }
//        缓存中无数据
        detail = getById(pid);
        long now = System.currentTimeMillis() / 1000;
        if (detail != null && detail.getCheckSeconds() > now - EXPIRED) {
            detailCache.put(pid, detail);
            return detail;
        }
//        数据库无数据或过期
        detail = PixivPost.getIllustDetail(pid, null);
        if (detail != null) {
            detailCache.put(pid, detail);
            save(detail);
//            保存用户
            PixivUserService pixivUserService = SpringContextUtil.getBean(PixivUserService.class);
            PixivUser newUser = new PixivUser(detail.getUserId(), detail.getUserAccount(), detail.getUserName());
            PixivUser oldUser = pixivUserService.findOne(detail.getUserId());
            if (oldUser == null) {
                pixivUserService.saveOne(newUser);
            } else if (!newUser.equals(oldUser)) {
                pixivUserService.updateOne(newUser);
            }
        }
        return detail;
    }

    @Override
    public List<PixivIllustDetail> findList(Collection<Serializable> idCollection) {
        return null;
    }


}
