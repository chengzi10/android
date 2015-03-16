package com.healthslife.heartrate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class XlcsDatabaseHelper extends SQLiteOpenHelper {


    private final static String DATABASE_NAME = "XLCS_DATA.db";//数据库名
    private final static int DATABASE_VERSION = 1;//数据库版本
    private final static String TABLE_NAME = "XLCS_TABLE";//表名
    private final static String XLCS_DATAID = "xlcs_dataid";//主键ID
    private final static String XLCS_BEATS = "xlcs_beats";//属性：心率数据
    private final static String XLCS_TIME = "xlcs_time";//属性：数据记录的时间
    private final static String XLCS_TYPE = "xlcs_type";//属性：数据类型
    
    public final static String[] BEATS_TYPE = {"没有运动","运动之前","运动之中","运动之后"};//数据类型的取值 共四个 可外部使用
    
//  =============================================分割用===============================================
    
    //构造函数，默认
    public XlcsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//  =============================================分割用===============================================

    @Override
    //在onCreat方法中创建数据库
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + 
                " (" + XLCS_DATAID + " INTEGER primary key autoincrement, " + 
                XLCS_BEATS + " text, "+ XLCS_TYPE + " text, "+ XLCS_TIME +" text);"; 
        db.execSQL(sql);
    }
    
//  =============================================分割用===============================================

    @Override
    //自动生成的方法，数据库更新
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME; 
        db.execSQL(sql); 
        onCreate(db); 

    }

//  =============================================分割用===============================================

    /**
     * 数据库的数据读取操作.
     * Note:默认读取全部数据，不提供筛选功能.
     * @return Curse
     *            表示数据库的全体记录对象.
     */
    public Cursor select() { 
        SQLiteDatabase db = this.getReadableDatabase(); 
        Cursor cursor = db 
        .query(TABLE_NAME, null, null, null, null, null, null); 
        return cursor; 
        } 

//  =============================================分割用===============================================

    /**
     * 数据库中插入一条记录.
     * Note:主键属性xlcs_dataid为自增属性.
     * 
     * @param xlcs_beats
     *            表示记录数据的xlcs_beat属性值.
     * @param xlcs_type
     *            表示记录数据的xlcs_type属性值.
     * @param xlcs_time
     *            表示记录数据的xlcs_time属性值.
     * @return long
     *            返回插入数据的记录ID,插入失败失败返回-1.
     */
    public long insert(int xlcs_beats,int xlcs_type,String xlcs_time) { 
        SQLiteDatabase db = this.getWritableDatabase(); 
        /* ContentValues */
        ContentValues cv = new ContentValues(); 
        cv.put(XLCS_BEATS, xlcs_beats); 
        cv.put(XLCS_TYPE, xlcs_type); 
        cv.put(XLCS_TIME, xlcs_time); 
        long row = db.insert(TABLE_NAME, null, cv); 
        return row; 
        } 
        

//  =============================================分割用===============================================  
   
    /**
     * 数据库中删除一条记录.
     * Note:只通过数据库ID进行删除操作，ID在界面中获取
     * 
     * @param id
     *            表示删除记录的主键ID.
     */
    public void delete(int id) 
        { 
        SQLiteDatabase db = this.getWritableDatabase(); 
        String where = XLCS_DATAID + " = ?"; 
        String[] whereValue ={ Integer.toString(id) }; 
        db.delete(TABLE_NAME, where, whereValue); 
        } 
}
