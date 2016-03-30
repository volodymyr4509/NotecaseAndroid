package com.domain.volodymyr.notecase.manager;

import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public interface UserManager {

    boolean addUser(User user);

    User getUserOwner();

    User getUserById(int id);
    /**
     * Get notecase AUthToken from client's idToken
     */
    boolean authenticateUser(User user);

    List<User> getAllUsers();

    boolean syncUsers();


}
