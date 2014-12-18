package com.mak001.ircBot.methods;

public class StringMethods {

	/**
	 * @param s
	 *            The String to cut
	 * @param beginChar
	 *            The last set of characters(originally a character, but it
	 *            would have been to broad) before the cut
	 * @return The shortened String
	 */
	public static String truncate(String s, String beginChar) {
		if (s.contains(beginChar))
			return s.replace(s.subSequence(s.indexOf(beginChar), s.length()),
					"");
		return s;
	}

	/**
	 * @param s
	 *            The String to cut
	 * @param endChar
	 *            The last set of characters(originally a character, but it
	 *            would have been to broad) after the cut
	 * @return The shortened String
	 */
	public static String cutBeginning(String s, String endChar) {
		if (s.contains(endChar))
			return s.replace(
					s.subSequence(0, s.indexOf(endChar) + endChar.length()), "");
		return s;
	}

	/**
	 * 
	 * @param base
	 *            - The String to work with
	 * @param toReplace
	 *            - The String to replace
	 * @param replacement
	 *            - The replacement String
	 * @return 
	 */
	public static String replaceIgnoreCase(String base, String toReplace,
			String replacement) {
		return base.replaceAll("(?i)" + toReplace, replacement);
	}

	/**
	 * Puts two String Arrays together
	 * 
	 * @param A
	 *            - The first String array
	 * @param B
	 *            - The second String array
	 * @return - Array A and Array B smashed into one String Array
	 */
	public static String[] addTwoArrays(String[] A, String[] B) {
		String[] C = new String[A.length + B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
		return C;
	}

}
