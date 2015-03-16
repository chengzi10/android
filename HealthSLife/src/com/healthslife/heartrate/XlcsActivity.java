package com.healthslife.heartrate;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;

public class XlcsActivity extends Activity implements OnItemClickListener{

	 //Activity标签
    private static final String TAG = "心率测试模块";
    
    //控制用
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    
    //定义全部组件
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    private static ListView list = null;

    //屏幕禁止休眠功能用
    private static WakeLock wakeLock = null;
    
    //数据处理用
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
        };
    private static TYPE currentType = TYPE.GREEN;
    public static TYPE getCurrent() {
        return currentType;
    }
    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    
    //数据库
    private XlcsDatabaseHelper mXlcsDatabaseHelper; 
    private Cursor mCursor; 
    private int XLCS_DATAID = 0; 


//  =============================================分割用===============================================
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hearttest_service);
        
        //获取组件SurfaceView
        preview = (SurfaceView) findViewById(R.id.xlcs_preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        //获取组件Image，Text和list
        image = findViewById(R.id.xlcs_image);
        text = (TextView) findViewById(R.id.xlcs_text);
        list = (ListView) findViewById(R.id.xlcs_listview);
        
        //电源管理工具
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm
                .newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");       
        //数据库文件
        mXlcsDatabaseHelper = new XlcsDatabaseHelper(this);
        mCursor = mXlcsDatabaseHelper.select();
        list.setAdapter( new ListAdapter(this,mCursor) );
        list.setOnItemClickListener(this); 
    }

//  =============================================分割用===============================================

    @SuppressLint("Wakelock")
    public void onResume() {
        super.onResume();
        
        //获取WakeLock锁
        wakeLock.acquire();
        
        //打开摄像头
        camera = Camera.open();
        
        //获取系统时间
        startTime = System.currentTimeMillis();
    }
    
//  =============================================分割用===============================================
    
    public void onPause() {
        super.onPause();

        //释放wakeLock锁
        wakeLock.release();

        //摄像头处理
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
    
//  =============================================分割用===============================================
    
    //此处定义的是Camera的内部对象，作用是在界面上实时显示摄像头锁获取到的图像数据
    private static int beatsAvg;//实时心率数据
    private static PreviewCallback previewCallback = new PreviewCallback() {
        //View的回调函数对象用于实时预览Camera的图像数据

        /**
         * 图像数据的处理函数.
         * @param data
         *            表示为图数据的字节数组
         * @param cam
         *            摄像头对象
         */
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null)
                throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();

            if (!processing.compareAndSet(false, true))
                return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
                    height, width);//获取到图像数据中的红色像素的单位平均值
            Log.i(TAG, "imgAvg=" + imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }//错误数据处理

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt)
                    : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    Log.e(TAG, "BEAT!! beats=" + beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // 转换状态，从一个转换到另外一个相同的
            if (newType != currentType) {
                currentType = newType;

                image.postInvalidate();
            }

            //采样时间
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats="
                        + beats);

                //心跳计数
                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                
                //数据显示
                beatsAvg= (beatsArrayAvg / beatsArrayCnt);
                text.setText("当前心率约为："+String.valueOf(beatsAvg));
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };
    
//  =============================================分割用===============================================
    
    //将数据显现到界面上
    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height="
                        + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }


        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
    
//  =============================================分割用===============================================
       
    //获取摄像头的最小预览大小
    private static Camera.Size getSmallestPreviewSize(int width, int height,
            Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea)
                        result = size;
                }
            }
        }
        return result;
    }

//  =============================================分割用===============================================
    
    //心率测试数据保存的函数，@id/xlcs_button_save注册的点击函数
    public void xlcs_save(View view) {
        
        new AlertDialog.Builder(this)
        .setTitle("当前心率约为"+beatsAvg+"\n请选择运动阶段")
        .setItems(XlcsDatabaseHelper.BEATS_TYPE, new OnClickListener() {
            
            @SuppressWarnings("deprecation")
			@Override
            public void onClick(DialogInterface dialog, int which) {
                
                //心跳数据为0，系统数据出错
                if(beatsAvg == 0) {
                    Toast.makeText(XlcsActivity.this, "数据出错，请重试",Toast.LENGTH_LONG);
                    return;
                    }
                //获取系统当前时间
                Time time = new Time();
                time.setToNow();
                String timeString = time.format("%Y-%m-%d %H:%M:%S");
                
                //保存到数据库
                mXlcsDatabaseHelper.insert(beatsAvg, which, timeString);

                //显示Toast
                Toast.makeText(XlcsActivity.this, 
                        "心率数据："+beatsAvg+
                        "\n运动阶段："+XlcsDatabaseHelper.BEATS_TYPE[which]+
                        "\n当前时间："+timeString+
                        "\n保存中...", 
                        Toast.LENGTH_LONG).show();
                //刷新界面
                mCursor.requery();
                list.invalidateViews(); 
                                
            }
        })
        .setNegativeButton("取消", null)
        .show();

    }

//  =============================================分割用===============================================
 
    public class ListAdapter extends BaseAdapter{ 
        
        private Context mContext; 
        private Cursor mCursor; 
        public ListAdapter(Context context,Cursor cursor) { 
         
        mContext = context; 
        mCursor = cursor; 
        } 
        @Override
        public int getCount() { 
            return mCursor.getCount(); 
        } 
        @Override
        public Object getItem(int position) { 
            return null; 
        } 
        @Override
        public long getItemId(int position) { 
            return 0; 
        } 
        @Override
        //将数据库数据显示到界面上
        public View getView(int position, View convertView, ViewGroup parent) { 
            TextView mTextView = new TextView(mContext); 
            mCursor.moveToPosition(position); 
            mTextView.setText(mCursor.getString(3)); 
            mTextView.setTextSize(25);
            return mTextView; 
        } 
         
        }

//  =============================================分割用===============================================
    
    //数据列表项被点急事的数据处理
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
        
        mCursor.moveToPosition(position); 
        XLCS_DATAID = mCursor.getInt(0); 
        new AlertDialog.Builder(this)
        .setTitle("具体数据为：")
        .setItems(new String[] {"数据库ID："+mCursor.getInt(0),
                "心率数据：" + mCursor.getInt(1),
                "运动阶段：" + XlcsDatabaseHelper.BEATS_TYPE[mCursor.getInt(2)],
                "记录时间：\n" + mCursor.getString(3)},
                null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int which) {//删除按键的处理函数
                        if (XLCS_DATAID == 0) { 
                            return; 
                        } //数据出错
                        mXlcsDatabaseHelper.delete(XLCS_DATAID); //删除数据库数据
                        mCursor.requery(); //重新读取，数据库较少是可以使用，本程序数据库适用
                        list.invalidateViews(); //
                    };
                })
                .setNegativeButton("确定", null)//无处理方法按键
                .show();
    } 
}
