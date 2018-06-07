package com.litty.litty2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.litty.userLocationPackage.userCredential;
import com.litty.userLocationPackage.userLocationInterface;

import static android.os.Debug.waitForDebugger;

public class LoginActivity extends AppCompatActivity {
    static EditText mUsername;
    static EditText mPassword;
    public static userCredential userCred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.usernameText);
        mPassword = findViewById(R.id.passwordText);

        CardView card_view = findViewById(R.id.cardViewLogin);
        card_view.setOnClickListener(new View.OnClickListener() {
            // Click event for cardViewLogin
            @Override
            public void onClick(View v) {
                Integer user_id = 0;

                try {
                    user_id = new verifyCredential(LoginActivity.this).execute(userCred).get();
                }
                catch (Exception e){
                    //Figure out what to do with exception
                    String ay = e.getMessage();
                }

                if (user_id != 0) {
                    waitForDebugger();
                    goToLocationActivity(); //Need to pass user_id to mainActivity
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Login Issue");
                    alertDialog.setMessage("Incorrect username and password combination.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    // Method to go to main activity.
    public void goToLocationActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Method to invoke verifyUserLogin lambda function.  verifyUserLogin returns the user_id if user's credentials
    // are found in the database, otherwise it returns zero. - JL
    private static class verifyCredential extends AsyncTask<userCredential, Void, Integer>{
        userLocationInterface userLocationInterface;

        verifyCredential(LoginActivity context) {
            // Create an instance of CognitoCachingCredentialsProvider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
            LambdaInvokerFactory factory = new LambdaInvokerFactory(context.getApplicationContext(), Regions.US_EAST_1, credentialsProvider);

            // Create the Lambda proxy object with default Json data binder.
            // You can provide your own data binder by implementing
            // LambdaDataBinder
            userLocationInterface = factory.build(userLocationInterface.class);

            userCred = new userCredential(mUsername.getText().toString(), mPassword.getText().toString());
        }

        @Override
        protected Integer doInBackground(userCredential... params) {
            // invoke "userLocationInterface" method. In case it fails, it will throw a
            // LambdaFunctionException.
            try {
                //waitForDebugger();
                return userLocationInterface.verifyUserLogin(params[0]);
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                return null;
            }
        }
    }
}
