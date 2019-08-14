package com.example.huanghaodong.signature;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.taobao.weex.bridge.JSCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignActivity extends Activity {
	private SignatureView mSignaturePad;
	private Button mClearButton;
	private Button mSaveButton;
	private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 0x2;
	private JSCallback callback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		mSignaturePad = (SignatureView) findViewById(R.id.signature_pad);
//	设置背景图
//		mSignaturePad.setBackgroundResource(R.drawable.a);
		mSignaturePad.setBackgroundColor(Color.parseColor("#ffffff"));

		mSignaturePad.setOnSignedListener(new SignatureView.OnSignedListener() {
			@Override
			public void onSigned() {
				mSaveButton.setEnabled(true);
				mClearButton.setEnabled(true);
			}

			@Override
			public void onClear() {
				mSaveButton.setEnabled(false);
				mClearButton.setEnabled(false);
			}
		});

		mClearButton = (Button) findViewById(R.id.clear_button);
		mSaveButton = (Button) findViewById(R.id.save_button);

		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSignaturePad.clear();
			}
		});

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				if (ContextCompat.checkSelfPermission(SignActivity.this, Manifest.permission
//						.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//					Log.e("未授权，准备授权","未授权，准备授权");
//					ActivityCompat.requestPermissions(SignActivity.this,
//							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//							WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
//				} else {
//					Log.e("已授权，无需再授权","已授权，无需再授权");
//
//					SignActivity.this.save();
//				}

				Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
				String base = SignActivity.bitmapToBase64(signatureBitmap);
				SinatureCallback.callback.invoke(base);
				Log.e("这是base ",base);
                //销毁这个页面，
                finish();
			}
		});
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		//super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 1:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					this.save();
					break;
				}
		}
	}
	private void save () {
		Log.e("授权成功", "授权成功");

		//创建文件夹
		Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
		Log.e("授权成功1", "授权成功1");

		if (addSignatureToGallery(signatureBitmap)) {
			Toast.makeText(SignActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(SignActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
		}
	}
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
		if(!file.exists()){
			Log.e("不存在file1", "不存在file1");
		}
		if (!file.mkdirs()) {
			Log.e("SignaturePad", "Directory not created");
		}
		return file;
	}

	public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, null);
		OutputStream stream = new FileOutputStream(photo);
		newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		stream.close();
	}

	public boolean addSignatureToGallery(Bitmap signature) {
		boolean result = false;
		try {
			File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
			if(!photo.exists()){
				Log.e("不存在file2", "不存在file2");
				photo.createNewFile();
            }
			saveBitmapToJPG(signature, photo);
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri contentUri = Uri.fromFile(photo);
			mediaScanIntent.setData(contentUri);
			SignActivity.this.sendBroadcast(mediaScanIntent);
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
