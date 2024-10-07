package com.imooc.pan.server.modules.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThirdPartyProviderEnum {

    GITHUB("github"),
    WECHAT("wechat"),
    GOOGLE("google");

    private final String provider;

}
