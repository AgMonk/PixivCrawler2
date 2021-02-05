package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivTagDao;
import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import com.gin.pixivcrawler.service.queryService.AddTagQueryService;
import com.gin.pixivcrawler.utils.StringUtils;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
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
    private final int LIST_MODE_HAS_CUSTOM_TRANSLATION = 1;
    private final int LIST_MODE_HAS_NOT_CUSTOM_TRANSLATION = 2;

    private final AddTagQueryService addTagQueryService;

    public PixivTagServiceImpl(AddTagQueryService addTagQueryService) {
        this.addTagQueryService = addTagQueryService;
    }

    @Override
    public boolean saveList(Collection<PixivTag> entities) {
        List<PixivTag> oldTags = listByIds(entities.stream().map(PixivTag::getTag).collect(Collectors.toList()));
        entities.removeAll(oldTags);
        if (entities.size() == 0) {
            return true;
        }
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
    public void addTag(PixivIllustDetail detail, Long userId) {
        String tagTranslatedString = translate(detail.getTagString(), " ");
        Long pid = detail.getId();
        addTagQueryService.saveOne(new AddTagQuery(pid, userId, tagTranslatedString));
    }

    @Override
    public HashMap<String, String> findDic(List<String> tagList) {
        QueryWrapper<PixivTag> queryWrapper = new QueryWrapper<>();
        if (tagList != null && tagList.size() > 0) {
            queryWrapper.in("tag", tagList);
        }
        queryWrapper.and(rqw -> rqw.isNotNull("trans_customize").or().isNotNull("trans_raw"));
        List<PixivTag> list = list(queryWrapper);
        HashMap<String, String> dic = new HashMap<>(list.size());
        list.forEach(tagObj -> {
            String transCustomize = tagObj.getTransCustomize();
            String tag = tagObj.getTag();
            if (transCustomize != null) {
                dic.put(tag, transCustomize);
            } else {
                dic.put(tag, tagObj.getTransRaw());
            }
        });
        return dic;
    }

    @Override
    public List<PixivTag> findListBy(int mode, String keyword) {
        QueryWrapper<PixivTag> qw = new QueryWrapper<>();
        if (mode == LIST_MODE_HAS_CUSTOM_TRANSLATION) {
            qw.isNotNull("trans_customize");
        }
        if (mode == LIST_MODE_HAS_NOT_CUSTOM_TRANSLATION) {
            qw.isNull("trans_customize");
        }
        if (!StringUtils.isEmpty(keyword)) {
            qw.and(tqw -> tqw
                    .or().like("trans_customize", keyword)
                    .or().like("tag", keyword)
                    .or().like("trans_raw", keyword)
            );
        }

        return list(qw);
    }

    @Override
    public String translate(String tagString, String delimiter) {
        List<String> tagList = Arrays.asList(tagString.split(DELIMITER));
//        字典
        HashMap<String, String> tagsMap = findDic(tagList);
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
