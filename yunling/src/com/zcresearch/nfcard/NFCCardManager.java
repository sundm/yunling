package com.zcresearch.nfcard;

import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;
import android.util.Log;

public final class NFCCardManager {

	public static String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;

	static {
		try {
			TECHLISTS = new String[][] { { IsoDep.class.getName() },
					{ NfcV.class.getName() }, { NfcF.class.getName() }, };

			FILTERS = new IntentFilter[] { new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (Exception e) {
			Log.e("CardManager", IsoDep.class.getName() + NfcV.class.getName()
					+ NfcF.class.getName(), e);
		}
	}

	public static String GetCardInfo(Parcelable parcelable) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			Iso7816_nfc.Tag nfctag = new Iso7816_nfc.Tag(isodep);
			return PBOC20.GetCardInfo(nfctag);
		}

		return null;
	}

	public static String BuildDDA(Parcelable parcelable) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			Iso7816_nfc.Tag nfctag = new Iso7816_nfc.Tag(isodep);
			return PBOC20.LoadDDA(nfctag);
		}

		return ErrorDef.BuildErr_BulidStrErr;
	}

	public static String getPan() {
		return PBOC20.getPan();
	}

	public static String getDDA() {
		return PBOC20.getDDA();
	}

	public static int ExecScript(Parcelable parcelable, String sLoadInfo) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			Iso7816_nfc.Tag nfctag = new Iso7816_nfc.Tag(isodep);
			return PBOC20.LoadBalance(nfctag, sLoadInfo);
		}

		return -1;
	}

	public static String QueryLog(Parcelable parcelable) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			Iso7816_nfc.Tag nfctag = new Iso7816_nfc.Tag(isodep);
			return PBOC20.QueryLog(nfctag);
		}

		return ErrorDef.BuildErr_BulidStrErr;
	}
}
