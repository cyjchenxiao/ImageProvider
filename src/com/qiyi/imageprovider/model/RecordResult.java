package com.qiyi.imageprovider.model;

import android.graphics.Bitmap;

public class RecordResult {
	public Bitmap bitmap;
	public String savePath;
	
	public RecordResult() { }
	
	public RecordResult(Bitmap bitmap, String savePath) {
		this.bitmap = bitmap;
		this.savePath = savePath;
	}
}
