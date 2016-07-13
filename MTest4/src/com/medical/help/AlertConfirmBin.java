package com.medical.help;

import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;

/**
 * 弹出框  （消息和确定键 ） 确定键 ：关闭
 * @author Administrator
 *
 */


public class AlertConfirmBin {
	private Dialog d;
	private TextView tv;

	private Button confrim_Button, cancel_Button;

	public Dialog getD() {
		return d;
	}

	public void setD(Dialog d) {
		this.d = d;
	}

	public TextView getTv() {
		return tv;
	}

	public void setTv(TextView tv) {
		this.tv = tv;
	}

	public Button getConfrim_Button() {
		return confrim_Button;
	}

	public void setConfrim_Button(Button confrim_Button) {
		this.confrim_Button = confrim_Button;
	}

	public Button getCancel_Button() {
		return cancel_Button;
	}

	public void setCancel_Button(Button cancel_Button) {
		this.cancel_Button = cancel_Button;
	}

}
