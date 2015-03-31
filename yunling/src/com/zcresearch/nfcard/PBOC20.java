package com.zcresearch.nfcard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.content.res.Resources;
import android.util.Log;

final class PBOC20 {
	private final static byte[] CUP_RID = { (byte) 0xA0, (byte) 0x00,
			(byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x01 };

	private final static String sAID = "A0000003330101";
	private final static int SWOK = 0x9000;

	private static String m_sAuthMount = null;

	private static String m_PAN = null;
	private static String m_DDA = null;

	private static String m_sPDOL = null;
	private static String m_sCDOL1 = null;
	private static String m_sCDOL2 = null;

	private static String m_sATC = null;
	private static String m_sARQC = null;

	private static String m_sAIP = null;
	private final static String m_s9F66 = "40000000";
	private final static String m_s9F7A = "01";

	private final static String m_sDF69 = "00";
	private final static String m_sDF60 = "00";

	private final static String m_s9F4E = "E58D97E4BAACE993B6E8A18C0000000000000000";

	private final static String m_s9F03 = "000000000000";
	private final static String m_s9F1A = "0156";

	private final static String m_s95 = "0000000000";
	private final static String m_s5F2A = "0156";
	private final static String m_s9C = "60";
	private final static String m_s9F33 = "604000";

	private static String m_s9A = null;
	private static String m_sDate = null;
	private static String m_s9F21 = null;
	private static String m_s9F37 = null;

	private static String m_s9F27 = null;
	private static String m_s9F10 = null;
	private static String m_s8A = null;
	@SuppressWarnings("unused")
	private static String m_s84 = null;

	private static String m_s9F34 = "020300";
	private static String m_s9F1E = "3032303030303034";

	private static Map<String, String> diMap = new HashMap<String, String>();

	private static String serl;

	private PBOC20(Iso7816_nfc.Tag tag, Resources res) {
	}

	public static String getPan() {
		return m_PAN;
	}

	public static String getDDA() {
		return m_DDA;
	}

	public static String GetCardInfo(Iso7816_nfc.Tag tag) {

		Iso7816_nfc.Response Res;
		tag.close();
		if (!tag.connect())
			return ErrorDef.BUILD_NJ_CONNECT;

		Res = tag.selectByName(CUP_RID);
		if (!Res.isOkey()) {
			return ErrorDef.BUILD_NJ_SELECT_STRING;
		}

		String sResString = ErrorDef.BUILD_NJ_DEFAULT;
		do {
			if (Res.isOkey()) {

				Iso7816_nfc.Response PAN;

				m_PAN = null;

				PAN = tag.readRecord(02, 01);
				if (PAN.isOkey()) {
					parsePAN(PAN);
				} else {
					Log.i("nfc", "Read Record 0201 failed,rv=" + PAN.getSw12());
					sResString = ErrorDef.BUILD_NJ_Read_Pan;
					break;
				}

				if (m_PAN == null) {
					sResString = ErrorDef.BUILD_NJ_DATA_NULL;
					break;
				} else {
					tag.close();
					Log.i("nfc", m_PAN);
					return m_PAN;
				}

			}
		} while (false);

		tag.close();
		return sResString;
	}

	public static String LoadDDA(Iso7816_nfc.Tag tag) {

		tag.close();

		if (!tag.connect())
			return ErrorDef.BUILD_NJ_CONNECT;

		m_PAN = null;
		m_DDA = null;

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.CHINESE);
		String sDataString = df.format(new Date());
		m_sDate = sDataString.substring(0, 8);
		m_s9A = sDataString.substring(2, 8);
		m_s9F21 = sDataString.substring(8, 14);
		m_s9F37 = Get9F37();
		m_sAuthMount = "000000000000";
		Log.i("nfc", "9A " + m_s9A + " 9F21 " + m_s9F21 + " 9F37 " + m_s9F37);
		if (m_sDate == null || m_s9A == null || m_s9F21 == null
				|| m_s9F37 == null) {
			tag.close();
			return ErrorDef.BUILD_NJ_DATA_NULL;
		}

		int rv = Select(tag);
		if (SWOK != rv) {
			Log.i("nfc", "Select Error, ret=" + Integer.toHexString(rv));
			tag.close();
			return ErrorDef.BUILD_NJ_SELECT_STRING;
		}

		diMap.clear();
		GetDGIValue(tag.GetResponse());

		if (SWOK != (rv = GetProcessOption(tag))) {
			Log.i("nfc", "GPO Failed,ret=" + Integer.toHexString(rv));
			tag.close();
			return ErrorDef.BUILD_NJ_GPO_STRING;
		} else {
			m_sAIP = tag.GetRes().substring(4, 8);
			Log.i("nfc", "AIP " + m_sAIP);
			if (m_sAIP == null) {
				Log.i("nfc", "Get AIP failed," + tag.GetRes());
				tag.close();
				return ErrorDef.BUILD_NJ_DATA_NULL;
			}

			String sAFL = tag.GetRes().substring(8);
			if (sAFL == null) {
				Log.i("nfc", "Get AFL failed," + tag.GetRes());
				tag.close();
				return ErrorDef.BUILD_NJ_DATA_NULL;
			}
			Log.i("nfc", "GPO success,then readcord");

			if (!ReadAFL(tag, sAFL)) {
				Log.i("nfc", "Read Record failed");
				tag.close();
				return ErrorDef.BUILD_NJ_READ_DATA;
			}
		}

		m_PAN = diMap.get("5A").toString();

		if (m_PAN == null || m_PAN.equals("")) {
			Log.i("ac", "read pan failed");
			tag.close();
			return ErrorDef.BUILD_NJ_DATA_NULL;
		} else {
			m_PAN = m_PAN.replaceAll("f|F", "");
		}

		// IntAuth
//		if (SWOK != (rv = IntAuth(tag))) {
//			Log.i("nfc", "IntAuth Failed,ret=" + Integer.toHexString(rv));
//			return ErrorDef.BUILD_NJ_DATA_NULL;
//		}
//
//		m_DDA = tag.GetRes();
		m_DDA = "";

		tag.close();

		String sBuild55 = m_PAN + "|" + m_DDA;

		Log.i("nfc",
				"dda len=" + String.format("%1$02X", sBuild55.length() / 2)
						+ " Data=" + sBuild55);
		return sBuild55;

	}

