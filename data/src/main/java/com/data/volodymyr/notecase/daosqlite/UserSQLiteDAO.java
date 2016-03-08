package com.data.volodymyr.notecase.daosqlite;

import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public interface UserSQLiteDAO {

    int addUser(User user);

    User getUser(int id);

    User getUserByEmail(String email);

    void updateOwnerUser(User user);
    void updateUser(User user);

    void deleteUser(int id);

    List<User> getAllUsers();

    List<User> getDirtyUsers();

    User getOwner();

}
