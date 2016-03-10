package com.data.volodymyr.notecase.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.data.volodymyr.notecase.R;
import com.data.volodymyr.notecase.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volodymyr on 25.10.15.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static String TAG = "DBHandler";
    private static DBHandler dbHandler;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "notecase.db";

    public static final String COLUMN_ID = "_id";
    public static final String DIRTY = "dirty";
    public static final String ENABLED = "enabled";

    //Product table
    public static final String TABLE_PRODUCT = "product";
    public static final String PRODUCT_CATEGORY = "CategoryId";
    public static final String PRODUCT_USER = "UserId";
    public static final String PRODUCT_NAME = "Name";
    public static final String PRODUCT_PRICE = "Price";
    public static final String PRODUCT_TIMESTAMP = "Created";

    //CategorySQLiteDAO table
    public static final String TABLE_CATEGORY = "category";
    public static final String CATEGORY_NAME = "Name";
    public static final String CATEGORY_COLOR = "Color";
    public static final String CATEGORY_IMAGE = "Image";

    //User table
    public static final String TABLE_USER = "user";
    public static final String USER_NAME = "Name";
    public static final String USER_AUTH_TOKEN = "AuthToken";
    public static final String USER_EMAIL = "Email";
    public static final String USER_OWNER = "Owner";


    //Queries
    private static final String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PRODUCT_USER + " INTEGER, " +
            PRODUCT_CATEGORY + " INTEGER, " +
            PRODUCT_NAME + " TEXT, " +
            PRODUCT_PRICE + " REAL, " +
            PRODUCT_TIMESTAMP + " DATETIME, " +
            ENABLED + " INTEGER, " +
            DIRTY + " INTEGER);";

    private static final String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY_NAME + " TEXT, " +
            CATEGORY_COLOR + " INTEGER, " +
            CATEGORY_IMAGE + " INTEGER, " +
            DIRTY + " INTEGER);";

    private static final String CREATE_USER = "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_NAME + " TEXT, " +
            USER_EMAIL + " TEXT, " +
            USER_AUTH_TOKEN + " TEXT, " +
            USER_OWNER + " INTEGER, " +
            DIRTY + " INTEGER);";

    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHandler getDbHandler(Context context) {
        if (dbHandler == null) {
            dbHandler = new DBHandler(context.getApplicationContext());
        }
        return dbHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating sqlite database tables");
        db.execSQL(CREATE_PRODUCT);
        db.execSQL(CREATE_CATEGORY);
        db.execSQL(CREATE_USER);
        initDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Drop and recreate sqlite database tables");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    private void initDefaultCategories(SQLiteDatabase db) {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Beer", -17595, R.drawable.beer));
        categoryList.add(new Category("Accommodation", -20151, R.drawable.bighouse));
        categoryList.add(new Category("bucket", -26151, R.drawable.bucket));
        categoryList.add(new Category("coffee", -14343, R.drawable.coffee));
        categoryList.add(new Category("carrot", -18141, R.drawable.ic_business_black_24dp));
        categoryList.add(new Category("house", -17232, R.drawable.house));

        for (Category category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(CATEGORY_NAME, category.getName());
            values.put(CATEGORY_COLOR, category.getColor());
            values.put(CATEGORY_IMAGE, category.getImage());
            values.put(DIRTY, category.isDirty());
            db.insert(TABLE_CATEGORY, null, values);
        }
    }

}
