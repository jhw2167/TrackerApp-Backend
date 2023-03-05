package com.jack.utility;

import java.math.BigInteger;

public class General {

    public static int mergeHash(int hash1, int hash2) {
        String toHash = String.format("%032d", hash1)+ String.format("%032d", hash2);
         return toHash.hashCode();
    }

}
