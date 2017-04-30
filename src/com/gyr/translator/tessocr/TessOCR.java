package com.gyr.translator.tessocr;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.gyr.translator.application.MyApplication;

public class TessOCR {
	private TessBaseAPI mTess;
	
	public TessOCR(String language) {
		mTess = new TessBaseAPI();
		Context context = MyApplication.getContext();
		String datapath = context.getFilesDir() + "/tesseract/";
		File dir = new File(datapath);
		if (!dir.exists()) 
			dir.mkdirs();
		mTess.init(datapath, language);
	}
	
	public String getOCRResult(Bitmap bitmap) {
		
		mTess.setImage(bitmap);
		String result = mTess.getUTF8Text();

		return result;
    }
	
	public void onDestroy() {
		if (mTess != null)
			mTess.end();
	}
	
}