	public static int LoadBalance(Iso7816_nfc.Tag tag, String sLoadInfo) {
		// clear data map
		diMap.clear();

		// ExtAuth
		if (sLoadInfo == null || sLoadInfo.equals("")
				|| sLoadInfo.length() % 2 != 0) {
			Log.i("nfc", "ExecScript String Error");
			tag.close();
			return ErrorDef.ERR_DATA_LENGTH_ZERO;
		}

		byte[] d = Util.hex2byte(sLoadInfo);
		GetDGIValue(d);
		String sExterAuthString = diMap.get("91");
		int rv = -1;

		if (SWOK != (rv = ExternAuth(tag, sExterAuthString))) {
			Log.i("nfc", "ExternAuth Failed,ret=" + Integer.toHexString(rv));
			return ErrorDef.ERR_ExternAuth;
		}

		// GAC2
		m_s8A = sExterAuthString.substring(16, 20);
		if (!GetCDOL(false)) {
			return ErrorDef.ERR_Putdata;
		} else {
			Log.i("nfc", "8D " + m_sCDOL2);
		}

		if (SWOK != (rv = GenerateAC(tag, false))) {
			Log.i("nfc", "GAC2 Failed,ret=" + Integer.toHexString(rv));
			return ErrorDef.ERR_GAC;
		}

		// update balance
		String sPutdataString = diMap.get("86");
		if (SWOK != (rv = PutData(tag, sPutdataString))) {
			Log.i("nfc", "PutData Failed,ret=" + Integer.toHexString(rv));
			if (SWOK != rv && 0x6F00 != rv) {
				return ErrorDef.ERR_NeedReLoad;
			} else {
				return ErrorDef.ERR_Putdata;
			}
		}

		tag.close();
		return 0;
	}

