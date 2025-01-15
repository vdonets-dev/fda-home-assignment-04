package com.fda.home.converter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BiConverter<I, O> implements Converter<I, O> {
    @Override
    public O convert(I input) {
        return doConvert(input);
    }

    protected abstract O doConvert(I input);
}
