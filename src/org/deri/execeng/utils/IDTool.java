package org.deri.execeng.utils;
/**
 * Tool for generating random IDs.
 * @author rfuller
 *
 */
public class IDTool {
	/**
	 * Generate a random ID with this prefix.
	 * @param prefix
	 * @return an id consisting of prefix+{timestamp}.{random number}
	 */
	public static String generateRandomID(String prefix){
		return prefix+System.currentTimeMillis()+"."+(Math.random()*1000000);		
	}
}
