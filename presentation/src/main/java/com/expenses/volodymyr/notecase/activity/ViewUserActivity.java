package com.expenses.volodymyr.notecase.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.data.volodymyr.notecase.entity.User;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.adapter.UserAdapter;

import java.util.List;

/**
 * Created by volodymyr on 12.02.16.
 */
public class ViewUserActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ViewUserActivity";

    private ImageView addUserButton;
    private ImageView leftActionItem;
    private ImageView navigationArrow;
    private ImageView logo;

    private ListView userListView;
    private UserManager userManager;
    private ArrayAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        navigationArrow = (ImageView) findViewById(R.id.navigation_arrow);
        logo = (ImageView) findViewById(R.id.logo);
        leftActionItem = (ImageView) findViewById(R.id.action_item_left);
        leftActionItem.setVisibility(View.GONE);
        addUserButton = (ImageView) findViewById(R.id.action_item_right);
        addUserButton.setImageResource(R.drawable.ic_control_point_white_24dp);

        userListView = (ListView) findViewById(R.id.user_list);


        logo.setOnClickListener(this);
        navigationArrow.setOnClickListener(this);
        addUserButton.setOnClickListener(this);

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
                        User user = new User();
                        user.setEmail(userEmail.getText().toString());
                        user.setName(userName.getText().toString());
                        new AsyncTask<User, Void, Boolean>(){
                            @Override
                            protected Boolean doInBackground(User... users) {
                                return userManager.addUser(users[0]);
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                renderUserList();
                                popupWindow.dismiss();
                            }
                        }.execute(user);
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

    public void renderUserList(){
        new AsyncTask<Void, Void, List<User>>(){
            @Override
            protected List<User> doInBackground(Void... params) {
                return userManager.getAllUsers();
            }

            @Override
            protected void onPostExecute(List<User> userList) {
                userAdapter = new UserAdapter(getApplicationContext(), userList);
                userListView.setAdapter(userAdapter);
            }
        }.execute();
    }

}
