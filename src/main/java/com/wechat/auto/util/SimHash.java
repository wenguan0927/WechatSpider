package com.wechat.auto.util;

import java.math.BigInteger;
import java.util.HashMap;


/**
 * 计算海明距离
 * 参考博客：https://www.cnblogs.com/shaosks/p/9121774.html
 */
public class SimHash {

    public static final int HASH_BITS = 128;

    private HashMap<String,Integer> mTokens;

    private BigInteger mSimHashValue;

    public SimHash(HashMap<String, Integer> tokens){
        this.mTokens = tokens;
        this.mSimHashValue = simHash();
    }

    public BigInteger getSimHashValue(){
        return mSimHashValue;
    }

    private BigInteger simHash() {
        int[] v = new int[HASH_BITS];

        for(String key: mTokens.keySet())
        {
            int weight = mTokens.get(key);
            BigInteger t = this.hash(key);
            for (int i = 0; i < HASH_BITS; i++){
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1 * weight;//加权
                }
                else {
                    v[i] -= 1 * weight;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < HASH_BITS; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        }
        else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(HASH_BITS).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * @Author：sks
     * @Description：计算海明距离
     * @leftSimHash,rightSimHash:要比较的信息指纹
     * @hashbits：128
     */
    public static int hammingDistance(BigInteger leftSimHash, BigInteger rightSimHash){
        BigInteger m = new BigInteger("1").shiftLeft(HASH_BITS).subtract(
                new BigInteger("1"));
        BigInteger x = leftSimHash.xor(rightSimHash).and(m);
        int count = 0;
        while (x.signum() != 0) {
            count += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return count;
    }
}
