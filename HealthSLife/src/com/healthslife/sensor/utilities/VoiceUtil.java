package com.healthslife.sensor.utilities;

import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;

/*工具类，提供语音功能*/
public class VoiceUtil implements TextToSpeech.OnInitListener {
	private static final String TAG = "VoiceUtil";
	//private Service mService;// 管理service，发送信息

	private static VoiceUtil instance = null;// 管理语音功能

	private TextToSpeech mTts;// 把文本转化为语音
	private boolean mSpeak = true;// 判断当前是否可以语音播报，用户设置的!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!默认为打开模式!!!!!
	private boolean mSpeakingEngineAvailable = false;// 判断当前语音引擎是否可用

	private VoiceUtil(Context context) {
		initTTS(context);
	}

	public static VoiceUtil getInstance(Context context) {
		if (instance == null) {
			instance = new VoiceUtil(context);
		}
		return instance;
	}

	/*public void setService(Service service) {
		mService = service;
	}*/

	/********** SPEAKING **********/

	public void initTTS(Context context) {
		// Initialize text-to-speech. This is an asynchronous operation.
		// The OnInitListener (second argument) is called after initialization
		// completes.
		Log.i(TAG, "Initializing TextToSpeech...");
		mTts = new TextToSpeech(context, this );// TextToSpeech.OnInitListener
	}

	public void shutdownTTS() {
		Log.i(TAG, "Shutting Down TextToSpeech...");

		mSpeakingEngineAvailable = false;
		mTts.shutdown();
		Log.i(TAG, "TextToSpeech Shut Down.");

	}

	public void say(String text) {
		if (mSpeak && mSpeakingEngineAvailable) {
			mTts.speak(text, TextToSpeech.QUEUE_ADD, 
					null);
		}// Drop all pending entries
		// in the playback
		// queue.
	}

	// Implements TextToSpeech.OnInitListener.
	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Language data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
				System.out.println("语言不支持！！");// 测试！！！！！！！！！！
			} else {
				Log.i(TAG, "TextToSpeech Initialized.");
				mSpeakingEngineAvailable = true;
				System.out.println("好使!!");// 测试！！！！！！！！！！
			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
			System.out.println("不能初始化!!");// 测试！！！！！！！！！！
		}
	}

	public void setSpeak(boolean speak) {
		mSpeak = speak;
	}

	public boolean isSpeakingEnabled() {
		return mSpeak;
	}

	public boolean isSpeakingNow() {
		return mTts.isSpeaking();
	}

	public void ding() {
	}

	/********** Time **********/

	public static long currentTimeInMillis() {
		Time time = new Time();
		time.setToNow();
		return time.toMillis(false);
	}
}
