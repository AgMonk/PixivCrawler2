package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.PixivTagService;
import com.gin.pixivcrawler.utils.StringUtils;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import lombok.extern.slf4j.Slf4j;
import org.nlpcn.commons.lang.jianfan.JianFan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author bx002
 * @date 2021/2/5 16:25
 */
@RequestMapping("tag")
@RestController
@Slf4j
public class PixivTagController {
    public static final Pattern PATTERN_ONLY_WORD = Pattern.compile("^[\\w+]$");
    private final PixivTagService pixivTagService;

    public PixivTagController(PixivTagService pixivTagService) {
        this.pixivTagService = pixivTagService;
    }

    /**
     * 获取翻译字典
     *
     * @return com.gin.pixivcrawler.entity.response.Res<java.util.HashMap < java.lang.String, java.lang.String>>
     * @author bx002
     * @date 2021/2/5 16:31
     */
    @RequestMapping("getDic")
    public Res<TreeMap<String, String>> getDic() {
        return Res.success(pixivTagService.findDic(null));
    }

    /**
     * 查询tag列表
     *
     * @param mode    模式 1有自定义翻译 2无自定义翻译
     * @param keyword 含有的关键字
     * @return com.gin.pixivcrawler.entity.response.Res<java.util.List < com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag>>
     * @author bx002
     * @date 2021/2/5 16:33
     */
    @RequestMapping("findListBy")
    public Res<List<PixivTag>> findListBy(@RequestParam(defaultValue = "0") Integer mode, String keyword) {
        TreeMap<String, String> dic = pixivTagService.findDic(null);
        return Res.success(pixivTagService.findListBy(mode, keyword).stream().peek(tag -> {
            String t = tag.getTag();
            tag.addRecTrans(getRecommend(t, dic));
            tag.addRecTrans(JianFan.f2j(t));
            String transRaw = tag.getTransRaw();
            if (!StringUtils.isEmpty(transRaw)) {
                tag.addRecTrans(getRecommend(transRaw, dic));
            }
        }).collect(Collectors.toList()));
    }

    @RequestMapping("updateTrans")
    public Res<Void> updateTrans(PixivTag tag) {
        pixivTagService.updateOne(tag);
        return Res.success();
    }

    private static String getRecommend(String tag, TreeMap<String, String> dic) {
        String t = tag;
        for (Map.Entry<String, String> entry : dic.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (t.contains(k)) {
//                log.info("tag：{} 替换 {} - > {} ", tag, k, v);
                t = t.replace(k, v);
            }
        }
        t = t.replace("〔", "·")
                .replace("〕", "")
                .replace("・", "·")
        ;
        return t;
    }
}
