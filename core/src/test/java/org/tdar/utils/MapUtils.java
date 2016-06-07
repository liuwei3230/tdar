package org.tdar.utils;

import java.util.*;
import java.util.function.Supplier;

/**
 * A handful of static methods to assist in quick creation of maps and lists.
 */
public class MapUtils {

    // Use the same seed value so that pseudo-random data is repeatable.
    private static Random random = new Random(1L);


    /**
     * Generate list of pseudorandom size. Not thread safe.
     */
    public static <T> List<T> generateItems(int minSize, int maxSize, Supplier<T> supplier ) {
        List<T> list = new ArrayList<>();
        int length = minSize;
        if(minSize < maxSize) {
            length = (random.nextInt() % (maxSize - minSize)) + minSize;
        }
        for(int i = 0; i < length; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    /**
     * Return a list of specified size containing elements from the specified generator.
     */
    public static <T> Iterable<T> generateItems(int size, Supplier<T> supplier) {
        return generateItems(size, size, supplier);
    }

    /**
     * Returns immutable map from an array of map entries. Map entries must not be null.
     *
     * Use in conjunction with {@link MapUtils#entry}.
     */
    public static <K, U> Map<K, U> newMap(Map.Entry<K, U>... entries) {
        //return Stream.of(entries).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
        Map<K, U> map = new HashMap<>();
        for(Map.Entry<K, U> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * Convenience method for creating {@link java.util.Map.Entry} objects.
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }


}
