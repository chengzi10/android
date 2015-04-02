package com.healthslife.loginregister;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.change.ChangeHeadPortrait;
import com.healthslife.change.HaveSdCard;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.integralactivity.IntegralActivity;
import com.healthslife.music.activity.ListMainActivity;
import com.healthslife.sensor.activity.DataAnalysisActivity;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.DBUtil;
import com.healthslife.server.DownloadServiceActivity;
import com.healthslife.system.SettingActivity;
import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.SuperID.IntFailCallback;
import com.isnc.facesdk.SuperID.IntSuccessCallback;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.SDKConfig;
import com.isnc.facesdk.common.Utils;
import com.isnc.facesdk.net.AsyncBitmapLoader;
import com.isnc.facesdk.net.AsyncBitmapLoader.ImageCallBack;

public class Aty_UserCenter extends Activity {
	private Context context;
	private Button btn_spbundle;
	public static ImageView icon_sp, avatarimg;
	private TextView tv_phonenum, tv_name;
	private String userinfo;
	private RelativeLayout layout;
	Dialog dialog;
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final String IMGURL = Environment
			.getExternalStorageDirectory() + "/Android/data/com.hang.dialog/";
	/* 照相机缓存头像名称 */
	private static final String IMAGE_FILE_NAME_TEMP = "tempfaceImage.jpg";
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.aty_usercenter);
		context = this;
		avatarimg = (ImageView) findViewById(R.id.avatarimg);
		// tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);
		tv_name = (TextView) findViewById(R.id.tv_name);
		layout = (RelativeLayout) findViewById(R.id.relative_face);

		if (SensorData.isLogin()) {
			layout.setVisibility(View.INVISIBLE);
			if(CalculateUtil.isUserFaceEXist()) {//如果该用户头像已设置，则使用非默认头像
				avatarimg.setImageBitmap(CalculateUtil.getDiskBitmap(CalculateUtil.getUserFacePath()));
			}else{
				avatarimg.setImageResource(R.drawable.superid_avatar_img_default);
			}
			
			tv_name.setText(SensorData.getUsername());
		}
	}

	// 有刷脸界面获取表情接口返回请查看onActivityResult，数据处理请查看Aty_AppGetFaceEmotion.class
	public void face_center(View v) {
		SuperID.GetFaceEmotion(this);
	}

	// 更换头像
	public void change_head(View v) {
		showDialog();
	}

	public void health_center(View v) {
		finish();
	}

	// 积分中心
	public void jifen_center(View v) {
		Intent intent = new Intent(Aty_UserCenter.this, IntegralActivity.class);
		startActivity(intent);
		finish();
	}

	public void data_center(View v) {
		Intent intent = new Intent(Aty_UserCenter.this, DataAnalysisActivity.class);
		startActivity(intent);
		finish();
	}

	public void music_center(View v) {
		Intent intent = new Intent(Aty_UserCenter.this, ListMainActivity.class);
		startActivity(intent);
		finish();
	}

	public void setting_center(View v) {
		Intent intent = new Intent(Aty_UserCenter.this, SettingActivity.class);
		startActivity(intent);
		finish();
	}

	// 接口返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:// 打开相册返回
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:// 打开相机返回
				if (HaveSdCard.hasSdcard()) {
					File tempFile = new File(IMGURL + IMAGE_FILE_NAME_TEMP);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:// 裁剪完成,删除照相机缓存的图片
				final File tempFile = new File(IMGURL + IMAGE_FILE_NAME_TEMP);
				if (tempFile.exists()) {
					new Thread() {
						public void run() {
							tempFile.delete();
						};
					}.start();
				}
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");
						// 尝试
						photo = toRoundBitmap(photo);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						avatarimg.setImageBitmap(photo);

						CalculateUtil.saveBitmapToImage(Aty_UserCenter.this,
								photo, CalculateUtil.getUserFacePath());// 保存头像到本地!!!!
						try {
							File f = new File(IMGURL + IMAGE_FILE_NAME);
							if (!f.exists()) {
								f.createNewFile();
							}
							FileOutputStream fOut = new FileOutputStream(f);
							photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
							fOut.flush();
							fOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
				dialog.cancel();// 关闭dialog
				break;
			}
		}

	}

	private void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.change_head_dialog,
				null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void on_click(View v) {
		switch (v.getId()) {
		case R.id.tuku:
			// 相机拍照
			openPhones();
			break;
		case R.id.paizhao:
			// 从图库中获取
			openCamera();
			break;
//		case R.id.cancel_btn:
//			break;
		default:
			break;
		}
	}

	// 打开相册
	private void openPhones() {
		Intent intentFromGallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentFromGallery.setType("image/*"); // 设置文件类型
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
	}

	// 打开照相机
	private void openCamera() {

		File file = new File(IMGURL);
		// 文件夹不存在则创建
		if (!file.exists())
			file.mkdirs();
		// 打开相机
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用,存储缓存图片
		if (HaveSdCard.hasSdcard()) {

			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(IMGURL, IMAGE_FILE_NAME_TEMP)));
		}
		// setResult(RESULT_OK,intentFromCapture);
		startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
		// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	// 选择人脸登录时是否下载数据
	private void selectDownData() {
		View view = getLayoutInflater()
				.inflate(R.layout.down_data_dialog, null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
