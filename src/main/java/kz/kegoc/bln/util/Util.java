package kz.kegoc.bln.util;

import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Util {
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <K, V> Function<K, V> first(Function<K, V> f) {
        return f;
    }
}
