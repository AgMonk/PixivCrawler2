package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * @author bx002
 * @date 2021/1/25 14:03
 */
@NoArgsConstructor
public class List {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    public void add(String key, Object obj) {
        map.put(key, obj);
    }

    public List getList(int index, String key) {
        key = ">>".repeat(index) + key;
        List list = (List) map.getOrDefault(key, new List());
        map.put(key, list);
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[list]");
        map.forEach((k, v) -> {
            sb.append("\n[*]");
            if (v instanceof List) {
                sb.append(k);
            }
            sb.append(v.toString());
        });
        sb.append("\n[/list]");
        return sb.toString();
    }

    public static void main(String[] args) {
        java.util.List<String> typeList = Arrays.asList("考据>大型活动>镜像论", "同人>漫画/插画2021", "新闻>活动直播", "考据>大型活动>双联乱数", "新闻>追放");
        List list = new List();
        for (String types : typeList) {
            List tar = list;
            String[] type = types.split(">");
            int last = type.length - 1;
            for (int i = 0; i < last; i++) {
                tar = tar.getList(i + 1, type[i]);
            }
            tar.add(type[last], ">".repeat(last + 1) + type[last]);
        }
        System.out.println(list);
    }
}