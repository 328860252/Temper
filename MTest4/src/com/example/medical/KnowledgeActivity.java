package com.example.medical;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.zby.medical.R;

public class KnowledgeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_knowledge);
	}
	
	public void btn_back(View v){
		finish();
	}

}
