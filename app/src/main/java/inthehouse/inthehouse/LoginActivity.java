package inthehouse.inthehouse;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import inthehouse.inthehouse.Persistence.PreferenceStorage;

public class LoginActivity extends Activity implements
        View.OnClickListener
{
    private static int SIGNING_REQUEST_CODE = 10000;
    private static String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
    private String token;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        /*findViewById(R.id.log_out_button).setOnClickListener(this);*/
    }
    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }


    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        Log.d("LoginActivity", "onActivityResult()");
        final Intent go_intent = new Intent(this, FriendStatusActivity.class);
        if (requestCode == SIGNING_REQUEST_CODE) {
            if (responseCode == RESULT_OK) {
                String email = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                AsyncTask<Void, Void, Void> task = new GetUserName(this, email, SCOPE) {
                    @Override
                    protected void onPostExecute(Void token) {
                        go_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(go_intent);
                        finish();
                    }};
                task.execute();
            }
            else if (responseCode == RESULT_CANCELED) {
                //User must click the signin button again.
            }
        }

    }

    private void signIn() {
        Log.d("LoginActivity", "attempting sign-in.");
        String[] accountTypes = {"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, SIGNING_REQUEST_CODE);
    }


    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    private class GetUserName extends AsyncTask<Void, Void, Void> {

        Activity mActivity;
        String mEmail;
        String mScope;

        GetUserName(Activity mActivity, String mEmail, String mScope) {
            super();
            this.mActivity = mActivity;
            this.mEmail = mEmail;
            this.mScope = mScope;
        }

        protected Void doInBackground(Void... params) {
            Log.d("THREAD", "Thread started");
            Log.d("THREAD", "Email: " + mEmail);
            Log.d("THREAD", "Scope: " + mScope);
            try {
                token = GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
                PreferenceStorage.setAuthToken(mActivity, token);
                token = PreferenceStorage.getAuthToken(mActivity);
                Log.d("Token", "Token: " + token);

            } catch (UserRecoverableAuthException userRecoverableAuthException) {
                //TODO: Exception Handling
                Log.d("THREAD", userRecoverableAuthException.toString());
                mActivity.startActivityForResult(userRecoverableAuthException.getIntent(), SIGNING_REQUEST_CODE);
            } catch (GoogleAuthException googleAuthException) {
                //TODO: Exception handling
                Log.d("THREAD", googleAuthException.toString());
            } catch (Exception e) {
                //TODO: Exception handling
                Log.d("THREAD", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void token) {
        }
    }


}