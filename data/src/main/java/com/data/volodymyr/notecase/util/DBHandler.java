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

    //CategoryDAO table
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

    public int addProduct(Product product) {
        Log.i(TAG, "Add new Product: " + product);
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(PRODUCT_CATEGORY, product.getCategoryId());
        values.put(DIRTY, product.isDirty());

        SQLiteDatabase db = getWritableDatabase();
        return (int) db.insert(TABLE_PRODUCT, null, values);
    }

    public void updateProduct(Product product) {
        Log.i(TAG, "Update Product: " + product);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, product.getName());
        values.put(PRODUCT_PRICE, product.getPrice());
        values.put(PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(PRODUCT_CATEGORY, product.getCategoryId());
        values.put(DIRTY, product.isDirty());

        db.update(TABLE_PRODUCT, values, COLUMN_ID + " = " + product.getId(), null);
    }

    public Product getProductById(int productId) {
        long before = System.currentTimeMillis();
        Log.i(TAG, "Retrieving product by id = " + productId + " from sqlite");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_ID + " = " + productId + ";", null);
        Product product = null;
        if (cursor.moveToNext()) {
            product = new Product();
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
        }
        Log.w(TAG, "Getting product by id: " + String.valueOf(System.currentTimeMillis() - before) + "ms");
        return product;
    }

    public List<Product> getAllProducts(Timestamp since, Timestamp till) {
        long before = System.currentTimeMillis();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCT +
                " WHERE " + PRODUCT_TIMESTAMP + " BETWEEN '" + since + "' AND '" + till + "' ORDER BY " + COLUMN_ID + " DESC LIMIT 500;";
        System.out.println(query);
        Cursor cursor = db.rawQuery(query, null);
        List<Product> products = new ArrayList();
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
        Log.i(TAG, "Product were retrieved from sqlite: count = " + products.size() + ", since = " + since + ", till = " + till);
        Log.w(TAG, "Getting all products: " + String.valueOf(System.currentTimeMillis() - before) + "ms");
        return products;
    }

    public void deleteProductById(int productId) {
        Log.i(TAG, "Deleting product by productId = " + productId);
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PRODUCT, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
    }

    public void addCategory(Category category) {
        Log.i(TAG, "Creating new category: " + category);
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, category.getName());
        values.put(CATEGORY_COLOR, category.getColor());
        values.put(CATEGORY_IMAGE, category.getImage());

        db.insert(TABLE_CATEGORY, null, values);
    }

    public void updateCategory(Category category) {
        Log.i(TAG, "Updating category: " + category);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, category.getName());
        values.put(CATEGORY_COLOR, category.getColor());
        values.put(CATEGORY_IMAGE, category.getImage());
        db.update(TABLE_CATEGORY, values, COLUMN_ID + " = " + category.getId(), null);
    }

    public void deleteCategoryById(int categoryId) {
        Log.i(TAG, "Deleting category by id: " + categoryId);
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORY, COLUMN_ID + "=?", new String[]{String.valueOf(categoryId)});
    }

    public Category getCategoryById(int categoryId) {
        Log.i(TAG, "Retrieving category by id: " + categoryId);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_ID + " = " + categoryId + ";", null);
        Category category = null;
        if (cursor.moveToNext()) {
            category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setColor(cursor.getInt(2));
            category.setImage(cursor.getInt(3));
        }
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
            category.setImage(cursor.getInt(3));

            categories.add(category);
        }
        Log.i(TAG, "Retrieved " + categories.size() + " categories from sqlite");
        return categories;
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
