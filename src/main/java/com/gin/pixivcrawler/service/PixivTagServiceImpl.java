package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.dao.PixivTagDao;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.DELIMITER;

/**
 * @author bx002
 * @date 2021/2/2 14:35
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PixivTagServiceImpl extends ServiceImpl<PixivTagDao, PixivTag> implements PixivTagService {
    private final PixivCookieDao pixivCookieDao;

    public PixivTagServiceImpl(PixivCookieDao pixivCookieDao) {
        this.pixivCookieDao = pixivCookieDao;
    }

    @Override
    public boolean saveList(Collection<PixivTag> entities) {
        List<PixivTag> oldTags = listByIds(entities.stream().map(PixivTag::getTag).collect(Collectors.toList()));
        entities.removeAll(oldTags);
        return saveBatch(entities);
    }

    @Override
    public boolean saveOne(PixivTag entity) {
        return save(entity);
    }

    @Override
    public PixivTag findOne(Serializable id) {
        return getById(id);
    }

    @Override
    public List<PixivTag> findList(Collection<Serializable> idCollection) {
        return listByIds(idCollection);
    }

    @Override
    public boolean updateOne(PixivTag entity) {
        return updateById(entity);
    }

    @Async("tagExecutor")
    @Override
    public void addTag(PixivIllustDetail detail, Long userId) {
        QueryWrapper<PixivCookie> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
//        查询tag翻译
        String tagTranslatedString = translate(detail.getTagString(), " ");
        log.info("为作品 pid = {} 添加Tag ：{}", detail.getId(), tagTranslatedString);

        PixivCookie pixivCookie = pixivCookieDao.selectOne(qw);
//        PixivPost.addTags(detail.getId(), pixivCookie.getCookie(), pixivCookie.getTt(), tagTranslatedString);
    }


    @Override
    public String translate(String tagString, String delimiter) {
        List<String> tagList = Arrays.asList(tagString.split(DELIMITER));
        QueryWrapper<PixivTag> queryWrapper = new QueryWrapper<>();
        HashMap<String, String> tagsMap = new HashMap<>();
        queryWrapper.in("tag", tagList).and(rqw -> rqw.isNotNull("trans_customize").or().isNotNull("trans_raw"));
        list(queryWrapper).forEach(tagObj -> {
            String transCustomize = tagObj.getTransCustomize();
            String tag = tagObj.getTag();
            if (transCustomize != null) {
                tagsMap.put(tag, transCustomize);
            } else {
                tagsMap.put(tag, tagObj.getTransRaw());
            }
        });
        return tagList.stream()
                .map(tag -> tagsMap.getOrDefault(tag, tag))
                .flatMap(tag -> Arrays.stream(tag.replace(")", "").split("\\(")))
                .distinct()
                .collect(Collectors.joining(delimiter));
    }
}
