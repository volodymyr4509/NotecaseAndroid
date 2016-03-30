package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.data.volodymyr.notecase.entity.User;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.LoginActivity;
import com.expenses.volodymyr.notecase.activity.ViewUserActivity;
import com.google.android.gms.common.SignInButton;

/**
 * Created by vkret on 04.12.15.
 */
public class TabSettings extends Fragment implements View.OnClickListener {
    private static final String TAG = "TabSettings";

    private UserManager userManager;

    private SignInButton signInButton;
    private TextView manageCategory;
    private TextView manageUser;
    private TextView resultText;


//    private GoogleApiClient googleApiClient;

    public TabSettings() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Settings fragment");

        userManager = new UserManagerImpl(getContext());

        View view = inflater.inflate(R.layout.tab_settings, container, false);
//        manageCategory = (TextView) view.findViewById(R.id.category_manager);
        manageUser = (TextView) view.findViewById(R.id.user_manager);
        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        resultText = (TextView) view.findViewById(R.id.result_client_id);

        User user = userManager.getUserOwner();
        if (user != null) {
            resultText.setText(user.getName());
        }

        manageUser.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        return view;
    }


    public void onDestroy() {
        Log.d(TAG, "Destroying Settings fragment");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        User owner = userManager.getUserOwner();
        if (owner != null) {
            resultText.setText(owner.getName());
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming Settings fragment");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.category_manager:
//                Intent categoryIntent = new Intent(getActivity(), ViewCategoryActivity.class);
//                startActivity(categoryIntent);
//                break;
            case R.id.user_manager:
                Intent userIntent = new Intent(getActivity(), ViewUserActivity.class);
                startActivity(userIntent);
                break;
            case R.id.sign_in_button:
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
    }


}
