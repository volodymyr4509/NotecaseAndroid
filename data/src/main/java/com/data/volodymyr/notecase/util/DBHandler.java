package com.data.volodymyr.notecase.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.data.volodymyr.notecase.R;
import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String USER_EMAIL = "Email";
    public static final String USER_PASSWORD = "Password";

    //Queries
    private static final String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PRODUCT_USER + " INTEGER, " +
            PRODUCT_CATEGORY + " INTEGER, " +
            PRODUCT_NAME + " TEXT, " +
            PRODUCT_PRICE + " REAL, " +
            PRODUCT_TIMESTAMP + " DATETIME, " +
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
            USER_PASSWORD + " TEXT, " +
            USER_EMAIL + " TEXT, " +
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

    public List<Product> getProductsByCategoryId(int categoryId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_CATEGORY + " = " + categoryId + ";", null);
        List<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getInt(0));
            product.setUserId(cursor.getInt(1));
            product.setCategoryId(cursor.getInt(2));
            product.setName(cursor.getString(3));
            product.setPrice(cursor.getDouble(4));
            product.setDirty(cursor.getInt(6) == 1);
            Timestamp timestamp = null;
            if (cursor.getString(5) != null) {
                timestamp = Timestamp.valueOf(cursor.getString(5));
            }
            product.setCreated(timestamp);
            products.add(product);
        }
        Log.i(TAG, "Retrieved " + products.size() + " products by categoryid = " + categoryId);
        return products;
    }

    private void initDefaultCategories(SQLiteDatabase db) {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Beer", -17595, R.drawable.beer));
        categoryList.add(new Category("Accommodation", -20151, R.drawable.bighouse));
        categoryList.add(new Category("bucket", -26151, R.drawable.bucket));
        categoryList.add(new Category("coffee", -14343, R.drawable.coffee));
        categoryList.add(new Category("carrot", -18141, R.drawable.morkva));
        categoryList.add(new Category("house", -17232, R.drawable.house));

        for (Category category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(CATEGORY_NAME, category.getName());
            values.put(CATEGORY_COLOR, category.getColor());
            values.put(CATEGORY_IMAGE, category.getImage());
            db.insert(TABLE_CATEGORY, null, values);
        }
    }

    public Cursor getProductNameCursor() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCT + ";";
        Log.i(TAG, "Loading product name cursor: \n" + query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor suggestProductName(String partialProductName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCT + " WHERE Name like '" + partialProductName + "%' GROUP BY " + PRODUCT_NAME + ";";
        Log.i(TAG, "Loading suggested prod name: \n" + query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    //select SUM(p.Price), c.* from product p join category c on p.categoryId= c._id group by p.categoryId;
    public Map<Category, Double> getExpensesGroupedByCategories(Timestamp since, Timestamp till) {
        long before = System.currentTimeMillis();
        SQLiteDatabase db = getReadableDatabase();
        Map<Category, Double> result = new HashMap<>();
        String query = "SELECT SUM(p." + PRODUCT_PRICE + ") AS Sum, c.* FROM " + TABLE_PRODUCT + " p JOIN " +
                TABLE_CATEGORY + " c ON p." + PRODUCT_CATEGORY + "=" + "c." + COLUMN_ID +
                " WHERE p." + PRODUCT_TIMESTAMP + " BETWEEN '" + since + "' AND '" + till + "' GROUP BY p." + PRODUCT_CATEGORY + ";";
        Log.i(TAG, "Loading grouped expenses by categories: \n" + query);
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getInt(1));
            category.setName(cursor.getString(2));
            category.setColor(cursor.getInt(3));
            result.put(category, cursor.getDouble(0));
        }
        Log.i(TAG, "Retrieved " + result.size() + " products by categories");
        Log.w(TAG, "Getting expenses grouped by categories: " + String.valueOf(System.currentTimeMillis() - before) + "ms");
        return result;
    }

}
