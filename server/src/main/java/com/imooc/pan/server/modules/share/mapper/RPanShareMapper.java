package com.imooc.pan.server.modules.share.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pan.server.modules.share.entity.RPanShare;
import com.imooc.pan.server.modules.share.vo.RPanShareUrlListVO;

import java.util.List;

/**
 * @author 18063
 * @description 针对表【r_pan_share(用户分享表)】的数据库操作Mapper
 * @createDate 2024-09-28 14:14:15
 * @Entity com.imooc.pan.server.modules.share.entity.RPanShare
 */
public interface RPanShareMapper extends BaseMapper<RPanShare> {

    /**
     * 查询用户的分享列表
     */
    List<RPanShareUrlListVO> selectShareVOListByUserId(Long userId);

    /**
     * 滚动查询已存在的分享id集合
     */
    List<Long> rollingQueryShareId(long startId, long limit);

}




