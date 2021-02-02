package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.PixivUserDao;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/2 14:00
 */
@Service
@Slf4j
public class PixivUserServiceImpl extends ServiceImpl<PixivUserDao,PixivUser> implements PixivUserService {
    @Override
    public boolean saveOne(PixivUser entity) {
        return save(entity);
    }

    @Override
    public PixivUser findOne(Serializable id) {
        return getById(id);
    }

    @Override
    public boolean updateOne(PixivUser entity) {
        return updateById(entity);
    }

    @Override
    public List<PixivUser> findList(Collection<Serializable> idCollection) {
        return null;
    }


}