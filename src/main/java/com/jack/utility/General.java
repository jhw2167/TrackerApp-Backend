package com.jack.utility;



public class General {

    /* Simple Utility */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /* Algorithm Utility */
    public static int mergeHash(int hash1, int hash2) {
        String toHash = String.format("%032d", hash1)+ String.format("%032d", hash2);
         return Math.abs(toHash.hashCode());
    }


}
