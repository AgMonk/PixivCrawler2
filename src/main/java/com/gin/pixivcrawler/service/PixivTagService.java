package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.service.base.BaseService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;

import java.util.List;
import java.util.TreeMap;

/**
 * @author bx002
 * @date 2021/2/2 14:35
 */
public interface PixivTagService extends BaseService<PixivTag> {

    /**
     * 为作品添加tag
     *
     * @param detail 详情
     * @param userId 用户id
     * @author bx002
     * @date 2021/2/3 17:43
     */
    void addTag(PixivIllustDetail detail, Long userId);

    /**
     * 翻译tag字符串
     *
     * @param tagString tag字符串，以英文逗号分隔
     * @param delimiter 翻译完成的字符串的分隔符
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/4 10:55
     */
    String translate(String tagString, String delimiter);

    /**
     * 查询所有带有自带翻译或自定义翻译的tag(构建字典)
     *
     * @param tagList tag范围
     * @return java.util.List<com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag>
     * @author bx002
     * @date 2021/2/5 15:49
     */
    TreeMap<String, String> findDic(List<String> tagList);

    /**
     * 按照模式查询
     *
     * @return java.util.List<com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag>
     * @author bx002
     * @date 2021/2/5 15:55
     */
    List<PixivTag> findListBy(int mode, String keyword);

}
