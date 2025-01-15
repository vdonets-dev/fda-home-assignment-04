package com.fda.home.converter;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Stream;

@FunctionalInterface
public interface Converter<I, O> {
    O convert(I input);

    default <C extends Collection<O>> C convertMultiple(Collection<I> input, Collector<O, ?, C> collector) {
        if (input == null) {
            return Stream.<O>empty().collect(collector);
        }

        return input.stream()
                .map(this::convert)
                .collect(collector);
    }
}
