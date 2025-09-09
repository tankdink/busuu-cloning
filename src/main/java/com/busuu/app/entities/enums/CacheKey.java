package com.busuu.app.entities.enums;

import lombok.Getter;

@Getter
public enum CacheKey {

    USER_PRINCIPAL ("user.principal");

    final String name;

    CacheKey(String name) {
        this.name = name;
    }
}
