package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public interface UserNetworkDAO {

    boolean updateUser(User user);

    int addUser(User user);

    /**
     * Returns notecase authToken from android idToken
     */
    User authenticateOwnerUser(String idToken);

    boolean deleteUser(int id);

    List<User> getAllTrustedUsers();

}
