package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.user.entity.RPanThirdPartyAuth;
import com.imooc.pan.server.modules.user.mapper.RPanThirdPartyAuthMapper;
import com.imooc.pan.server.modules.user.service.IThirdPartyAuthService;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyAuthServiceImpl extends ServiceImpl<RPanThirdPartyAuthMapper, RPanThirdPartyAuth>
        implements IThirdPartyAuthService {

}
