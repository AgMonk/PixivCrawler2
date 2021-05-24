package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.FanboxCookieDao;
import com.gin.pixivcrawler.dao.FanboxItemDao;
import com.gin.pixivcrawler.utils.fanboxUtils.FanboxCookie;
import com.gin.pixivcrawler.utils.fanboxUtils.FanboxPost;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxResponseBody;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bx002
 */
@Service
public class FanboxItemServiceImpl extends ServiceImpl<FanboxItemDao, FanboxItem> implements FanboxItemService {

    private final FanboxCookieDao fanboxCookieDao;


    public FanboxItemServiceImpl(FanboxCookieDao fanboxCookieDao) {
        this.fanboxCookieDao = fanboxCookieDao;
    }

    @Override
    public List<FanboxItem> listSupporting(int limit) {
        FanboxCookie fanboxCookie = fanboxCookieDao.selectById(1);
        String cookie = fanboxCookie.getCookie();
        FanboxResponseBody fanboxResponseBody = FanboxPost.listSupporting(cookie, limit);
        if (fanboxResponseBody == null) {
            return null;
        }

        List<FanboxItem> items = fanboxResponseBody.getItems();
        if (items.size()==0) {
            return null;
        }
        List<Long> existsId = items.stream().map(FanboxItem::getId).collect(Collectors.toList());
        List<Long> idList = listByIds(existsId).stream().map(FanboxItem::getId).collect(Collectors.toList());

        items.removeIf(item -> idList.contains(item.getId()) || item.getBody() == null);

        saveBatch(items);
        return items;
    }

    @Override
    public FanboxItem findItem(long id) {
        FanboxCookie fanboxCookie = fanboxCookieDao.selectById(1);
        String cookie = fanboxCookie.getCookie();

        FanboxItem item = FanboxPost.findItem(cookie, id);
        save(item);
        return item;
    }

}
