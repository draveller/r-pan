package com.imooc.pan.server.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.user.entity.RPanThirdPartyAuth;
import com.imooc.pan.server.modules.user.mapper.RPanThirdPartyAuthMapper;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyAuthService extends ServiceImpl<RPanThirdPartyAuthMapper, RPanThirdPartyAuth>
        implements IService<RPanThirdPartyAuth> {

}
