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
    private UserNetworkDAO userNetworkDAO = new UserNetworkDAOImpl();
    private UserSQLiteDAO userSQLiteDAO;

    public UserManagerImpl(Context context) {
        userSQLiteDAO  = new UserSQLiteDAOImpl(context);
    }

    @Override
    public boolean syncUsers() {
        //upload dirty users from device and change dirty = false;
        List<User> dirtyUsers = userSQLiteDAO.getDirtyUsers();
        for (User user : dirtyUsers) {
            boolean uploaded = userNetworkDAO.addUser(user);
            if (uploaded) {
                user.setDirty(false);
                userSQLiteDAO.updateUser(user);
            }
        }

        boolean renderAgain = false;
        //upload all users from server
        List<User> updatedUsers = userNetworkDAO.getAllCategories();
        if (updatedUsers != null) {
            for (User user : updatedUsers) {
                User deviceUser = userSQLiteDAO.getUser(user.getId());
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
