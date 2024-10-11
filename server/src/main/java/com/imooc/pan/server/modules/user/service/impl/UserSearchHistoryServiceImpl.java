package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.user.entity.RPanUserSearchHistory;
import com.imooc.pan.server.modules.user.mapper.RPanUserSearchHistoryMapper;
import com.imooc.pan.server.modules.user.service.IUserSearchHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
 * @createDate 2024-09-28 14:06:46
 */
@Service
public class UserSearchHistoryServiceImpl extends ServiceImpl<RPanUserSearchHistoryMapper, RPanUserSearchHistory>
        implements IUserSearchHistoryService {

    @Override
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




