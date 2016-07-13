package com.example.medical;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zby.medical.R;

public class SettingHelpActivity extends Activity {

	private TextView tv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_help);
		tv_content = (TextView) findViewById(R.id.textView_content);
		tv_content.setText(Html.fromHtml(getString(R.string.guide_help_info)));
		
	}
	
	public void btn_back(View v) {
		finish();
	}

}
