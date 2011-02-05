package net.sf.timecult.util;

/**
 * Additional string manipulation methods.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 *
 */
public class StringHelper {

	public enum AddMode {
		TO_LEFT, TO_RIGHT
	};

	public static String addSpaces(String str, int len, AddMode mode) {
		StringBuffer strBuf = new StringBuffer();
		if (mode.equals(AddMode.TO_RIGHT)) {
			strBuf.append(str);
		}
		for (int i = 0; i < len - str.length(); i++) {
			strBuf.append(' ');
		}
		if (mode.equals(AddMode.TO_LEFT)) {
			strBuf.append(str);
		}
		return strBuf.toString();
	}
}
