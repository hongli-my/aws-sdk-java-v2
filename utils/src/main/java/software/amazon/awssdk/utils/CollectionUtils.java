/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Returns a new list containing the second list appended to the first list.
     */
    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> merged = new LinkedList<>();
        if (list1 != null) {
            merged.addAll(list1);
        }
        if (list2 != null) {
            merged.addAll(list2);
        }
        return merged;
    }

    /**
     * @param list List to get first element from.
     * @param <T> Type of elements in the list.
     * @return The first element in the list if it exists. If the list is null or empty this will
     * return null.
     */
    public static <T> T firstIfPresent(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * Perform a deep copy of the provided map of lists. This only performs a deep copy of the map and lists. Entries are not
     * copied, so care should be taken to ensure that entries are immutable if preventing unwanted mutations of the elements is
     * desired.
     */
    public static <T, U> Map<T, List<U>> deepCopyMap(Map<T, ? extends List<U>> map) {
        return deepCopyMap(map, () -> new LinkedHashMap<>(map.size()));
    }

    /**
     * Perform a deep copy of the provided map of lists. This only performs a deep copy of the map and lists. Entries are not
     * copied, so care should be taken to ensure that entries are immutable if preventing unwanted mutations of the elements is
     * desired.
     */
    public static <T, U> Map<T, List<U>> deepCopyMap(Map<T, ? extends List<U>> map, Supplier<Map<T, List<U>>> mapConstructor) {
        Map<T, List<U>> result = mapConstructor.get();
        map.forEach((k, v) -> result.put(k, new ArrayList<>(v)));
        return result;
    }

    public static <T, U> Map<T, List<U>> unmodifiableMapOfLists(Map<T, List<U>> map) {
        return new UnmodifiableMapOfLists<>(map);
    }

    /**
     * Perform a deep copy of the provided map of lists, and make the result unmodifiable.
     *
     * This is equivalent to calling {@link #deepCopyMap} followed by {@link #unmodifiableMapOfLists}.
     */
    public static <T, U> Map<T, List<U>> deepUnmodifiableMap(Map<T, ? extends List<U>> map) {
        return unmodifiableMapOfLists(deepCopyMap(map));
    }

    /**
     * Perform a deep copy of the provided map of lists, and make the result unmodifiable.
     *
     * This is equivalent to calling {@link #deepCopyMap} followed by {@link #unmodifiableMapOfLists}.
     */
    public static <T, U> Map<T, List<U>> deepUnmodifiableMap(Map<T, ? extends List<U>> map,
                                                             Supplier<Map<T, List<U>>> mapConstructor) {
        return unmodifiableMapOfLists(deepCopyMap(map, mapConstructor));
    }


    /**
     * Collect a stream of {@link Map.Entry} to a {@link Map} with the same key/value types
     * @param <K> the key type
     * @param <V> the value type
     * @return a map
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, VInT, VOutT> Map<K, VOutT> mapValues(Map<K, VInT> inputMap, Function<VInT, VOutT> mapper) {
        return inputMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> mapper.apply(e.getValue())));
    }

    /**
     * Return a new map that is the inverse of the supplied map, with the values becoming the keys
     * and vice versa. Requires the values to be unique.
     *
     * @param inputMap a map where both the keys and values are unique
     * @param <K> the key type
     * @param <V> the value type
     * @return a map
     */
    public static <K, V> Map<K, V> inverseMap(Map<V, K> inputMap) {
        return inputMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    /**
     * For a collection of values of type {@code V} that can all be converted to type {@code K}, create a map that
     * indexes all of the values by {@code K}. This requires that no two values map to the same index.
     * 
     * @param values the collection of values to index
     * @param indexFunction the function used to convert a value to its index
     * @param <K> the index (or key) type
     * @param <V> the value type
     * @return a (modifiable) map that indexes K to its unique value V
     * @throws IllegalArgumentException if any of the values map to the same index
     */
    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> indexFunction) {
        Map<K, V> map = new HashMap<>();
        for (V value : values) {
            K index = indexFunction.apply(value);
            V prev = map.put(index, value);
            Validate.isNull(prev, "No duplicate indices allowed but both %s and %s have the same index: %s",
                            prev, value, index);
        }
        return map;
    }
}