	public static String QueryLog(Iso7816_nfc.Tag tag) {
		tag.close();
		if (!tag.connect())
			return ErrorDef.BuildErr_OpenErr;
		int rv = Select(tag);
		int nSFI = 0;
		if (SWOK != rv) {
			Log.i("nfc", "Select Error, ret=" + Integer.toHexString(rv));
			tag.close();
			return ErrorDef.BuildErr_SelectErr;
		} else {
			diMap.clear();
			GetDGIValue(tag.GetResponse());
			String s9F4D = diMap.get("9F4D");
			if (s9F4D == null) {
				nSFI = 11;
			} else {
				Log.i("nfc", "9F4D = " + s9F4D);
				String sSFI = s9F4D.substring(0, 2);
				nSFI = Integer.parseInt(sSFI, 16);
			}
		}

		Iso7816_nfc.Response PAN;
		PAN = tag.readRecord(02, 01);
		if (PAN.isOkey()) {
			m_PAN = null;
			parsePAN(PAN);
			if (m_PAN == null) {
				Log.i("nfc", "Get Pan Err," + PAN.toString());
				tag.close();
				return ErrorDef.BuildErr_TranceErr;
			}
		} else {
			Log.i("nfc", "Read Record 0201 failed,rv=" + PAN.getSw12());
			tag.close();
			return ErrorDef.BuildErr_ReadDataErr;
		}

		ArrayList<byte[]> LOG = null;
		String sLogString = m_PAN;
		LOG = readLog(tag, nSFI);
		if (!LOG.isEmpty()) {
			sLogString += "|";
			sLogString += parseLog(LOG);
			Log.i("nfc", "log is " + sLogString);
		} else {
			Log.i("nfc", "Can't find any Log");
		}

		return sLogString;
	}

	private static int Select(Iso7816_nfc.Tag tag) {
		String sCmd = "00A40400";
		sCmd += String.format("%1$02X", sAID.length() / 2);
		sCmd += sAID;
		return tag.SendAPDU(sCmd);
	}

	@SuppressWarnings("unused")
	private static int GetData(Iso7816_nfc.Tag tag, String sTag) {
		String sCmd = "80CA" + sTag;
		return tag.SendAPDU(sCmd);
	}

	@SuppressWarnings("unused")
	private static String Int2String(int nInt, int nStrLen) {
		String s = Integer.toString(nInt);
		int nPad = nStrLen - s.length();
		if (nPad < 0) {
			s = s.substring(0, nStrLen);
		} else {
			for (int i = 0; i < nPad; ++i) {
				s = "0" + s;
			}
		}
		return s;
	}

	@SuppressWarnings("unused")
	private static int ReadRecord(Iso7816_nfc.Tag tag, byte bSFI, byte bRecNo) {
		byte[] bCmd = new byte[4];
		bCmd[0] = (byte) 0x00;
		bCmd[1] = (byte) 0xB2;
		bCmd[2] = bRecNo;
		bCmd[3] = (byte) ((byte) (bSFI << 3) | (byte) 0x04);//

		String sCmd = Util.byte2hex(bCmd);
		return tag.SendAPDU(sCmd);
	}

	private static int readSFI(Iso7816_nfc.Tag tag, byte bSFI, byte bRecNo)
			throws InterruptedException {
		byte[] bCmd = new byte[4];
		bCmd[0] = (byte) 0x00;
		bCmd[1] = (byte) 0xB2;
		bCmd[2] = bRecNo;
		bCmd[3] = (byte) (bSFI | (byte) 0x04);

		String sCmd = Util.byte2hex(bCmd);
		return tag.SendAPDU(sCmd);
	}

	private static int ExternAuth(Iso7816_nfc.Tag tag, String sExAuth) {
		String sCmd = "00820000";
		try {
			sCmd += String.format("%1$02X", sExAuth.length() / 2);
			sCmd += sExAuth;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag.SendAPDU(sCmd);
	}

	public static String createData(int length) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(rand.nextInt(10));
		}
		String data = sb.toString();

