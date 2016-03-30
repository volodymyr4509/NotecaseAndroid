package com.expenses.volodymyr.notecase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.data.volodymyr.notecase.entity.User;
import com.data.volodymyr.notecase.util.AuthenticationException;
import com.domain.volodymyr.notecase.manager.UserManager;
import com.domain.volodymyr.notecase.manager.UserManagerImpl;
import com.expenses.volodymyr.notecase.R;
import com.expenses.volodymyr.notecase.fragment.TabViewExpenses;
import com.expenses.volodymyr.notecase.util.SafeAsyncTask;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by volodymyr on 03.03.16.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private SignInButton signInButton;
    private TextView resultText;
    private TextView loginMessage;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Creating LoginActivity");

        userManager = new UserManagerImpl(this);

        setContentView(R.layout.login_activity);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        resultText = (TextView) findViewById(R.id.result_client_id);
        loginMessage = (TextView) findViewById(R.id.login_or_exit);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Intent intent = new Intent(getApplicationContext(), ConnectionProblemActivity.class);
                        startActivity(intent);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        User owner = userManager.getUserOwner();
        if (owner != null) {
            resultText.setText(owner.getName());
        }else {
            loginMessage.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Sign in button clicked");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result != null && result.isSuccess()) {
                Log.d(TAG, "handleSignInResult:" + result.isSuccess());
                // Signed in successfully, show authenticated UI.
                final GoogleSignInAccount acct = result.getSignInAccount();

                resultText.setText(acct.getDisplayName());

                new SafeAsyncTask<Void, Void, Boolean>(this){
                    @Override
                    public Boolean doInBackgroundSafe() throws AuthenticationException {
                        User user = new User();
                        user.setName(acct.getDisplayName());
                        user.setEmail(acct.getEmail());
                        user.setIdToken(acct.getIdToken());
                        user.setOwner(true);
                        user.setDirty(true);

                        return userManager.authenticateUser(user);
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success!=null && success){
                            finish();
                        }
                    }
                }.execute();
            }
        }
    }

}
