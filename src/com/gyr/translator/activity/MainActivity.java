package com.gyr.translator.activity;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.gyr.translator.R;
import com.gyr.translator.application.MyApplication;
import com.gyr.translator.onlinetranslate.TranslateToGoogle;
import com.gyr.translator.onlinetranslate.TranslateUtil;
import com.gyr.translator.tessocr.TessOCR;
import com.gyr.translator.utils.ClearableEditText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
//import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.KeyEvent;
//import android.view.ContextMenu;
//import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private TranslateUtil mTranslator;
	private TessOCR mTessOCR;
	private TextToSpeech mSpeech;
	private ProgressDialog mProgressDialog;
	private Builder mbuilder;
	private AlertDialog mdialog;
	private ClearableEditText mSource;	
	private Spinner srcSpinner,tarSpinner;
	private Button mButtonTranslate, mButtonSwitcher;
	private ImageButton mButtonGallery, mButtonCamera, mButtonSpeech, mButtonSpeaker, mButtonDelete;
	private TextView mResult;
	private String mCurrentPhotoPath;
	//private String ocrlanguage,srlanguage,srclanguage,tarlanguage;
	private Timer timer;
	
	private static int src_lang;
	private static int tar_lang;
	private static final int LANG_CH = 0;
	private static final int LANG_EN = 1;
	private static final int LANG_FR = 2;
	private static final int LANG_RU = 3;
	
	private static final int REQUEST_TAKE_PHOTO = 11;
	private static final int REQUEST_PICK_PHOTO = 22;
	private static final int REQUEST_VOICE_RECOGNITION = 33;
	
	private static final int TIME_OUT = 10;
	private static final int SUCCESS = 20;
	
	private long exitTime = 0;
	
	Locale locale_ru = new Locale("ru");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		createFiles();
		
		/*
		ocrlanguage = "eng";
		srlanguage = "en";
		srclanguage = "en";
		tarlanguage = "zh-CN";
		*/
		src_lang = LANG_EN;
		tar_lang = LANG_CH;

		mSource = (ClearableEditText) findViewById(R.id.et_source);
		mResult = (TextView) findViewById(R.id.tv_result);
		//registerForContextMenu(mResult);
		srcSpinner = (Spinner) findViewById(R.id.sp_srclanguage);
		srcSpinner.setSelection(1,true);
		srcSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					src_lang = LANG_CH;
					/*
					ocrlanguage = "chi_sim";
					srlanguage = "zh_CN";
					srclanguage = "zh-CN";
					*/
					break;
				case 1:
					src_lang = LANG_EN;
					/*
					ocrlanguage = "eng";
					srlanguage = "en";
					srclanguage = "en";
					*/
					break;
				case 2:
					src_lang = LANG_FR;
					/*
					ocrlanguage = "fra";
					srlanguage = "fr";
					srclanguage = "fr";
					*/
					break;
				case 3:
					src_lang = LANG_RU;
					/*
					ocrlanguage = "rus";
					srlanguage = "ru";
					srclanguage = "ru";
					*/
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		tarSpinner = (Spinner) findViewById(R.id.sp_tarlanguage);
		tarSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					tar_lang = LANG_CH;
					//tarlanguage = "zh-CN";
					break;
				case 1:
					tar_lang = LANG_EN;
					//tarlanguage = "en";
					break;
				case 2:
					tar_lang = LANG_FR;
					//tarlanguage = "fr";
					break;
				case 3:
					tar_lang = LANG_RU;
					//tarlanguage = "ru";
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		mButtonGallery = (ImageButton) findViewById(R.id.bt_gallery);
		mButtonGallery.setOnClickListener(this);
		mButtonCamera = (ImageButton) findViewById(R.id.bt_camera);
		mButtonCamera.setOnClickListener(this);
		mButtonSpeech = (ImageButton) findViewById(R.id.bt_speech);
		mButtonSpeech.setOnClickListener(this);
		mButtonTranslate = (Button) findViewById(R.id.bt_translate);
		mButtonTranslate.setOnClickListener(this);
		mButtonSwitcher = (Button) findViewById(R.id.bt_switch);
		mButtonSwitcher.setOnClickListener(this);
		mButtonSpeaker = (ImageButton) findViewById(R.id.bt_speaker);
		mButtonSpeaker.setOnClickListener(this);
		mButtonDelete = (ImageButton) findViewById(R.id.bt_delete);
		mButtonDelete.setOnClickListener(this);
		
	}
	
	/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			Uri uri = (Uri) intent
					.getParcelableExtra(Intent.EXTRA_STREAM);
			uriOCR(uri);
		}
	}
	*/

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mSpeech != null) {
			mSpeech.stop();
			mSpeech.shutdown();
			mSpeech = null;
		}
		
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_TAKE_PHOTO
				&& resultCode == Activity.RESULT_OK) {
			setPic();
		}
		if (requestCode == REQUEST_PICK_PHOTO
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();			
			if (uri != null) {
				uriTodir(uri);
			}
		}
		if (requestCode == REQUEST_VOICE_RECOGNITION
				&& resultCode == Activity.RESULT_OK){
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			getSpeech(matches);
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {   
	        if((System.currentTimeMillis()-exitTime) > 2000) {  
	            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.add(0, v.getId(), 0, "复制");
		TextView mTextView = (TextView) v;
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(mTextView.getText());
	}
	*/

	private void createFiles(){
		Context context = MyApplication.getContext();
		
		String chi = "chi_sim.traineddata";
		String eng = "eng.traineddata";
		String fra = "fra.traineddata";
		String rus = "rus.traineddata";
		String dataDirPath = context.getFilesDir() + "/tesseract/tessdata";
		String chi_dataPath = dataDirPath + "/" + chi;
		String eng_dataPath = dataDirPath + "/" + eng;
		String fra_dataPath = dataDirPath + "/" + fra;
		String rus_dataPath = dataDirPath + "/" + rus;
		
		try {
			File dir = new File(dataDirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File traineddata_chi = new File(chi_dataPath);
			File traineddata_eng = new File(eng_dataPath);
			File traineddata_fra = new File(fra_dataPath);
			File traineddata_rus = new File(rus_dataPath);
			if (!traineddata_chi.exists()) {
				InputStream ins = getResources().openRawResource(
						R.raw.chi_sim);
				FileOutputStream fos = new FileOutputStream(traineddata_chi);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = ins.read(buffer))>0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				ins.close();
			}
			if (!traineddata_eng.exists()) {
				InputStream ins = getResources().openRawResource(
						R.raw.eng);
				FileOutputStream fos = new FileOutputStream(traineddata_eng);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = ins.read(buffer))>0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				ins.close();
			}
			if (!traineddata_fra.exists()) {
				InputStream ins = getResources().openRawResource(
						R.raw.fra);
				FileOutputStream fos = new FileOutputStream(traineddata_fra);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = ins.read(buffer))>0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				ins.close();
			}
			if (!traineddata_rus.exists()) {
				InputStream ins = getResources().openRawResource(
						R.raw.rus);
				FileOutputStream fos = new FileOutputStream(traineddata_rus);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = ins.read(buffer))>0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				ins.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*
	private void uriOCR(Uri uri) {
		if (uri != null) {
			InputStream is = null;
			try {
				is = getContentResolver().openInputStream(uri);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				//mImage.setImageBitmap(bitmap);
				OCRConfirm(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	*/
	
	private void uriTodir(Uri uri){
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		mCurrentPhotoPath = actualimagecursor.getString(actual_image_column_index);
		setPic();
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		Context context = MyApplication.getContext();
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		String storageDir = context.getExternalCacheDir()
				+ "/MyTranslator";
		File dir = new File(storageDir);
		if (!dir.exists()) {
			dir.mkdir();
		}

		File image = new File(storageDir + "/" + imageFileName + ".jpg");

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	@SuppressWarnings("deprecation")
	private void setPic() {
		
		//ImageView mImage = new ImageView(this);		
		//int targetW = mImage.getWidth();
		//int targetH = mImage.getHeight();

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = Math.min(photoW / 480, photoH / 640);

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		final Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		OCRConfirm(bitmap);
		
		//doOCR(bitmap);

	}
	
	private void OCRConfirm(final Bitmap bitmap){
		ImageView mImage = new ImageView(this);
		mImage.setImageBitmap(bitmap);
		mbuilder = new AlertDialog.Builder(this);
		mbuilder
		.setTitle("预览")
		.setView(mImage)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				doOCR(bitmap);
				dialog.dismiss();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mdialog = mbuilder.create();
		mdialog.show();
	}
	
	
	private void getSpeech(ArrayList<String> contents){
		final ListView mList = new ListView(this);
		mList.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, contents));
		mList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(mSource.getText()) 
						|| mSource.getText().toString().endsWith(" ")
						|| mSource.getText().toString().endsWith("\n")) {
					mSource.getText().append(mList.getItemAtPosition(position).toString());
				}
				else {
					mSource.getText().append(" " + mList.getItemAtPosition(position).toString());
				}				
				mdialog.dismiss();
			}
			
		});
		mbuilder = new AlertDialog.Builder(this);
		mbuilder.setTitle("请选择").setView(mList);
		mdialog = mbuilder.create();
		mdialog.show();
	}
	
	
	private int ttssetLanguage(int lang) {
		int result = TextToSpeech.LANG_NOT_SUPPORTED;
		if (lang == LANG_CH) {
			result = mSpeech.setLanguage(Locale.CHINESE);
		}
		else if (lang == LANG_EN) {
			result = mSpeech.setLanguage(Locale.ENGLISH);
		}
		else if (lang == LANG_FR) {
			result = mSpeech.setLanguage(Locale.FRENCH);
		}
		else if (lang == LANG_RU) {
			result = mSpeech.setLanguage(locale_ru);
		}
		return result;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.bt_gallery:
			pickPhoto();
			break;
		case R.id.bt_camera:
			takePhoto();
			break;
		case R.id.bt_speech:
			recognizeSpeech();
			break;
		case R.id.bt_translate:
			if (!mSource.getText().toString().trim().equals("")) {
				translateText();
			}
			else {
				mResult.setText("");
			}
			break;
		case R.id.bt_switch:
			switchSpinner();
			break;
		case R.id.bt_speaker:
			if (mSpeech == null) {
				if (!mResult.getText().toString().trim().equals("")) {
					speakText();
					mButtonSpeaker.setImageResource(R.drawable.speakerstop);
				}				
			}
			else {
				mSpeech.stop();
				mSpeech.shutdown();
				mSpeech = null;
				mButtonSpeaker.setImageResource(R.drawable.speaker);
			}
			break;
		case R.id.bt_delete:
			deleteText();
			break;
		}
	}
	
	
	private void pickPhoto() {
		Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhotoIntent, REQUEST_PICK_PHOTO);
	}

	private void takePhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {

			}
			if (photoFile != null) {
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}
	
	@SuppressLint("ShowToast")
	private void recognizeSpeech(){
		String srlanguage;
		switch(src_lang){
		case LANG_CH:
			srlanguage = "zh_CN";
			break;
		case LANG_EN:
			srlanguage = "en";
			break;
		case LANG_FR:
			srlanguage = "fr";
			break;
		case LANG_RU:
			srlanguage = "ru";
			break;
		default:
			srlanguage = "en";
			break;
		}
		
		try {
			Intent recognizeSpeechIntent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
					srlanguage);
			recognizeSpeechIntent
					.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说话");
			startActivityForResult(recognizeSpeechIntent,
					REQUEST_VOICE_RECOGNITION);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast
			.makeText(MainActivity.this, "找不到语音设备", Toast.LENGTH_SHORT)
			.show();
		}
	}
	
	private void translateText(){
		final String srclanguage, tarlanguage;
		switch(src_lang){
		case LANG_CH:
			srclanguage = "zh-CN";
			break;
		case LANG_EN:
			srclanguage = "en";
			break;
		case LANG_FR:
			srclanguage = "fr";
			break;
		case LANG_RU:
			srclanguage = "ru";
			break;
		default:
			srclanguage = "en";
			break;
		}
		switch(tar_lang){
		case LANG_CH:
			tarlanguage = "zh-CN";
			break;
		case LANG_EN:
			tarlanguage = "en";
			break;
		case LANG_FR:
			tarlanguage = "fr";
			break;
		case LANG_RU:
			tarlanguage = "ru";
			break;
		default:
			tarlanguage = "zh-CN";
			break;
		}		
		
		mTranslator = new TranslateToGoogle();
		mProgressDialog = ProgressDialog.show(this, "请稍候",
				"正在翻译...", true);
		new Thread(){
			public void run(){
				try {
					final String[] transresults = mTranslator.translate(mSource.getText().toString().split("\n"), srclanguage, tarlanguage);					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub							
							String transresult = "";
							int size = transresults.length;
							for(int i = 0 ; i < size ; i++) {
								if (transresults[i] != null && transresults[i].length()>0) {
									if( i < size-1) {
										transresult = transresult + transresults[i] + "\n";
									} else {
										transresult = transresult + transresults[i];
									}
								}								
							}
							mResult.setText(transresult);
							sendMsg(SUCCESS);
							mProgressDialog.dismiss();
						}
						
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendMsg(TIME_OUT);
			}
			
		}, 25000);
		
	}
	
	private void switchSpinner(){
		int temp;
		temp = src_lang;
		src_lang = tar_lang;
		tar_lang = temp;
		
		srcSpinner.setSelection(src_lang,true);
		tarSpinner.setSelection(tar_lang,true);
	}
	
	private void speakText(){
		mSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener(){

			@SuppressWarnings("deprecation")
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if(status == TextToSpeech.SUCCESS) {
					int ttsresult = ttssetLanguage(tar_lang);
					if (ttsresult == TextToSpeech.LANG_MISSING_DATA
							|| ttsresult == TextToSpeech.LANG_NOT_SUPPORTED) {
						Toast.makeText(MainActivity.this, "暂不支持用该语言朗读", Toast.LENGTH_SHORT).show();
						/*
						Object[] a = mSpeech.getVoices().toArray();
						System.out.println(Arrays.toString(a));
						*/
					}
					else if (!mResult.getText().toString().equals("")) {
						mSpeech.speak(mResult.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
			
		});

	}
	
	private void deleteText(){
		mResult.setText("");
	}

	private void doOCR(final Bitmap bitmap) {
		String ocrlanguage;
		switch(src_lang){
		case LANG_CH:
			ocrlanguage = "chi_sim";
			break;
		case LANG_EN:
			ocrlanguage = "eng";
			break;
		case LANG_FR:
			ocrlanguage = "fra";
			break;
		case LANG_RU:
			ocrlanguage = "rus";
			break;
		default:
			ocrlanguage = "eng";
			break;
		}		
		
		mTessOCR = new TessOCR(ocrlanguage);
		mProgressDialog = ProgressDialog.show(this, "请稍候",
				"正在识别...", true);
		
		new Thread(new Runnable() {
			public void run() {

				final String ocrresult = mTessOCR.getOCRResult(bitmap);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (ocrresult != null && !ocrresult.equals("")) {
							mSource.setText(ocrresult);
						}
						mProgressDialog.dismiss();
						mTessOCR.onDestroy();
						
					}

				});

			};
		}).start();
		
	}
	
	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case TIME_OUT:
				Thread.interrupted();
				mProgressDialog.dismiss();
				Toast
				.makeText(MainActivity.this, "网络连接超时", Toast.LENGTH_SHORT)
				.show();
				break;
			case SUCCESS:
				timer.cancel();
				break;
			default:
				break;
			}
		}
	};
	
	private void sendMsg(int i){
		Message timeOutMsg = new Message();
		timeOutMsg.what = i;
		mHandler.sendMessage(timeOutMsg);
	}

}
