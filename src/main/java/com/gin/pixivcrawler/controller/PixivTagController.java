package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.PixivTagService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/5 16:25
 */
@RequestMapping("tag")
@RestController
public class PixivTagController {
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
    public Res<HashMap<String, String>> getDic() {
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
        return Res.success(pixivTagService.findListBy(mode, keyword));
    }
}
