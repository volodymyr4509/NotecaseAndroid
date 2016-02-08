package com.data.volodymyr.notecase.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.util.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public class CategorySQLiteDAOImpl implements CategorySQLiteDAO {
    private static final String TAG = "CategorySQLiteDAOImpl";

    private DBHandler dbHandler;

    public CategorySQLiteDAOImpl(Context context) {
        this.dbHandler = DBHandler.getDbHandler(context);
    }

    @Override
    public void addCategory(Category category) {
        Log.i(TAG, "Creating new category: " + category);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHandler.CATEGORY_NAME, category.getName());
        values.put(DBHandler.CATEGORY_COLOR, category.getColor());
        values.put(DBHandler.CATEGORY_IMAGE, category.getImage());

        db.insert(DBHandler.TABLE_CATEGORY, null, values);
    }

    @Override
    public void updateCategory(Category category) {
        Log.i(TAG, "Updating category: " + category);

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.CATEGORY_NAME, category.getName());
        values.put(DBHandler.CATEGORY_COLOR, category.getColor());
        values.put(DBHandler.CATEGORY_IMAGE, category.getImage());
        db.update(DBHandler.TABLE_CATEGORY, values, DBHandler.COLUMN_ID + " = " + category.getId(), null);
    }

    @Override
    public void deleteCategoryById(int categoryId) {
        Log.i(TAG, "Deleting category by id: " + categoryId);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DBHandler.TABLE_CATEGORY, DBHandler.COLUMN_ID + "=?", new String[]{String.valueOf(categoryId)});
    }

    @Override
    public Category getCategoryById(int categoryId) {
        Log.i(TAG, "Retrieving category by id: " + categoryId);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHandler.TABLE_CATEGORY + " WHERE " + DBHandler.COLUMN_ID + " = " + categoryId + ";", null);
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

    @Override
    public List<Category> getAllCategories() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHandler.TABLE_CATEGORY + ";", null);
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
        return categories;    }
}
