package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivTagDao;
import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import com.gin.pixivcrawler.service.queryService.AddTagQueryService;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.StringUtils;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_COMMA;
import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_SPACE;

/**
 * @author bx002
 * @date 2021/2/2 14:35
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class PixivTagServiceImpl extends ServiceImpl<PixivTagDao, PixivTag> implements PixivTagService {
    Pattern BMK_PATTERN = Pattern.compile("users入り$");
    public static final Pattern PATTERN_ONLY_WORD = Pattern.compile("^[\\w\\s-']+$");
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
        log.info("更新Tag {} -> {}", entity.getTag(), entity.getTransCustomize());
        return updateById(entity);
    }

    @Override
    public void addTag(PixivIllustDetail detail, Long userId) {
        String tagTranslatedString = translate(detail.getTagString(), DELIMITER_SPACE);
        Long pid = detail.getId();
        PixivIllustDetailService detailService = SpringContextUtil.getBean(PixivIllustDetailService.class);
        detailService.setIllustBookmarked(pid);
        addTagQueryService.saveOne(new AddTagQuery(pid, userId, tagTranslatedString));
    }

    @Override
    public TreeMap<String, String> findDic(List<String> tagList) {
        QueryWrapper<PixivTag> queryWrapper = new QueryWrapper<>();
        if (tagList != null && tagList.size() > 0) {
            queryWrapper.in("tag", tagList);
        }
        queryWrapper.and(rqw -> rqw.isNotNull("trans_customize")
//                .or().isNotNull("trans_raw")
        );
        List<PixivTag> list = list(queryWrapper);
        TreeMap<String, String> dic = new TreeMap<>((o1, o2) -> {
            if (o1.length() != o2.length()) {
                return o2.length() - o1.length();
            }
            return o2.compareTo(o1);
        });
        list.forEach(tagObj -> {
            String transCustomize = tagObj.getTransCustomize();
            String tag = tagObj.getTag();
            String trans = transCustomize;
            trans = PixivTag.replace(trans);
            dic.put(tag, trans);
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
    @Scheduled(cron = "0 0 * * * ?")
    public void countTags() {
        PixivIllustDetailServiceImpl detailService = SpringContextUtil.getBean(PixivIllustDetailServiceImpl.class);
        QueryWrapper<PixivIllustDetail> qw = new QueryWrapper<>();
        qw.select("tag_string");
        List<PixivIllustDetail> list = detailService.list(qw);
        log.info("查询到 {} 个详情", list.size());
        Map<String, Long> groupMap = list.stream()
                .map(PixivIllustDetail::getTagString)
                .flatMap(s -> Arrays.stream(s.split(DELIMITER_COMMA)))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        log.info("总计有tag {} 个", groupMap.size());
        groupMap.forEach((k, v) -> updateById(new PixivTag(k, Math.toIntExact(v))));
        log.info("统计完毕");
    }


    @Override
    public String translate(String tagString, String delimiter) {
        List<String> tagList = Arrays.asList(tagString.split(DELIMITER_COMMA));
//        字典
        TreeMap<String, String> tagsMap = findDic(tagList);
//        返回翻译后的标签
        return tagList.stream()
                .map(tag -> tagsMap.getOrDefault(tag, tag))
                .flatMap(tag -> Arrays.stream(tag.replace(")", "").split("\\(")))
                .flatMap(tag -> Arrays.stream(tag.replace("）", "").split("（")))
//                过滤掉收藏标签
                .filter(tag -> !BMK_PATTERN.matcher(tag).find())
                .map(String::trim)
                .map(tag -> tag.replace(" ", "_"))
                .distinct()
                .collect(Collectors.joining(delimiter));
    }
}
