package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.service.ConfigService;
import com.gin.pixivcrawler.service.FanboxItemService;
import com.gin.pixivcrawler.service.queryService.DownloadQueryService;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItemsBodyImage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Gin
 */
@RestController
@RequestMapping("fanbox")
public class FanboxController {
    private final FanboxItemService fanboxItemService;
    private final DownloadQueryService downloadQueryService;
    private final ConfigService configService;

    public FanboxController(FanboxItemService fanboxItemService, DownloadQueryService downloadQueryService, ConfigService configService) {
        this.fanboxItemService = fanboxItemService;
        this.downloadQueryService = downloadQueryService;
        this.configService = configService;
    }

    @RequestMapping("findItem")
    public void findItem(long id){
        String rootPath = configService.getConfig().getRootPath();

        FanboxItem item = fanboxItemService.findItem(id);
        List<FanboxItemsBodyImage> images = item.getBody().getImages();
        for (int i = 0; i < images.size(); i++) {
            FanboxItemsBodyImage image = images.get(i);
            downloadQueryService.saveOne(image.getId(),
                    String.format("%s/fanbox/%s/[%d] %s", rootPath, item.getCreatorId(), item.getId(), item.getTitle()),
                    String.format("%d - %s.%s", i, image.getId(), image.getExtension()),
                    image.getOriginalUrl(),
                    "fanbox",
                    10
            );
        }
    }
}
