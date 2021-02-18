package com.gin.pixivcrawler.service;

import java.util.TreeMap;

/**
 * @author bx002
 * @date 2021/2/18 11:52
 */
public interface PixivFileService {

    TreeMap<String, String> getFileMap(int limit);
}
