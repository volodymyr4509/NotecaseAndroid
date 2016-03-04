package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public interface UserNetworkDAO {

    boolean updateUser(User user);

    String addUser(User user);

    /**
     * Returns notecase authToken from android idToken
     */
    String authenticateOwnerUser(String idToken);

    boolean deleteUser(int id);

    List<User> getAllTrustedUsers(int userId);

    boolean sendIdToken(String idToken);
}