		return data;
	}

	private static int IntAuth(Iso7816_nfc.Tag tag) {
		String sCmd = "0088000004" + createData(8);
		return tag.SendAPDU(sCmd);
	}

	private static int PutData(Iso7816_nfc.Tag tag, String sPutData) {

		if (15 != sPutData.length() / 2) {
			sPutData = sPutData.substring(0, 30);
		}

		return tag.SendAPDU(sPutData);
	}

	private static int GenerateAC(Iso7816_nfc.Tag tag, boolean bFirst) {
		String sCmd = bFirst ? "80AE8000" : "80AE4000";
		String sCDOLData = GetCDOLData(bFirst);
		sCmd += String.format("%1$02X", sCDOLData.length() / 2);
		sCmd += sCDOLData;

		return tag.SendAPDU(sCmd);
	}

	private static int GetProcessOption(Iso7816_nfc.Tag tag) {
		if (!GetPDOL())
			return -1;

		String sPDOLData = GetPDOLData();
		Log.i("nfc", "PDOLData is " + sPDOLData);

		int nPDOLDataLen = sPDOLData.length() / 2;
		if (nPDOLDataLen == 1) {
			return -1;
		}
		String sCmd = String.format("80A80000%1$02X83%2$02X", nPDOLDataLen + 2,
				nPDOLDataLen);

		sCmd += sPDOLData;
		Log.i("nfc", "GPO CMD is " + sCmd);

		return tag.SendAPDU(sCmd);
	}

	private static String GetTAL(byte[] TagAndLen) {
		String sResult = new String();
		while (TagAndLen.length > 0) {
			int nOffset = 0;
			byte[] bTag = Util.SubBytes(TagAndLen, nOffset, nOffset + 1);
			nOffset++;
			if ((Util.Bytes2Int(bTag) & 0x1F) == 0x1F) {
				nOffset++;
			}
			bTag = Util.SubBytes(TagAndLen, 0, nOffset);
			int nLen = Util.Bytes2Int(Util.SubBytes(TagAndLen, nOffset,
					nOffset + 1));
			nOffset++;
			if (nLen > 128) {
				int nLenLen = nLen - 128;
				byte[] lendata = Util.SubBytes(TagAndLen, nOffset, nOffset
						+ nLenLen);
				nLen = Util.Bytes2Int(lendata);
				nOffset += nLenLen;
			}
			String sTagValue = diMap.get(Util.byte2hex(bTag));
			if (sTagValue == null || sTagValue.length() / 2 != nLen) {
				sResult = ErrorDef.BuildErr_GetDataErr;
				break;
			} else {
				sResult += sTagValue;
			}

			TagAndLen = Util.SubBytes(TagAndLen, nOffset, TagAndLen.length);
		}

		return sResult;
	}

	private static int GetDGIValue(byte[] DGIValue) {
		while (DGIValue.length > 0) {
			int nOffset = 0;
			byte[] bTag = Util.SubBytes(DGIValue, nOffset, nOffset + 1);
			nOffset++;
			boolean bRecursion = Util
					.convertIntToBoolean((Util.Bytes2Int(bTag) & 0x20));
			if ((Util.Bytes2Int(bTag) & 0x1F) == 0x1F) {
				nOffset++;
			}
			bTag = Util.SubBytes(DGIValue, 0, nOffset);
			int nLen = Util.Bytes2Int(Util.SubBytes(DGIValue, nOffset,
					nOffset + 1));
			nOffset++;
			if (nLen > 128) {
				int nLenLen = nLen - 128;
				byte[] lendata = Util.SubBytes(DGIValue, nOffset, nOffset
						+ nLenLen);
				nLen = Util.Bytes2Int(lendata);
				nOffset += nLenLen;
			}
			byte[] bValue = Util.SubBytes(DGIValue, nOffset, nOffset + nLen);

			nOffset += nLen;

			if (bRecursion) {
				GetDGIValue(bValue);
			} else {
				String strDicKey = Util.toHexString(bTag, 0, bTag.length);
				String strDicVal = Util.toHexString(bValue, 0, bValue.length);
				diMap.put(strDicKey, strDicVal);
			}
			DGIValue = Util.SubBytes(DGIValue, nOffset, DGIValue.length);
		}

		return 0;
	}

	private static boolean ReadAFL(Iso7816_nfc.Tag tag, String sAFL) {
		int nFileNum = sAFL.length() / 8;
		boolean isComplete = true;
		for (int i = 0; i < nFileNum; i++) {
			String safl = sAFL.substring(i * 8, i * 8 + 8);
			String str = safl.substring(0, 2);
			int nP2 = 0;
			nP2 = Integer.parseInt(str, 16);
			String str2 = safl.substring(2, 4);
			String str3 = safl.substring(4, 6);
			int nFrom = 0;
			int nEnd = 0;
			int rev = 0;
			nFrom = Integer.parseInt(str2, 16);
			nEnd = Integer.parseInt(str3, 16);
			for (int j = nFrom; j <= nEnd; j++) {

				try {
					rev = readSFI(tag, (byte) nP2, (byte) j);
				} catch (InterruptedException e) {
					e.printStackTrace();
					isComplete = false;
					break;
				}

				if (SWOK == rev) {
					GetDGIValue(tag.GetResponse());
				} else {
					isComplete = false;
				}

			}
		}

		return isComplete;

	}

	private static boolean GetPDOL() {
		m_sPDOL = diMap.get("9F38");
		m_s84 = diMap.get("84");
		if (m_sPDOL == null) {
			Log.i("nfc", "Get PDOL Failed");
			return false;
		} else {
			Log.i("nfc", "PDOL " + m_sPDOL);
			return true;
		}

	}

	private static String GetPDOLData() {
		diMap.put("9F66", m_s9F66);
		diMap.put("9F02", m_sAuthMount);
		diMap.put("9F03", m_s9F03);
		diMap.put("9F1A", m_s9F1A);
		diMap.put("95", m_s95);
		diMap.put("5F2A", m_s5F2A);
		diMap.put("9A", m_s9A);
		diMap.put("9C", m_s9C);
		diMap.put("9F7A", m_s9F7A);
		diMap.put("9F37", m_s9F37);
		diMap.put("DF69", m_sDF69);
		diMap.put("DF60", m_sDF60);
		diMap.put("9F21", m_s9F21);
		diMap.put("9F4E", m_s9F4E);

		return GetTAL(Util.hex2byte(m_sPDOL));
	}

	private static boolean GetCDOL(boolean bFirst) {
		if (bFirst)
			m_sCDOL1 = diMap.get("8C");
		else
			m_sCDOL2 = diMap.get("8D");
		return true;
	}

	private static String GetCDOLData(boolean bFirst) {
		String s;
		if (bFirst) {
			s = m_sAuthMount;
			s += m_s9F03;
			s += m_s9F1A;
			s += m_s95;
			s += m_s5F2A;
			s += m_s9A;
			s += m_s9C;
			s += m_s9F37;
			s += m_s9F21;
			s += m_s9F4E;
		} else {
			s = m_s8A;
			s += m_sAuthMount;
			s += m_s9F03;
			s += m_s9F1A;
			s += m_s95;
			s += m_s5F2A;
			s += m_s9A;
			s += m_s9C;
			s += m_s9F37;
			s += m_s9F21;
			s += m_s9F4E;
		}

		return s.replace(" ", "");
	}

	private static void parsePAN(Iso7816_nfc.Response data) {
		m_PAN = null;
		if (!data.isOkey()) {
			serl = null;
			return;
		}
		final byte[] d = data.getBytes();

		serl = Util.tagTrace((short) 0x5A, d, 0, data.size(), "UTF-8", true);
		if (serl != null) {
			serl = serl.replaceAll("f|F", "");
		}

		m_PAN = serl;
	}

	private static String Get9F37() {
		String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		List<String> list = Arrays.asList(beforeShuffle);
		Collections.shuffle(list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		String afterShuffle = sb.toString();
		String result = afterShuffle.substring(1, 9);
		return result;
	}

	private static ArrayList<byte[]> readLog(Iso7816_nfc.Tag tag, int sfi) {
		final ArrayList<byte[]> ret = new ArrayList<byte[]>(10);

		for (int i = 1; i <= 10; ++i) {
			if (!addLog(tag.readRecord(sfi, i), ret))
				break;
		}

		return ret;
	}

	private static boolean addLog(final Iso7816_nfc.Response r,
			ArrayList<byte[]> l) {
		if (!r.isOkey())
			return false;

		final byte[] raw = r.getBytes();
		final int N = raw.length;
		if (N < 0)
			return false;

		l.add(raw);

		return true;
	}

	private static String parseLog(ArrayList<byte[]> logs) {

		final StringBuilder r = new StringBuilder();

		if (logs == null)
			return null;

		for (final byte[] v : logs) {
			String sLogString = Util.byte2hex(v);

			r.append(sLogString).append("|");
		}

		r.deleteCharAt(r.length() - 1);

		return r.toString();
	}
}
