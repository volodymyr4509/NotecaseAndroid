package com.data.volodymyr.notecase.daosqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;
import com.data.volodymyr.notecase.util.DBHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 31.01.16.
 */
public class ProductSQLiteDAOImpl implements ProductSQLiteDAO {
    private static final String TAG = "ProductSQLiteDAOImpl";
    private static final String LAST_PRODUCT_SYNC_TIMESTAMP = "lastSyncTimestamp";

    private DBHandler dbHandler;
    private Context context;

    public ProductSQLiteDAOImpl(Context context) {
        this.context = context;
        this.dbHandler = DBHandler.getDbHandler(context);
    }

    @Override
    public int addProduct(Product product) {
        Log.i(TAG, "Add new Product: " + product);
        ContentValues values = new ContentValues();
        values.put(DBHandler.PRODUCT_NAME, product.getName());
        values.put(DBHandler.PRODUCT_USER, product.getUserId());
        values.put(DBHandler.PRODUCT_PRICE, product.getPrice());
        values.put(DBHandler.PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(DBHandler.PRODUCT_CATEGORY, product.getCategoryId());
        values.put(DBHandler.DIRTY, product.isDirty());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        return (int) db.insert(DBHandler.TABLE_PRODUCT, null, values);
    }

    @Override
    public void updateProduct(Product product) {
        Log.i(TAG, "Update Product: " + product);
        ContentValues values = new ContentValues();
        values.put(DBHandler.PRODUCT_NAME, product.getName());
        values.put(DBHandler.PRODUCT_PRICE, product.getPrice());
        values.put(DBHandler.PRODUCT_TIMESTAMP, product.getCreated().toString());
        values.put(DBHandler.PRODUCT_CATEGORY, product.getCategoryId());
        values.put(DBHandler.DIRTY, product.isDirty());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.update(DBHandler.TABLE_PRODUCT, values, DBHandler.COLUMN_ID + " = " + product.getId(), null);
    }

    @Override
    public Product getProductById(int productId) {
        Log.i(TAG, "Retrieving product by id = " + productId + " from sqlite");
        long before = System.currentTimeMillis();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHandler.TABLE_PRODUCT + " WHERE " + DBHandler.COLUMN_ID + " = " + productId + ";", null);
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

    @Override
    public List<Product> getAllProducts(Timestamp since, Timestamp till) {
        long before = System.currentTimeMillis();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM " + DBHandler.TABLE_PRODUCT +
                " WHERE " + DBHandler.PRODUCT_TIMESTAMP + " BETWEEN '" + since + "' AND '" + till + "' ORDER BY " + DBHandler.COLUMN_ID + " DESC LIMIT 500;";
        Log.d(TAG, "SQLite Query: " + query);
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

    @Override
    public List<Product> getDirtyProducts(){
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM " + DBHandler.TABLE_PRODUCT + " WHERE " + DBHandler.DIRTY + " = 1;";
        Cursor cursor = db.rawQuery(query, null);
        List<Product> productList = new ArrayList<>();
        while (cursor.moveToNext()){
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
            productList.add(product);
        }
        Log.i(TAG, "Dirty products were retrieved from sqlite: count = " + productList.size());
        return productList;
    }

    @Override
    public void deleteProductById(int productId) {
        Log.i(TAG, "Deleting product by productId = " + productId);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DBHandler.TABLE_PRODUCT, DBHandler.COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHandler.TABLE_PRODUCT + " WHERE " + DBHandler.PRODUCT_CATEGORY + " = " + categoryId + ";", null);
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

    @Override
    public Map<Category, Double> getExpensesGroupedByCategories(Timestamp since, Timestamp till) {
        long before = System.currentTimeMillis();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Map<Category, Double> result = new HashMap<>();
        String query = "SELECT SUM(p." + DBHandler.PRODUCT_PRICE + ") AS Sum, c.* FROM " + DBHandler.TABLE_PRODUCT + " p JOIN " +
                DBHandler.TABLE_CATEGORY + " c ON p." + DBHandler.PRODUCT_CATEGORY + "=" + "c." + DBHandler.COLUMN_ID +
                " WHERE p." + DBHandler.PRODUCT_TIMESTAMP + " BETWEEN '" + since + "' AND '" + till + "' GROUP BY p." + DBHandler.PRODUCT_CATEGORY + ";";
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

    public Cursor getProductNameCursor() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM " + DBHandler.TABLE_PRODUCT + ";";
        Log.i(TAG, "Loading product name cursor: \n" + query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor suggestProductName(String partialProductName) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM " + DBHandler.TABLE_PRODUCT + " WHERE Name like '" + partialProductName + "%' GROUP BY " + DBHandler.PRODUCT_NAME + ";";
        Log.i(TAG, "Loading suggested prod name: \n" + query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Timestamp getLastSyncTimestamp(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long timestamp = preferences.getLong(LAST_PRODUCT_SYNC_TIMESTAMP, 0l);
        Timestamp lastSync = new Timestamp(timestamp);
        Log.i(TAG, "Last Product update timestamp retrieved(SharedPreferences): " + timestamp);
        return lastSync;
    }

    public void updateLastSyncTimestamp(Timestamp timestamp){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putLong(LAST_PRODUCT_SYNC_TIMESTAMP, timestamp.getTime());
        boolean saved = prefEditor.commit();
        Log.i(TAG, "Last Product update timestamp updated(SharedPreferences): " + timestamp + " with success: " + saved);
    }

}
