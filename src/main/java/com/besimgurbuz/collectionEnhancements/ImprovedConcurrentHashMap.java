package com.besimgurbuz.collectionEnhancements;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ImprovedConcurrentHashMap {
    /*
    The ConcurrentHashMap class was introduced to provide a more modern HashMap, which is also
    concurrency friendly. ConcurrentHashMap allows concurrent add and update operations that lock
    only certain parts of the internal data structure. Thus, read and write operations have improved
    performance compared with the synchronized Hashtable alternative.
    (Note that the standard HashMap is un-synchronized.)

    Reduce and Search
    ConcurrentHashMap supports three new kinds of operations, reminiscent of what you saw with steams:
        -forEach --Performs a given action for each (key, value)
        -reduce --Combines all(key, value) given reduction function into a result
        -search --Applies a function on each (key, value) until the function produces a non-null result

    Each kind of operation supports four forms, accepting functions with keys, values, Map.Entry, and
    (key, value) arguments:

        - Operates with key and values (forEach, reduce, search)
        - Operates with keys (forEachKey, reduceKeys, searchKeys)
        - Operates with values (forEachValue, reduceValues, searchValues)
        - Operates with Map.Entry objects (forEachEntry, reduceEntries, searchEntries)

    Note that these operations don’t lock the state of the ConcurrentHashMap; they operate on the
    elements as they go along. The functions supplied to these operations shouldn’t depend on any
    ordering or on any other objects or values that may change while computation is in progress.
    In addition, you need to specify a parallelism threshold for all these operations. The operations
    execute sequentially if the current size of the map is less than the given threshold. A value of
    1 enables maximal parallelism using the common thread pool. A threshold value of Long.MAX_VALUE
    runs the operation on a single thread. You generally should stick to these values unless your
    software architecture has advanced resource-use optimization.
        In this example, you use the reduceValues method to find the maximum value in
    the map:
     */
    public static void main(String[] args) {
        ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
        map.put("k1", 10L);
        map.put("k2", 30L);
        map.put("k3", -2L);
        long parallelismThreshold = 1;

        Optional<Long> maxValue =
                Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
        maxValue.ifPresent(System.out::println);
        /*
        Note the primitive specializations for int, long, and double for each reduce operation
        (reduceValuesToInt, reduceKeysToLong, and so on), which are more efficient, as they prevent
        boxing.

        Counting
        The ConcurrentHashMap class provides a new method called mappingCount , which
        returns the number of mappings in the map as a long. You should use it for new code
        in preference to the size method, which returns an int. Doing so future proofs your
        code for use when the number of mappings no longer fits in an int.

        Set views
        The ConcurrentHashMap class provides a new method called keySet that returns a
        view of the ConcurrentHashMap as a Set. (Changes in the map are reflected in the Set,
        and vice versa.) You can also create a Set backed by a ConcurrentHashMap by using the
        new static method new KeySet.
         */
    }
}
