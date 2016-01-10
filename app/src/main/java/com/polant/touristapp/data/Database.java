package com.polant.touristapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.MarkRecord;
import com.polant.touristapp.model.UserMedia;

import java.util.ArrayList;

/**
 * Created by Антон on 08.01.2016.
 */
public class Database {

    private Context context;

    private TouristOpenHelper touristOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    public Database(Context context) {
        this.context = context;
    }

    public void open(){
        touristOpenHelper = new TouristOpenHelper(context);
        sqLiteDatabase = touristOpenHelper.getWritableDatabase();
    }

    public void close(){
        if (touristOpenHelper != null){
            touristOpenHelper.close();
            touristOpenHelper = null;
        }
    }


    public ArrayList<UserMedia> selectAllUserMediaByUserId(int userId){
        ArrayList<UserMedia> result = new ArrayList<>();

        String where = MEDIA_USER_ID + "=?";
        String[] whereArgs = {String.valueOf(userId)};
        Cursor c = sqLiteDatabase.query(TABLE_USERS_MEDIA, null, where, whereArgs, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                int colId = c.getColumnIndex(MEDIA_ID);
                int colName = c.getColumnIndex(MEDIA_NAME);
                int colDescription = c.getColumnIndex(MEDIA_DESCRIPTION);
                int colUserId = c.getColumnIndex(MEDIA_USER_ID);
                int colLatitude = c.getColumnIndex(MEDIA_LATITUDE);
                int colLongitude = c.getColumnIndex(MEDIA_LONGITUDE);
                int colExternalPath = c.getColumnIndex(MEDIA_EXTERNAL_PATH);
                int colIsInGallery = c.getColumnIndex(MEDIA_IS_IN_GALLERY);
                int colCreatedDate = c.getColumnIndex(MEDIA_CREATED_DATE);
                do {
                    UserMedia media = new UserMedia(
                            c.getInt(colId),
                            c.getString(colName),
                            c.getString(colDescription),
                            c.getInt(colUserId),
                            c.getDouble(colLatitude),
                            c.getDouble(colLongitude),
                            c.getString(colExternalPath),
                            c.getInt(colIsInGallery),
                            c.getLong(colCreatedDate)
                    );
                    result.add(media);
                } while (c.moveToNext());
            }
            c.close();
        }
        return result;
    }

    //Вставка записи в TABLE_USERS_MEDIA.
    public int insertMedia(UserMedia media){
        ContentValues cv = putUsersMediaContentValues(media);
        return (int) sqLiteDatabase.insert(TABLE_USERS_MEDIA, null, cv);
    }

    public void updateMedia(UserMedia media){
        ContentValues cv = putUsersMediaContentValues(media);

        String where = MEDIA_ID + "=?";
        String[] whereArgs = { String.valueOf(media.getId()) };
        sqLiteDatabase.update(TABLE_USERS_MEDIA, cv, where, whereArgs);
    }

    //Создание ContentValues для талбицы TABLE_USERS_MEDIA.
    private ContentValues putUsersMediaContentValues(UserMedia media){
        ContentValues cv = new ContentValues();
        cv.put(MEDIA_NAME, media.getName());
        cv.put(MEDIA_DESCRIPTION, media.getDescription());
        cv.put(MEDIA_USER_ID, media.getUserId());
        cv.put(MEDIA_LATITUDE, media.getLatitude());
        cv.put(MEDIA_LONGITUDE, media.getLongitude());
        cv.put(MEDIA_EXTERNAL_PATH, media.getMediaExternalPath());
        cv.put(MEDIA_IS_IN_GALLERY, media.isInGallery());
        cv.put(MEDIA_CREATED_DATE, media.getCreatedDate());

        return cv;
    }


    //Вставка записи в TABLE_MARK_RECORDS.
    public int insertMarkRecord(MarkRecord record){
        ContentValues cv = puvMarkRecordContentValues(record);
        return (int)sqLiteDatabase.insert(TABLE_MARK_RECORDS, null, cv);
    }

    //Создание ContentValues для талбицы TABLE_MARK_RECORDS.
    private ContentValues puvMarkRecordContentValues(MarkRecord record){
        ContentValues cv = new ContentValues();
        cv.put(MARK_RECORD_MEDIA_ID, record.getMediaId());
        if (record.getMarkId() >= 0) {
            cv.put(MARK_RECORD_MARK_ID, record.getMarkId());
        }
        return cv;
    }


    //-------------------------Названия таблиц и их атрибуты--------------------------------//

    //Пользователи.
    public static final String TABLE_USERS = "TABLE_USERS";
    public static final String USER_ID = "USER_ID";
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_PASSWORD = "USER_PASSWORD";

    //Медиа.
    public static final String TABLE_USERS_MEDIA = "TABLE_USERS_MEDIA";
    public static final String MEDIA_ID = "MEDIA_ID";
    public static final String MEDIA_NAME = "MEDIA_NAME";
    public static final String MEDIA_DESCRIPTION = "MEDIA_DESCRIPTION";
    public static final String MEDIA_USER_ID = "MEDIA_USER_ID";             //Внешний ключ.
    public static final String MEDIA_LATITUDE = "MEDIA_LATITUDE";
    public static final String MEDIA_LONGITUDE = "MEDIA_LONGITUDE";
    public static final String MEDIA_EXTERNAL_PATH = "MEDIA_EXTERNAL_PATH";
    public static final String MEDIA_IS_IN_GALLERY = "MEDIA_IS_IN_GALLERY";
    public static final String MEDIA_CREATED_DATE = "MEDIA_CREATED_DATE";

    //Записи о метках (т.к. одно медиа может иметь несколько меток).
    public static final String TABLE_MARK_RECORDS = "TABLE_MARK_RECORDS";
    public static final String MARK_RECORD_ID = "MARK_RECORD_ID";
    public static final String MARK_RECORD_MEDIA_ID = "MARK_RECORD_MEDIA_ID";//Внешний ключ.
    public static final String MARK_RECORD_MARK_ID = "MARK_RECORD_MARK_ID";  //Внешний ключ.

    //Метки.
    public static final String TABLE_MARKS = "TABLE_MARKS";
    public static final String MARK_ID = "MARK_ID";
    public static final String MARK_NAME = "MARK_NAME";
    public static final String MARK_DESCRIPTION = "MARK_DESCRIPTION";



    private static class TouristOpenHelper extends SQLiteOpenHelper{

        private static final int DB_VERSION = 3;
        private static final String DB_NAME = "Tourist";
        private static final String LOG_TAG = TouristOpenHelper.class.getName();

        private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ( " +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_LOGIN + " TEXT UNIQUE, " +
                USER_PASSWORD + " TEXT);";

        private static final String CREATE_TABLE_USERS_MEDIA = "CREATE TABLE " + TABLE_USERS_MEDIA + " ( " +
                MEDIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MEDIA_NAME + " TEXT, " +
                MEDIA_DESCRIPTION + " TEXT, " +
                MEDIA_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "(" + USER_ID + ") ON DELETE CASCADE, " +
                MEDIA_LATITUDE + " DOUBLE, " +
                MEDIA_LONGITUDE + " DOUBLE, " +
                MEDIA_EXTERNAL_PATH + " TEXT, " +
                MEDIA_IS_IN_GALLERY + " INT2, " +
                MEDIA_CREATED_DATE + " INTEGER);";

        private static final String CREATE_TABLE_MARK_RECORDS = "CREATE TABLE " + TABLE_MARK_RECORDS + " ( " +
                MARK_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MARK_RECORD_MEDIA_ID + " INTEGER REFERENCES " + TABLE_USERS_MEDIA + "(" + MEDIA_ID + ") ON DELETE CASCADE, " +
                MARK_RECORD_MARK_ID + " INTEGER REFERENCES " + TABLE_MARKS + "(" + MARK_ID + ") ON DELETE CASCADE);";

        private static final String CREATE_TABLE_MARKS = "CREATE TABLE " + TABLE_MARKS + " ( " +
                MARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MARK_NAME + " TEXT, " +
                MARK_DESCRIPTION + " TEXT);";

        TouristOpenHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_USERS_MEDIA);
            db.execSQL(CREATE_TABLE_MARK_RECORDS);
            db.execSQL(CREATE_TABLE_MARKS);

            //Добавил одного пользователя.
            ContentValues cv = new ContentValues();
            cv.put(USER_LOGIN, "polant");
            cv.put(USER_PASSWORD, "qwerty");
            db.insert(TABLE_USERS, null, cv);

            //Добавил 5 меток по умолчанию.
            ArrayList<Mark> marks = new ArrayList<>(5);
            marks.add(new Mark(1, "Отдых", "Здесь находятся все данные с различных поездок, отпусков, вечеринок..."));
            marks.add(new Mark(2, "Работа", "Здесь находятся все данные, связанные с работой."));
            marks.add(new Mark(3, "Учеба", "Здесь находятся все данные, связанные с учебой."));
            marks.add(new Mark(4, "Путеществия", "Здесь находятся все данные, связанные с путеществиями."));
            marks.add(new Mark(5, "Другое", "Здесь находятся все данные с другой различной информацией"));

            for (Mark m : marks){
                cv = new ContentValues();
                cv.put(MARK_NAME, m.getName());
                cv.put(MARK_DESCRIPTION, m.getDescription());
                db.insert(TABLE_MARKS, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(LOG_TAG, "UPDATE_DATABASE");

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_MEDIA + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARK_RECORDS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKS + ";");

            onCreate(db);
        }
    }
}
