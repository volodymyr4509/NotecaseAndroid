package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.UserAdapter;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public class ViewUserActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ViewUserActivity";

    private ImageView addUserButton;
    private ImageView leftActionItem;
    private ImageView navigationArrow;
    private ImageView logo;

    private ListView userListView;
    private UserManager userManager;
    private ArrayAdapter userAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Creating ViewUserActivity");
        setContentView(R.layout.activity_view_user);

        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        leftActionItem = (ImageView) findViewById(R.id.action_item_left);
        leftActionItem.setVisibility(View.GONE);
        addUserButton = (ImageView) findViewById(R.id.action_item_right);
        addUserButton.setImageResource(R.drawable.ic_control_point_white_24dp);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.user_swipe_refresh);

        userListView = (ListView) findViewById(R.id.user_list);


        logo.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        addUserButton.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

        userManager = new UserManagerImpl(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        renderUserList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //add user:
            case R.id.action_item_right:
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                final EditText userEmail = (EditText) popupView.findViewById(R.id.user_email);
                final EditText userName = (EditText) popupView.findViewById(R.id.user_name);
                Button submit = (Button) popupView.findViewById(R.id.popup_submit);
                Button dismiss = (Button) popupView.findViewById(R.id.popup_dismiss);

                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final User user = new User();
                        user.setEmail(userEmail.getText().toString());
                        user.setName(userName.getText().toString());

                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
                            new SafeAsyncTask<User, Void, Boolean>(getApplicationContext()) {
                                @Override
                                public Boolean doInBackgroundSafe() throws AuthenticationException {
                                    return userManager.addUser(user);
                                }

                                @Override
                                protected void onPostExecute(Boolean success) {
                                    if (success) {
                                        renderUserList();
                                        popupWindow.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Cannot add user with email " + userEmail.getText().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }.execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please provide valid email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 300);
                break;
            case R.id.navigation_arrow:
            case R.id.logo:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "OnRefresh SwipeRefreshLayout");
        new SafeAsyncTask<Void, Void, Boolean>(this) {
            @Override
            public Boolean doInBackgroundSafe() throws AuthenticationException {
                return userManager.syncUsers();
            }

            @Override
            protected void onPostExecute(Boolean success) {
                swipeRefresh.setRefreshing(false);
                if (success) {
                    Log.i(TAG, "Users synchronized successfully");
                    renderUserList();
                }
            }
        }.execute();
    }

    public void renderUserList() {
        Log.i(TAG, "Rendering user list");
        new SafeAsyncTask<Void, Void, List<User>>(this) {
            @Override
            public List<User> doInBackgroundSafe() throws AuthenticationException {
                return userManager.getAllUsers();
            }

            @Override
            protected void onPostExecute(List<User> userList) {
                userAdapter = new UserAdapter(getApplicationContext(), userList);
                userListView.setAdapter(userAdapter);
                Log.i(TAG, "User list updated. Size: " + userList.size());
            }
        }.execute();
    }

}
