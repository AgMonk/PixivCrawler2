package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.dao.PixivTagDao;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
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
    private final List<Long> addTagQuery = new ArrayList<>();
    private final ThreadPoolTaskExecutor tagExecutor;

    public PixivTagServiceImpl(PixivCookieDao pixivCookieDao, ThreadPoolTaskExecutor tagExecutor) {
        this.pixivCookieDao = pixivCookieDao;
        this.tagExecutor = tagExecutor;
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

    @Override
    public List<Long> getAddTagQuery() {
        return addTagQuery;
    }

    @Override
    public void addTag(PixivIllustDetail detail, Long userId) {
        QueryWrapper<PixivCookie> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
//        查询tag翻译
        String tagTranslatedString = translate(detail.getTagString(), " ");

        PixivCookie pixivCookie = pixivCookieDao.selectOne(qw);
        Long pid = detail.getId();
        addTagQuery.add(pid);
        log.info("当前添加tag的队列长度为：{}", addTagQuery.size());
        tagExecutor.execute(() -> {
            PixivPost.addTags(pid, pixivCookie.getCookie(), pixivCookie.getTt(), tagTranslatedString);
            addTagQuery.remove(pid);
            log.info("当前添加tag的队列长度为：{}", addTagQuery.size());
        });
    }


    @Override
    public String translate(String tagString, String delimiter) {
        List<String> tagList = Arrays.asList(tagString.split(DELIMITER));
        QueryWrapper<PixivTag> queryWrapper = new QueryWrapper<>();
        HashMap<String, String> tagsMap = new HashMap<>();
        queryWrapper.in("tag", tagList).and(rqw -> rqw.isNotNull("trans_customize").or().isNotNull("trans_raw"));
//        构建字典
        list(queryWrapper).forEach(tagObj -> {
            String transCustomize = tagObj.getTransCustomize();
            String tag = tagObj.getTag();
            if (transCustomize != null) {
                tagsMap.put(tag, transCustomize);
            } else {
                tagsMap.put(tag, tagObj.getTransRaw());
            }
        });
//        返回翻译后的标签
        return tagList.stream()
                .map(tag -> tagsMap.getOrDefault(tag, tag))
                .flatMap(tag -> Arrays.stream(tag.replace(")", "").split("\\(")))
                .flatMap(tag -> Arrays.stream(tag.replace("）", "").split("（")))
                .map(String::trim)
                .map(tag -> tag.replace(" ", "_"))
                .distinct()
                .collect(Collectors.joining(delimiter));
    }
}
