package net.mpoisv.kakaoschedule.net.mpoisv.kakaoschedule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import net.mpoisv.kakaoschedule.ResponseKakao;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "schedule.db";
    public static final String ID = "ID";
    public static final String TABLE_NAME = "school_schedule";
    public static final String SUBJECT = "SUBJECT";
    public static final String SENDER = "SENDER";
    public static final String MESSAGE = "MESSAGE";
    public static final String DATE = "DATE";
    public static final String IS_UNTIL = "IS_UNTIL";

    private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            SUBJECT + " INTEGER NOT NULL," +
            SENDER + " TEXT NOT NULL," +
            MESSAGE + " TEXT NOT NULL," +
            DATE + " INTEGER NOT NULL," +
            IS_UNTIL + " INTEGER NOT NULL DEFAULT (0)," +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT);";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int subject, String sender, String msg, long date, int isUntil) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SUBJECT, subject);
        contentValues.put(SENDER, sender);
        contentValues.put(MESSAGE, msg);
        contentValues.put(DATE, date);
        contentValues.put(IS_UNTIL, isUntil);

        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        return result != -1;
    }

    public HashMap<String, ArrayList<String>> getData(long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + DATE + " = " + date + " OR ("+DATE+" > " + date + " AND "+ IS_UNTIL + " != 0)", null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        HashMap<String, ArrayList<String>> data = new HashMap<>();

        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String subject = ResponseKakao.getSubjectString(cursor.getInt(cursor.getColumnIndexOrThrow(SUBJECT)));
                String schedule = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE));
                if (data.containsKey(subject))
                    data.get(subject).add(schedule);
                else {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(schedule);
                    data.put(subject, arr);
                }
            }
        }

        cursor.close();
        db.close();

        return data;
    }

    public int deleteData(int subject, int index, long date) {
        SQLiteDatabase db = this.getWritableDatabase();

        int data = db.delete(TABLE_NAME,SUBJECT+" = "+subject+" AND "+ID+" IN (SELECT "+ID+" FROM "+TABLE_NAME+" WHERE "+SUBJECT+" = "+subject+" AND ("+DATE+" = "+date+" OR ("+DATE+" > "+date+" AND "+IS_UNTIL+" != 0)) LIMIT 1 OFFSET "+(index-1)+");", null);

        db.close();

        return data;
    }
}
