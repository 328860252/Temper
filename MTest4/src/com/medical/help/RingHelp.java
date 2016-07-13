package com.medical.help;

import org.apache.poi.ddf.DefaultEscherRecordFactory;

import com.zby.medical.R;

import android.content.Context;

public class RingHelp {
	
	public static int getRingRourse(Context context ,int position) {
		int res;
		switch(position) {
			case 0:
				res = R.raw.ring1;
				break;
			case 1:
				res = R.raw.ring2;
				break;
			case 2:
				res = R.raw.ring3;
				break;
			case 3:
				res = R.raw.ring4;
				break;
			case 4:
				res = R.raw.ring5;
				break;
			default:
				res = R.raw.ring1;
				break;
		}
		return res;
	}

}
