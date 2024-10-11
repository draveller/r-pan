package com.imooc.pan.server.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pan.server.modules.user.entity.RPanUserSearchHistory;

import java.util.List;

/**
* @author 18063
* @description 针对表【r_pan_user_search_history(用户搜索历史表)】的数据库操作Service
* @createDate 2024-09-28 14:06:46
*/
public interface IUserSearchHistoryService extends IService<RPanUserSearchHistory> {

    List<String> getUserSearchHistories(Long userId);

}
