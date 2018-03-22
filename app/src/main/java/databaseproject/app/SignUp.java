package databaseproject.app;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import databaseproject.app.Utility.HttpConnection;

public class SignUp extends Fragment implements
    View.OnClickListener    {

    private TextInputEditText mInputEmail, mInputpassword, mInputVpassword,
                                mFname, mLname, mPhone, mStreetAddress, mCity, mState, mZipcode;
    Spinner dropdown;
    private SharedPreferences prefs;

    String[] items = new String[]{"BUYER", "SELLER", "BOTH"};

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater  <p> The LayoutInflater object that can be used to inflate any views in the fragment. </p>
     * @param container     <p> If non-null, this is the parent view that the fragment's UI should
     *                      be attached to. The fragment should not add the view itself,
     *                      but this can be used to generate the LayoutParams of the view. </p>
     * @param savedInstanceState    <p> If non-null, this fragment is being re-constructed from
     *                              a previous saved state as given here. </p>
     * @return  <p> Return the View for the fragment's UI, or null.  </p>
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);

        mInputEmail = (TextInputEditText) view.findViewById(R.id.email);
        mInputpassword = (TextInputEditText) view.findViewById(R.id.password);
        mInputVpassword = (TextInputEditText) view.findViewById(R.id.verify_password);
        mFname = (TextInputEditText) view.findViewById(R.id.fname);
        mLname = (TextInputEditText) view.findViewById(R.id.lname);
        mPhone = (TextInputEditText) view.findViewById(R.id.phone);
        mStreetAddress = (TextInputEditText) view.findViewById(R.id.street_address);
        mCity = (TextInputEditText) view.findViewById(R.id.city);
        mState = (TextInputEditText) view.findViewById(R.id.state);
        mZipcode = (TextInputEditText) view.findViewById(R.id.zipcode);

        mInputEmail.setText("sam309@test.com");
        mInputpassword.setText("test1234");
        mInputVpassword.setText("test1234");
        mFname.setText("Sam");
        mLname.setText("M");
        mPhone.setText("2488250249");
        mStreetAddress.setText("300 N Rochester Rd");
        mCity.setText("Troy");
        mState.setText("MI");
        mZipcode.setText("90078");

        //get the spinner from the xml.
        dropdown = (Spinner) view.findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        Button mSignUp = (Button) view.findViewById(R.id.signup_button);
        mSignUp.setOnClickListener(this);

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view  <p> The view that was clicked. </p>
     */
    @Override
    public void onClick(View view) {

        switch (view.getId())   {

            case R.id.signup_button:

                if (mInputpassword.getText().toString().length() != 0
                        && mInputVpassword.getText().toString().length() != 0
                            && mInputEmail.getText().toString().length() != 0) {
                    //Check if password and verify password match
                    if (mInputpassword.getText().toString().equals(mInputVpassword.getText().toString())) {

                        if (isEmailValid(mInputEmail.getText().toString()))
                            signUpAPICall();
                        else Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(getContext(), "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void signUpAPICall() {

        new AsyncTask<String, Void, Boolean>()  {

            String API_result;

            /**
             * Makes the API call.
             *
             * @param params <p> URL to execute. </p>
             * @return  <p> true if the API call was a success. </p>
             */
            @Override
            protected Boolean doInBackground(String... params) {
                try {

                    API_result = HttpConnection.dbConnection(
                            "SELECT `U_EMAIL` FROM `USER` WHERE `U_EMAIL` = '" + mInputEmail.getText().toString() + "'"
                    );

                    JSONArray jsonArray = new JSONArray(API_result);
// 'Rochester', 'MI', '48326');
                    if (jsonArray.length() == 0) {

                        API_result = HttpConnection.dbConnection(
                               "INSERT INTO `USER` (`U_ID`, `U_FNAME`, `U_LNAME`, `U_TYPE`, `U_EMAIL`, `U_ACODE`, `U_PHONE`, `U_PASSWORD`, `U_S_ADDRESS`, `U_CITY`, `U_STATE`, `U_ZIPCODE`) VALUES " +
                                       "(NULL," +
                                       "'" + mFname.getText().toString() + "'," +
                                       "'" + mLname.getText().toString() + "'," +
                                       "'" + items[dropdown.getSelectedItemPosition()] + "'," +
                                       "'" + mInputEmail.getText().toString() + "'," +
                                       "'" + mPhone.getText().toString().substring(0, 3) + "'," +
                                       "'" + mPhone.getText().toString().substring(3, 10) + "'," +
                                       "'" + mInputpassword.getText().toString() + "'," +
                                       "'" + mStreetAddress.getText().toString() + "'," +
                                       "'" + mCity.getText().toString() + "'," +
                                       "'" + mState.getText().toString() + "'," +
                                       "'" + mZipcode.getText().toString() + "');"
                        );

                    } else {
                        cancel(true);
                    }

                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                Toast.makeText(getContext(), "Email Already Exist", Toast.LENGTH_SHORT).show();

            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result) {
                    ((LoginActivity) getActivity()).getTabLayout().getTabAt(0).select();
                    Toast.makeText(getContext(), "User Created! Now Login!", Toast.LENGTH_SHORT).show();
                } else
                    HttpConnection.noInternetAlert(getContext());

            }
        }.execute();
    }


    /**
     * Method is used for checking valid email id format.
     *
     * @param email Email.
     * @return boolean true for valid false for invalid
     */
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}