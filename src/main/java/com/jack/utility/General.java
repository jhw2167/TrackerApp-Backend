package com.jack.utility;



public class General {

    public static int mergeHash(int hash1, int hash2) {
        String toHash = String.format("%032d", hash1)+ String.format("%032d", hash2);
         return Math.abs(toHash.hashCode());
    }

}
