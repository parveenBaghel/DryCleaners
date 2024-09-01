package com.superdrycleaners.drycleaners.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.superdrycleaners.drycleaners.beans.RateModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class sqLiteDB extends SQLiteOpenHelper {

  public  static  final String DataBaseName = "cartManager";
  public static final int version = 1;
  public static final String Cart_table = "Cart_table";
  public static String KEY_ID = "ID";
  public static final String Cart_No = "Cart_No";
  public static final String ITEMS_NAME = "Items_Name";
  public static final String JOBS = "Jobs";
  public static final String MIN_RATE = "Min_Rate";
  public static final String MAX_RATE = "Max_Rate";
  public static final String DATE = "date";
  public static final String JOB_ID = "jobID";
  public static final String JOB_ITEMS_ID = "jobItemsID";
  public static final String JOB_QUNTY = "jobQunty";
  public static final String OLD_MIN = "oldMin";
  public static final String OLD_MAX = "oldMax";

  Context myContect;
  public  sqLiteDB(Context context){
    super(context, DataBaseName, null, version);
    this.myContect = context;
  }

  public sqLiteDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String cart_Table = " create table "+ Cart_table+ "("+KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Cart_No + " TEXT, " + OLD_MIN + " TEXT, " + OLD_MAX + " TEXT, " +
            ITEMS_NAME + " TEXT, " + JOBS + " TEXT, " + MIN_RATE + " TEXT, " + MAX_RATE + " TEXT, " + DATE + " TEXT, " + JOB_ID + " TEXT, " + JOB_ITEMS_ID + " TEXT, " + JOB_QUNTY +" TEXT " +")";
    db.execSQL(cart_Table);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + Cart_table);
  }

  public Long insertCartData(RateModel model){
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(JOB_ID , model.getJob_id());
    values.put(JOB_ITEMS_ID , model.getJob_itemID());
    values.put(ITEMS_NAME ,model.getItemName());
    values.put(JOBS , model.getJob());
    values.put(MIN_RATE , model.getJob_minRate());
    values.put(MAX_RATE , model.getJob_maxRate());
    values.put(OLD_MIN , model.getOldMin());
    values.put(OLD_MAX , model.getOldMax());
    values.put(DATE , model.getJob_date());
    values.put(JOB_QUNTY , model.getJobQunty());
    return db.insert(Cart_table , null , values);

  }


  public List<RateModel> getAddCart(){
    SQLiteDatabase db = this.getWritableDatabase();
    List<RateModel> cartList=new ArrayList<>();
    Cursor mCursor = db.query(Cart_table,
            null,null, null, null, null, null);
    if (mCursor.moveToFirst()) {
      do {
        RateModel model=new RateModel();

        model.setJob_id(mCursor.getString(mCursor.getColumnIndex(JOB_ID)));
        model.setJob_itemID(mCursor.getString(mCursor.getColumnIndex(JOB_ITEMS_ID)));
        model.setItemName(mCursor.getString(mCursor.getColumnIndex(ITEMS_NAME)));
        model.setJob(mCursor.getString(mCursor.getColumnIndex(JOBS)));
        model.setJob_minRate(mCursor.getString(mCursor.getColumnIndex(MIN_RATE)));
        model.setJob_maxRate(mCursor.getString(mCursor.getColumnIndex(MAX_RATE)));
        model.setOldMin(mCursor.getString(mCursor.getColumnIndex(OLD_MIN)));
        model.setOldMax(mCursor.getString(mCursor.getColumnIndex(OLD_MAX)));
        model.setJob_date(mCursor.getString(mCursor.getColumnIndex(DATE)));
        model.setJobQunty(mCursor.getString(mCursor.getColumnIndex(JOB_QUNTY)));

        cartList.add(model);
      } while (mCursor.moveToNext());
    }
    return  cartList;

  }

  public boolean carDelete(String jobID) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(Cart_table, JOB_ID +  "='"+jobID+"'", null) > 0;
  }


  public  boolean updateJobTable(String JobID , int itemCount , double minRate , double maxRate){
    ContentValues value = new ContentValues();
    SQLiteDatabase db = this.getWritableDatabase();
    // value.put(JOB_ID , JOB_ID);
    value.put(JOB_QUNTY , String.valueOf(itemCount));
    value.put(MIN_RATE , String.valueOf(minRate));
    value.put(MAX_RATE , String.valueOf(maxRate));

    return db.update(Cart_table , value , JOB_ID + "='"+JobID+"'",null)>0;
  }

  public int deleteCartTable(){
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(Cart_table, null, null);
  }

}
