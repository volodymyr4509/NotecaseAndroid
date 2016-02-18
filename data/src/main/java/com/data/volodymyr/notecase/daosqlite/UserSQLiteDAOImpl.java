package com.data.volodymyr.notecase.daosqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public class UserSQLiteDAOImpl implements UserSQLiteDAO {
    private static final String TAG = "ProductSQLiteDAOImpl";

    private DBHandler dbHandler;
    private Context context;

    public UserSQLiteDAOImpl(Context context) {
        this.context = context;
        this.dbHandler = DBHandler.getDbHandler(context);
    }

    @Override
    public int addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.USER_NAME, user.getName());
        values.put(DBHandler.USER_EMAIL, user.getEmail());
        values.put(DBHandler.USER_OWNER, user.isOwner());
        values.put(DBHandler.DIRTY, user.isDirty());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int id = (int) db.insert(DBHandler.TABLE_PRODUCT, null, values);
        Log.i(TAG, "Saved new User: " + user + " with id = " + id);
        return id;
    }

    @Override
    public void deleteUser(int id) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DBHandler.TABLE_USER, DBHandler.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        Log.i(TAG, "Deleted User by id = " + id);
    }

    @Override
    public void updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.USER_NAME, user.getName());
        values.put(DBHandler.USER_EMAIL, user.getEmail());
        values.put(DBHandler.USER_OWNER, user.isOwner());
        values.put(DBHandler.DIRTY, user.isDirty());

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.update(DBHandler.TABLE_USER, values, DBHandler.COLUMN_ID + " = " + user.getId(), null);
        Log.i(TAG, "Updated User: " + user);
    }

    @Override
    public User getUser(int id) {
        Log.i(TAG, "Retrieving User by id = " + id + " from sqlite");
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHandler.TABLE_USER + " WHERE " + DBHandler.COLUMN_ID + " = " + id + ";", null);
        User user = null;
        if (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setOwner(cursor.getInt(3) == 1);
            user.setDirty(cursor.getInt(4) == 1);
        }
        Log.w(TAG, "Retrieved User by id: " + id + ", User: " + user);
        return user;
    }

    @Override
    public List<User> getDirtyUsers() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM " + DBHandler.TABLE_USER + " WHERE " + DBHandler.DIRTY + " = 1;";
        Cursor cursor = db.rawQuery(query, null);
        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()){
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setOwner(cursor.getInt(3) == 1);
            user.setDirty(cursor.getInt(4) == 1);
            userList.add(user);
        }
        Log.i(TAG, "Dirty users were retrieved from sqlite: count = " + userList.size());
        return userList;
    }

}
