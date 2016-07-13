package com.example.medical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.medical.adapter.LanuageAdapter;
import com.medical.constants.AppString;
import com.medical.help.RingHelp;
import com.medical.help.SetupData;
import com.zby.medical.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * ���ý���
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends Activity {

	private TextView tv_version;
	private TextView tv_title, tv_guide_help, tv_language, tv_ring, tv_location;

	private SetupData mSetupData;
	private Builder dialog_language;

	private final static int activity_language = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		initData();
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case activity_language:
			if (resultCode == Activity.RESULT_OK) {
				tv_guide_help.setText(R.string.setting_guide_help);
				tv_title.setText(R.string.setting);
				tv_language.setText(R.string.setting_language);
				tv_ring.setText(R.string.ring);
				tv_location.setText(R.string.location);
			}
			break;
		}
	}

	private void initData() {
		tv_title = (TextView) findViewById(R.id.textView_title);
		tv_guide_help = (TextView) findViewById(R.id.textView_guide_help);
		tv_language = (TextView) findViewById(R.id.textView_language);
		tv_version = (TextView) findViewById(R.id.textView_version);
		tv_ring = (TextView) findViewById(R.id.textView_ring);
		tv_location = (TextView) findViewById(R.id.textView_location);
		PackageManager pmanager = this.getPackageManager();
		try {
			PackageInfo pinfo = pmanager.getPackageInfo(getPackageName(), 0);
			tv_version.setText("V" + pinfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public void btn_back(View v) {
		finish();
	}

	public void btn_language(View v) {
		Intent intent = new Intent(this, SettingLanguageActivity.class);
		startActivityForResult(intent, activity_language);
	}

	public void btn_guide_help(View v) {
		Intent intent = new Intent(this, SettingHelpActivity.class);
		startActivity(intent);
	}

	public void btn_ring(View v) {
		showSelectLanguage();
	}
	
	public void btn_location(View v) {
		Intent intent =new Intent(this, LocationActivity.class);
		startActivity(intent);
	}

	// private void showRing() {
	// language_item = mSetupData.readInt(AppString.language,
	// AppString.Language_zh);
	// if(list ==null) {
	// list= new ArrayList<String>();
	// list.add(getString(R.string.language_zh));
	// list.add(getString(R.string.language_en));
	// list.add(getString(R.string.language_ar));
	// }
	// if(wAdapter==null) {
	// wAdapter = new LanuageAdapter(mHandler, list, this, language_item);
	// } else {
	// wAdapter.updateIndex(language_item);
	// }
	// listView.setAdapter(wAdapter);
	// listView.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// // TODO Auto-generated method stub
	// language_item = arg2;
	// wAdapter.updateIndex(language_item);
	// mHandler.sendEmptyMessage(handler_refresh);
	// }
	// });
	// }

	List<String> list;
	LanuageAdapter wAdapter;

	private void showSelectLanguage() {
		if(mSetupData ==null) mSetupData = SetupData.getSetupData(this);
		int lang = mSetupData.readInt(AppString.Ring, 0);
		if (list == null) {
			list = new ArrayList<String>();
			list.add(getString(R.string.ring) + "1");
			list.add(getString(R.string.ring) + "2");
			list.add(getString(R.string.ring) + "3");
			list.add(getString(R.string.ring) + "4");
			list.add(getString(R.string.ring) + "5");
		}
		if (wAdapter == null) {
			wAdapter = new LanuageAdapter(mHandler, list, this, lang);
		} else {
			wAdapter.updateIndex(lang);
		}
		dialog_language = new AlertDialog.Builder(this);
		// CharSequence[] items = new CharSequence[]
		// {getString(R.string.language_en), getString(R.string.language_zh_cn),
		// getString(R.string.language_zh_tw)};
		// dialog_language.setSingleChoiceItems(items, lang, new
		// DialogInterface.OnClickListener() {
		dialog_language.setSingleChoiceItems(wAdapter, lang,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
							mSetupData.saveInt(AppString.Ring, which);
							playSound(RingHelp.getRingRourse(SettingActivity.this, which)  , 1);
							dialog.dismiss();
					}
				});
		dialog_language.show();
	}

	
	
	MediaPlayer mp ;
	public void playSound(int sound, int recely) {
		if(mp!=null) {
			if(mp.isPlaying()) {
				mp.stop();
			}
			mp =null;
		} 
		mp = new MediaPlayer();   
		try {
			mp.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" +sound));
			mp.prepare();  
			mp.start();   
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private SoundPool soundPool;
//	public void playSound(int sound, int number){
//		if(soundPool==null) {
//			soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
//			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//				
//				@Override
//				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//					// TODO Auto-generated method stub
//					//����
//					soundPool.play(sampleId,     //������Դ
//							15,         //�����
//							15,         //�����
//							1,             //���ȼ���0���
//							0,         //ѭ������0�ǲ�ѭ����-1����Զѭ��
//							1);            //�ط��ٶȣ�0.5-2.0֮�䡣1Ϊ���ٶ�
//					
//				}
//			});
//		}
//	    soundPool.load(SettingActivity.this, sound, 1);
//	}

}
