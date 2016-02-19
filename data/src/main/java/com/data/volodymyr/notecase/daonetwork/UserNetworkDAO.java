package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public interface UserNetworkDAO {

    boolean updateUser(User user);

    boolean addUser(User user);

    boolean deleteUser(int id);

    List<User> getAllTrustedUsers(int userId);

    boolean sendIdToken(String idToken);
}
