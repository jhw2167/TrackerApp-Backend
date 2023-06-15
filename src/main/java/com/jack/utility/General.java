package com.jack.utility;



public class General {

    /* Simple Utility */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /* Algorithm Utility */

    /**
     * Creates a String by merging two longs at constant length of 19
     * and then hashes the result into a unique hash
     * @param hash1
     * @param hash2
     * @return
     */
    public static int mergeHash(long hash1, long hash2) {
        String toHash = String.format("%019d", hash1)+ String.format("%019d", hash2);
         return Math.abs(toHash.hashCode());
    }


}
