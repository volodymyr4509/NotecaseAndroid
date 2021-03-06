package com.domain.volodymyr.notecase.manager;

import android.content.Context;

import com.data.volodymyr.notecase.daonetwork.UserNetworkDAO;
import com.data.volodymyr.notecase.daonetwork.UserNetworkDAOImpl;
import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAO;
import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAOImpl;
import com.data.volodymyr.notecase.entity.User;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public class UserManagerImpl implements UserManager {
    private UserNetworkDAO userNetworkDAO;
    private UserSQLiteDAO userSQLiteDAO;

    public UserManagerImpl(Context context) {
        this.userSQLiteDAO = new UserSQLiteDAOImpl(context);
        this.userNetworkDAO = new UserNetworkDAOImpl(context);
    }

    /**
     * For owner's friends only
     */
    @Override
    public boolean addUser(User user) {
        int id = userNetworkDAO.addUser(user);
        if (id > 0) {
            user.setDirty(false);
            user.setId(id);
            userSQLiteDAO.addUser(user);
            return true;
        }
        return false;
    }

    @Override
    public User getUserOwner() {
        return userSQLiteDAO.getOwner();
    }

    @Override
    public User getUserById(int id) {
        return userSQLiteDAO.getUser(id);
    }

    /**
     * Add/update device's owner
     */
    @Override
    public boolean authenticateUser(User user) {
        User existingUser = userSQLiteDAO.getOwner();
        if (existingUser == null) {
            userSQLiteDAO.addUser(user);
        }
        User authenticatedUser = userNetworkDAO.authenticateOwnerUser(user.getIdToken());
        if (authenticatedUser == null || authenticatedUser.getAuthToken() == null || authenticatedUser.getAuthToken().length() < 5) {
            return false;
        } else {
            authenticatedUser.setDirty(false);
            authenticatedUser.setOwner(true);
            userSQLiteDAO.updateOwnerUser(authenticatedUser);
            return true;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userSQLiteDAO.getAllUsers();
    }

    @Override
    public boolean syncUsers() {
        //upload dirty users from device and change dirty = false;
        List<User> dirtyUsers = userSQLiteDAO.getDirtyUsers();
        for (User user : dirtyUsers) {
            int id = userNetworkDAO.addUser(user);
            if (id > 0) {
                user.setDirty(false);
                user.setId(id);
                userSQLiteDAO.updateOwnerUser(user);
            }
        }

        boolean renderAgain = false;
        //upload all users from server
        List<User> updatedUsers = userNetworkDAO.getAllTrustedUsers();
        if (updatedUsers != null) {
            for (User user : updatedUsers) {
                User deviceUser = userSQLiteDAO.getUserByEmail(user.getEmail());
                user.setDirty(false);
                if (deviceUser == null) {
                    userSQLiteDAO.addUser(user);
                } else {
                    userSQLiteDAO.updateUser(user);
                }
                renderAgain = true;
            }
        }

        return renderAgain;
    }


}
