package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.service.ConfigService;
import com.gin.pixivcrawler.service.FanboxItemService;
import com.gin.pixivcrawler.service.queryService.DownloadQueryService;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItemsBodyFile;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItemsBodyImage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
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
        if (images!=null) {
            download(rootPath, item, images);
        }

        HashMap<String, FanboxItemsBodyImage> imageMap = item.getBody().getImageMap();
        if (imageMap!=null) {
            download(rootPath, item, new ArrayList<>(imageMap.values()));
        }

        HashMap<String, FanboxItemsBodyFile> fileMap = item.getBody().getFileMap();
        if (fileMap!=null) {
            ArrayList<FanboxItemsBodyFile> files = new ArrayList<>(fileMap.values());
            for (int i = 0; i < files.size(); i++) {
                FanboxItemsBodyFile file = files.get(i);
                downloadQueryService.saveOne(file.getId(),
                        String.format("%s/fanbox/%s/[%d] %s", rootPath, item.getCreatorId(), item.getId(), item.getTitle()),
                        String.format("%d - %s.%s", i, file.getId(), file.getExtension()),
                        file.getUrl(),
                        "fanbox",
                        10
                );
            }
        }


    }

    private void download(String rootPath, FanboxItem item, List<FanboxItemsBodyImage> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
            FanboxItemsBodyImage image = imageList.get(i);
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
