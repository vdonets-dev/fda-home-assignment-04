package com.fda.home.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CacheNames {
    OPEN_FDA_CACHE("openFdaCache");

    private final String name;
}
