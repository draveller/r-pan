package com.imooc.pan.server.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.user.entity.RPanUserSearchHistory;
import com.imooc.pan.server.modules.user.mapper.RPanUserSearchHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:06:46
 */
@Service
public class UserSearchHistoryService extends ServiceImpl<RPanUserSearchHistoryMapper, RPanUserSearchHistory>
        implements IService<RPanUserSearchHistory> {

    public List<String> getUserSearchHistories(Long userId) {
        return this.lambdaQuery()
                .eq(RPanUserSearchHistory::getUserId, userId)
                .orderByDesc(RPanUserSearchHistory::getCreateTime)
                .last(" limit 10")
                .list()
                .stream()
                .map(RPanUserSearchHistory::getSearchContent)
                .distinct()
                .toList();
    }

}




