package com.gin.pixivcrawler.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @author bx002
 * @date 2021/2/18 11:52
 */
public interface PixivFileService {

    TreeMap<String, String> getFileMap(int limit);

    /**
     * 归档文件
     *
     * @param pidCollection pid集合
     * @author bx002
     * @date 2021/2/19 10:04
     */
    HashMap<String, List<String>> archive(Collection<String> pidCollection);

    /**
     * 删除文件
     *
     * @param pidCollection pid集合
     * @return java.util.HashMap<java.lang.String, java.util.List < java.lang.String>>
     * @author bx002
     * @date 2021/2/19 10:52
     */
    HashMap<String, List<String>> del(Collection<String> pidCollection);
}
