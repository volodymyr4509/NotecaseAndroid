package com.expenses.volodymyr.notecase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.activity.ConnectionProblemActivity;
import com.expenses.volodymyr.notecase.activity.ViewCategoryActivity;
import com.expenses.volodymyr.notecase.activity.ViewUserActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by vkret on 04.12.15.
 */
public class TabSettings extends Fragment implements View.OnClickListener {
    private static final String TAG = "TabSettings";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton signInButton;
    private TextView manageCategory;
    private TextView manageUser;
    private TextView resultText;


    private GoogleApiClient googleApiClient;

    public TabSettings() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating Settings fragment");

        View view = inflater.inflate(R.layout.tab_settings, container, false);
        manageCategory = (TextView) view.findViewById(R.id.category_manager);
        manageUser = (TextView) view.findViewById(R.id.user_manager);
        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        resultText = (TextView) view.findViewById(R.id.result_client_id);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.servlet_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Intent intent = new Intent(getContext(), ConnectionProblemActivity.class);
                        startActivity(intent);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        manageCategory.setOnClickListener(this);
        manageUser.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void onDestroy() {
        Log.d(TAG, "Destroying Settings fragment");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Resuming Settings fragment");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_manager:
                Intent categoryIntent = new Intent(getActivity(), ViewCategoryActivity.class);
                startActivity(categoryIntent);
                break;
            case R.id.user_manager:
                Intent userIntent = new Intent(getActivity(), ViewUserActivity.class);
                startActivity(userIntent);
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            resultText.setText(acct.getDisplayName());
        }
    }
}
