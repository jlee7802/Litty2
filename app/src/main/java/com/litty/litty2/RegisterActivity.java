package com.litty.litty2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.litty.userLocationPackage.registerInfo;
import com.litty.userLocationPackage.userLocationInterface;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    Calendar myCalendar = Calendar.getInstance();
    EditText edittext;
    public static registerInfo regInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        edittext = findViewById(R.id.DOB);
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"123", "blue", "cow"};

        final ArrayAdapter<String> sprache_ratoromanisch_adapter = new ArrayAdapter<String>(
                        RegisterActivity.this,
                        android.R.layout.simple_spinner_item,
                        items) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
            };

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Create listener for date eddittext so that a date picker dialog box appears when the user clicks on the edittext
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public void registerUser(View view) {
        EditText email = findViewById(R.id.emailText);
        EditText dob = findViewById(R.id.DOB);
        RadioGroup gender = findViewById(R.id.radioGender);
        EditText password = findViewById(R.id.passwordText);
        EditText confirmPassword = findViewById(R.id.confirmPasswordText);

        RadioButton genderRD = findViewById(gender.getCheckedRadioButtonId());

        if (password.getText() != confirmPassword.getText())
        {
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Password");
            alertDialog.setMessage("Password and Confirm Password must be the same.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                regInfo = new registerInfo(email.getText().toString(), LocalDate.parse(dob.getText().toString(), formatter), 1, 1, password.getText().toString(), confirmPassword.getText().toString());
                new registerUser(RegisterActivity.this).execute(regInfo).get();
            } catch (Exception e) {
                // Handle exception here.
                String s = e.getMessage();
            }
        }
    }

    private static class registerUser extends AsyncTask<registerInfo, Void, Void> {
        userLocationInterface userLocationInterface;

        registerUser(RegisterActivity context) {
            // Create an instance of CognitoCachingCredentialsProvider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
            LambdaInvokerFactory factory = LambdaInvokerFactory.builder().context(context).region(Regions.US_EAST_1).credentialsProvider(credentialsProvider) .build();

            // Create the Lambda proxy object with default Json data binder.
            // You can provide your own data binder by implementing
            // LambdaDataBinder
            userLocationInterface = factory.build(com.litty.userLocationPackage.userLocationInterface.class);

            //regInfo = new registerInfo(mUsername.getText().toString(), mPassword.getText().toString());
        }

        @Override
        protected Void doInBackground(registerInfo... params) {
            // invoke "userLocationInterface" method. In case it fails, it will throw a
            // LambdaFunctionException.
            try {
                //waitForDebugger();
                return userLocationInterface.registerUser(params[0]);
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                return null;
            }
        }
    }
}
