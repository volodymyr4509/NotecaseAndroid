package com.expenses.volodymyr.notecase.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.expenses.volodymyr.notecase.entity.Category;
import com.expenses.volodymyr.notecase.entity.Product;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 25.10.15.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static DBHandler dbHandler;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notecase.db";

    private static final String COLUMN_ID = "_id";

    //Product table
    public static final String TABLE_PRODUCT = "product";
    private static final String PRODUCT_CATEGORY = "CategoryId";
    private static final String PRODUCT_USER = "UserId";
    public static final String PRODUCT_NAME = "Name";
    private static final String PRODUCT_PRICE = "Price";
    private static final String PRODUCT_TIMESTAMP = "Created";

    //Category table
    public static final String TABLE_CATEGORY = "category";
    private static final String CATEGORY_NAME = "Name";
    private static final String CATEGORY_COLOR = "Color";

    //User table
    public static final String TABLE_USER = "user";
    private static final String USER_NAME = "Name";
    private static final String USER_EMAIL = "Email";
    private static final String USER_PASSWORD = "Password";

    //Queries
    private static final String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PRODUCT_USER + " INTEGER, " +
            PRODUCT_CATEGORY + " INTEGER, " +
            PRODUCT_NAME + " TEXT, " +
            PRODUCT_PRICE + " REAL, " +
            PRODUCT_TIMESTAMP + " DATETIME);";

    private static final String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY_NAME + " TEXT, " +
            CATEGORY_COLOR + " INTEGER);";

    private static final String CREATE_USER = "CREATE TABLE " + TABLE_USER + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_NAME + " TEXT, " +
            USER_PASSWORD + " TEXT, " +
            USER_EMAIL + " TEXT);";

    public DBHandler(Context context) {
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
        db.execSQL(CREATE_PRODUCT);
        db.execSQL(CREATE_CATEGORY);
        db.execSQL(CREATE_USER);
        initDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void addProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(PRODUCT_CATEGORY, product.getCategoryId());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCT, null, values);
//        db.close();
    }

    public void updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(PRODUCT_CATEGORY, product.getCategoryId());
        db.update(TABLE_PRODUCT, values, COLUMN_ID + " = " + product.getId(), null);
//        db.close();
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_ID + " = " + productId + ";", null);
        Product product = null;
        if (cursor.moveToNext()) {
            product = new Product();
            product.setId(cursor.getInt(0));
            product.setCategoryId(cursor.getInt(1));
            product.setUserId(cursor.getInt(2));
            product.setName(cursor.getString(3));
            product.setPrice(cursor.getDouble(4));
            Timestamp timestamp = null;
            if (cursor.getString(5) != null) {
                timestamp = Timestamp.valueOf(cursor.getString(5));
            }
            product.setCreated(timestamp);
        }
//        db.close();
        return product;
    }

    public List<Product> getAllProducts(Timestamp since, Timestamp till) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCT +
                " WHERE " + PRODUCT_TIMESTAMP + " BETWEEN '" + since + "' AND '" + till + "' ORDER BY " + COLUMN_ID + " DESC LIMIT 500;";
        System.out.println();
        System.out.println(query);
        System.out.println();
        Cursor cursor = db.rawQuery(query, null);
        List<Product> products = new ArrayList();
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getInt(0));
            product.setUserId(cursor.getInt(1));
            product.setCategoryId(cursor.getInt(2));
            product.setName(cursor.getString(3));
            product.setPrice(cursor.getDouble(4));
            Timestamp timestamp = null;
            if (cursor.getString(5) != null) {
                timestamp = Timestamp.valueOf(cursor.getString(5));
            }
            product.setCreated(timestamp);
            products.add(product);
        }
//        db.close();
        return products;
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, category.getName());
        values.put(CATEGORY_COLOR, category.getColor());

        db.insert(TABLE_CATEGORY, null, values);
//        db.close();
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, category.getName());
        values.put(CATEGORY_COLOR, category.getColor());
        db.update(TABLE_CATEGORY, values, COLUMN_ID + " = " + category.getId(), null);
//        db.close();
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_ID + " = " + categoryId + ";", null);
        Category category = null;
        if (cursor.moveToNext()) {
            category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setColor(cursor.getInt(2));

        }
//        db.close();
        return category;
    }


    public List<Category> getAllCategories() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + ";", null);
        List<Category> categories = new ArrayList();
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setColor(cursor.getInt(2));

            categories.add(category);
        }
//        db.close();
        return categories;
    }

    private void initDefaultCategories(SQLiteDatabase db) {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Food", -17595));
        categoryList.add(new Category("Accommodation", -20151));
        categoryList.add(new Category("Transport", -26151));
        categoryList.add(new Category("Travel", -14343));
        categoryList.add(new Category("Dinner", -18141));
        categoryList.add(new Category("Other", -17232));

        for (Category category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(CATEGORY_NAME, category.getName());
            values.put(CATEGORY_COLOR, category.getColor());
            db.insert(TABLE_CATEGORY, null, values);
        }
    }

    public Cursor getProductNameCursor() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + ";", null);
//        db.close();
        return cursor;
    }

    public Cursor suggestProductName(String partialProductName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE Name like '" + partialProductName + "%' GROUP BY " + PRODUCT_NAME + ";", null);
//        db.close();
        return cursor;
    }

    //select SUM(p.Price), c.* from product p join category c on p.categoryId= c._id group by p.categoryId;
    public Map<Category, Double> getExpensesGroupedByCategories() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Category, Double> result = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT SUM(p." + PRODUCT_PRICE + ") AS Sum, c.* FROM " + TABLE_PRODUCT + " p JOIN " +
                TABLE_CATEGORY + " c ON p." + PRODUCT_CATEGORY + "=" + "c." + COLUMN_ID + " GROUP BY p." + PRODUCT_CATEGORY, null);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getInt(1));
            category.setName(cursor.getString(2));
            category.setColor(cursor.getInt(3));
            result.put(category, cursor.getDouble(0));
        }
        return result;
    }

}
