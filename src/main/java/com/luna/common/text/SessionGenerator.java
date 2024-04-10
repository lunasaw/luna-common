package com.luna.common.text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * sessionId生成方式
 */
public class SessionGenerator {

    private static final Map<String, UUID> cache       = new HashMap<String, UUID>();
	private final static String DEFAULT_KEY = "DEFAULT"; 
	
	private static UUID getDefaultUUID() {
		UUID u = cache.get(DEFAULT_KEY);
		
		if(u == null) {
			u = UUID.randomUUID();
			cache.put(DEFAULT_KEY, u);
		}
		return u;
	}

	/**
	 * 去掉UUID小号性能的算法,自定义一个不重复的sessionId生成器
	 * @return
	 */
	public static String generateSessionId() {
		UUID u = getDefaultUUID();
		String prefix 	= toHex(u.getLeastSignificantBits(), 16);
		String key0 	= toHex((Thread.currentThread().getId()), 4);
		String key1 	= toHex(System.currentTimeMillis(), 8);
		String key2 	= toHex(System.nanoTime(), 8);
		return prefix + key0 + key1 + key2;
	}
	
	private static String toHex(long i, int len) {
		char[] buf = new char[64];
		Arrays.fill(buf, '0');
		int charPos = 64;
		int radix = 1 << 4;
		long mask = radix - 1;
		do {
		    buf[--charPos] = digits[(int)(i & mask)];
		    i >>>= 4;
		} while (i != 0);
		return new String(buf, (64 - len), len);
	}
	
    final static char[] digits = {
    	'0' , '1' , '2' , '3' , '4' , '5' ,
    	'6' , '7' , '8' , '9' , 'a' , 'b' ,
    	'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
    	'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
    	'o' , 'p' , 'q' , 'r' , 's' , 't' ,
    	'u' , 'v' , 'w' , 'x' , 'y' , 'z'
    };
    
	public static void main(String[] a) {
		System.out.println(toHex(0x112, 10));
		System.out.println(toHex(0x131412, 3));
		System.out.println(generateSessionId());
	}
	
}
