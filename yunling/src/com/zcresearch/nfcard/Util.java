package com.zcresearch.nfcard;

import java.util.Locale;

public final class Util {
	private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static final int ISO_PADDING_1 = 0;
	public static final int ISO_PADDING_2 = 1;

	public static final Boolean isDebug = true;

	public static void echo(String str) {
		if (isDebug) {
			System.out.println(str);
		}
	}

	public static byte[] toBytes(int a) {
		return new byte[] { (byte) (0x000000ff & (a >>> 24)),
				(byte) (0x000000ff & (a >>> 16)),
				(byte) (0x000000ff & (a >>> 8)), (byte) (0x000000ff & (a)) };
	}

	public static String bytesToHexString(byte[] d, int s, int n) {

		if (d == null) {
			return null;
		}

		if (s < 0) {
			return null;
		}

		if (n < 1) {
			return null;
		}

		int len = d.length;
		if (len < (s + n)) {
			return null;
		}

		final char[] ret = new char[n * 2];
		final int e = s + n;
		int x = 0;

		for (int i = s; i < e; ++i) {

			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}

		return new String(ret);
	}

	public static byte[] byteToByteHex(byte[] data, short start, short length) {

		if (data == null) {
			return null;
		}

		if (start < 0) {
			return null;
		}

		if (length < 1) {
			return null;
		}

		int len = data.length;
		if (len < start + length) {
			return null;
		}

		int i;
		short temp;
		byte[] tempdata = new byte[length];

		for (i = 0; i < length; i++) {

			temp = data[start + i];
			if ((temp >= 0) && (temp <= 9)) {
				tempdata[i] = (byte) ((temp + 48) & 0xFF);
			} else if ((temp >= 10) && (temp <= 15)) {
				tempdata[i] = (byte) ((temp + 55) & 0xFF);
			} else {
				return null;
			}
		}

		return tempdata;
	}

	public static int parseInt(String txt, int radix, int def) {
		int ret;
		try {
			ret = Integer.valueOf(txt, radix);
		} catch (Exception e) {
			ret = def;
		}

		return ret;
	}

