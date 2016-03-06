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
    private UserNetworkDAO userNetworkDAO ;
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
        user.setDirty(true);
        int id = userSQLiteDAO.addUser(user);
        user.setId(id);

        String authToken = userNetworkDAO.addUser(user);
        if (authToken != null) {
            user.setDirty(false);
            user.setAuthToken(authToken);
            userSQLiteDAO.updateOwnerUser(user);
            return true;
        }
        return false;
    }

    /**
     * Add/update device's owner
     */
    @Override
    public boolean authenticateUser(User user) {
        User existingUser = userSQLiteDAO.getUserByEmail(user.getEmail());
        if (existingUser == null) {
            userSQLiteDAO.addUser(user);
        } else {
            user.setDirty(true);
            userSQLiteDAO.updateOwnerUser(user);
        }
        String authToken = userNetworkDAO.authenticateOwnerUser(user.getIdToken());
        user.setAuthToken(authToken);
        userSQLiteDAO.updateOwnerUser(user);
        if (authToken == null || authToken.length() < 5) {
            return false;
        } else {
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
//        List<User> dirtyUsers = userSQLiteDAO.getDirtyUsers();
//        for (User user : dirtyUsers) {
//            boolean uploaded = userNetworkDAO.addUser(user);
//            if (uploaded) {
//                user.setDirty(false);
//                userSQLiteDAO.updateOwnerUser(user);
//            }
//        }
//
        boolean renderAgain = false;
//        //upload all users from server
//        User owner = userSQLiteDAO.getOwner();
//        if (owner != null) {
//            List<User> updatedUsers = userNetworkDAO.getAllTrustedUsers(owner.getId());
//            if (updatedUsers != null) {
//                for (User user : updatedUsers) {
//                    User deviceUser = userSQLiteDAO.getUser(user.getId());
//                    user.setDirty(false);
//                    if (deviceUser == null) {
//                        userSQLiteDAO.addUser(user);
//                    } else {
//                        userSQLiteDAO.updateOwnerUser(user);
//                    }
//                    renderAgain = true;
//                }
//            }
//        }
        return renderAgain;
    }

    @Override
    public boolean sendUserIdToken(String idToken) {
        return userNetworkDAO.sendIdToken(idToken);
    }


}