	public static String toHexString(byte[] d, int s, int n) {
		final char[] ret = new char[n * 2];
		final int e = s + n;

		int x = 0;
		for (int i = s; i < e; ++i) {
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	public static String toHexStringR(byte[] d, int s, int n) {
		final char[] ret = new char[n * 2];

		int x = 0;
		for (int i = s + n - 1; i >= s; --i) {
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	public static int toInt(byte[] b, int s, int n) {
		int ret = 0;

		final int e = s + n;
		for (int i = s; i < e; ++i) {
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}

	public static int toIntR(byte[] b, int s, int n) {
		int ret = 0;

		for (int i = s; (i >= 0 && n > 0); --i, --n) {
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}

	public static byte[] Padding(byte[] data, int iMode) throws Exception {
		byte[] byteRet = null;
		String szData = Util.byte2hex(data);
		switch (iMode) {
		case ISO_PADDING_1: {
			while (szData.length() % 16 != 0) {
				szData += "00";
			}
			byteRet = Util.hex2byte(szData);
		}
			break;
		case ISO_PADDING_2: {
			szData += "80";
			while (szData.length() % 16 != 0) {
				szData += "00";
			}
			byteRet = Util.hex2byte(szData);
		}
			break;
		default:
			throw IllegalArgumentException("Padding error");
		}

		return byteRet;
	}

	private static Exception IllegalArgumentException(String string) {
		return null;
	}

	public static byte[] SubBytes(byte[] byteSrc, int iStart, int iEnd) {
		byte[] byteRet = new byte[iEnd - iStart];
		for (int iPos = iStart; iPos < iEnd; iPos += 1) {
			byteRet[iPos - iStart] = byteSrc[iPos];
		}
		return byteRet;
	}

	public static String byte2hex(byte[] b, int len) {
		StringBuffer sb = new StringBuffer();
		String tmp = "";
		for (int i = 0; i < len; i++) {
			tmp = Integer.toHexString(b[i] & 0XFF);
			if (tmp.length() == 1) {
				sb.append("0" + tmp);
			} else {
				sb.append(tmp);
			}
		}
		String str = sb.toString();
		str = str.toUpperCase(Locale.CHINESE);
		return str;
	}

	public static String byte2hex(byte[] b) {
		return byte2hex(b, b.length);
	}

	public static byte[] hex2byte(String str) {
		if (str == null) {
			return null;
		}
		int len = str.length();

		if (len == 0) {
			return null;
		}
		if (len % 2 == 1) {
			throw new IllegalArgumentException("length error");
		}

		byte[] b = new byte[len / 2];
		for (int i = 0; i < str.length(); i += 2) {
			b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2))
					.intValue();
		}
		return b;
	}

	static public void arrayXOR(byte[] des, int desoffset, byte[] src,
			int srcoffset, int len) {
		int i;
		for (i = 0; i < len; i++) {
			des[desoffset + i] = (byte) (des[desoffset + i] ^ src[srcoffset + i]);
		}
	}

	static public byte[] arrayNOT(byte[] des, int len) {
		int i;
		byte[] b = new byte[len];
		for (i = 0; i < len; i++) {
			b[i] = (byte) (des[+i] ^ 0xFF);
		}

		return b;
	}

	static public int Bytes2Int(byte[] bArray) {
		return Bytes2Int(bArray, 16);
	}

	static public boolean convertIntToBoolean(int intValue) {
		return (intValue != 0);
	}

	static public int Bytes2Int(byte[] bArray, int base) {
		return Bytes2Int(bArray, bArray.length, base);
	}

	static public int Bytes2Int(byte[] bArray, int iLen, int base) {
		String szBalance = byte2hex(bArray, iLen);
		int sum = Integer.valueOf(szBalance, base);
		return sum;
	}

	public static String tagTrace(short sTag, byte[] buffer, int iOffset,
			int iLen, String charsetName, boolean IsHex) {
		int iMove;
		int iTagValueLen;
		short sTempTag;

		while (iLen > 0) {
			sTempTag = sGetTag(buffer, iOffset);
			iMove = sGetTagLen(sTempTag);
			iOffset += iMove;

			if (sTempTag == (short) 0x70) {
				if (buffer[iOffset] == (byte) 0x81) {
					iLen = (short) (iLen - iMove - 2);
					iOffset += (short) 2;
				} else {
					iLen = (short) (iLen - iMove - 1);
					iOffset++;
				}
				continue;
			}

			byte byTemp = buffer[iOffset];
			if (byTemp == (byte) 0x81) {
				iTagValueLen = (short) (buffer[++iOffset] & 0x00ff);
				iOffset++;
			} else {
				iTagValueLen = (short) (byTemp & 0x00ff);
				iOffset++;
			}

			if (sTempTag == sTag) {

				try {
					String sztemp = null;
					if (IsHex) {
						sztemp = String.format("%02X", buffer[iOffset]);
						for (int iT = 1; iT < iTagValueLen; iT++) {
							sztemp = sztemp.concat(String.format("%02X",
									buffer[iOffset + iT]));

						}

					} else {
						sztemp = new String(copyOfRange(buffer, iOffset,
								iOffset + iTagValueLen), charsetName);
					}
					return sztemp;
				} catch (Exception e) {
					return null;
				}
			}

			iMove = (short) (iTagValueLen + 1 + iMove);
			iOffset = (short) (iTagValueLen + iOffset);
			if (iTagValueLen >= 128) {
				iMove++;
				iOffset++;
			}
			iLen = (short) (iLen - iMove);
		}

		return null;
	}

	static short sGetTag(byte[] buffer, int iOffset) {
		short sTag;
		if ((byte) (buffer[iOffset] & (byte) 0x1F) == (byte) 0x1F) {
			sTag = (short) toInt(buffer, iOffset, 2);
		} else {
			sTag = (short) buffer[iOffset];
		}
		return sTag;
	}

	static short sGetTagLen(short sTag) {
		if (((short) (sTag & (short) 0xff00)) != (short) 0)
			return (short) 2;
		return (short) 1;
	}

	public static int[] copyOf(int[] original, int newLength) {
		int[] copy = new int[newLength];
		System.arraycopy(original, 0, copy, 0,
				Math.min(original.length, newLength));
		return copy;
	}

	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0,
				Math.min(original.length - from, newLength));
		return copy;
	}

}
