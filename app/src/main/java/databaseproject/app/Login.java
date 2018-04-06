package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;

import databaseproject.app.Utility.HttpConnection;

/**
 * A child class of {@link Fragment} used to show the login page to the user.
 * API call to log the user is also done here. The database is populated, and the status
 * of the login is changed.
 *
 * @author Samartha Mudigere
 * @version 1.0
 */
public class Login extends Fragment implements
        View.OnClickListener    {


    public TextInputEditText getUserName() {
        return userName;
    }

    private TextInputEditText userName, password;

    private SharedPreferences prefs;

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

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);

        userName = (TextInputEditText) view.findViewById(R.id.userName);
        userName.setText("samq309@test.com");

        password = (TextInputEditText) view.findViewById(R.id.password);
        password.setText("test1234");

        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

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

            case R.id.login_button:

                try {

                    View v = getActivity().getCurrentFocus();

                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    if (userName.getText().toString().length() != 0
                            && password.getText().toString().length() != 0)
                        loginAPICall(userName.getText().toString(), password.getText().toString());
                    else Toast.makeText(getContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();

                } catch (Exception e)   {
                    Log.v("E", "CC");
                }

                break;
        }
    }

    /**
     * Method to make the API_CALL and handle the result.
     *
     * @param userName   Username.
     * @param password   Password.
     */
    @SuppressLint("StaticFieldLeak")
    private void loginAPICall(final String userName, final String password) {

        new AsyncTask<Void, Void, Boolean>()  {

            String API_result;

            @Override
            protected Boolean doInBackground(Void... voids) {

                try {

                    API_result = HttpConnection.dbConnection(
                            "SELECT `U_ID`, `U_FNAME`, `U_LNAME`, `U_TYPE`, `U_EMAIL`, `U_ACODE`, `U_PHONE`, `U_S_ADDRESS`, `U_CITY`, `U_STATE`, `U_ZIPCODE` FROM `USER` WHERE `U_EMAIL` = " +
                                    "'" + userName + "'" +
                                    " AND `U_PASSWORD` = " +
                                    "'" + password + "'"
                    );
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result)
                    loading_success(API_result);
                else {
                    HttpConnection.noInternetAlert(getContext());
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

        }.execute();
    }

    private void loading_success(final String result) {

        try {

            JSONArray jsonArray = new JSONArray(result);

            if (jsonArray.getJSONObject(0).getString("U_TYPE").equals("BOTH")) {

                final CharSequence loginTypes[] = new CharSequence[] {"BUYER", "SELLER"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Login as...");
                builder.setItems(loginTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("onCLick", (String) loginTypes[which]);

                        if (which == 0)
                            enterBuyerScreen(result);
                        else
                            enterSellerScreen(result);
                    }
                });
                builder.show();
            } else if (jsonArray.getJSONObject(0).getString("U_TYPE").equals("SELLER"))
                enterSellerScreen(result);
            else {
                Log.i("loading_success", result);
                enterBuyerScreen(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterBuyerScreen(String result) {
        Toast.makeText(getContext(), "Logged in!", Toast.LENGTH_SHORT).show();
        prefs.edit().putBoolean(getString(R.string.LOGIN_STATUS), true).apply();
        prefs.edit().putString(getString(R.string.USERINFO), result).apply();
        prefs.edit().putString(getString(R.string.U_TYPE), "BUYER").apply();
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }


    private void enterSellerScreen(String result) {
        Toast.makeText(getContext(), "Logged in!", Toast.LENGTH_SHORT).show();
        prefs.edit().putBoolean(getString(R.string.LOGIN_STATUS), true).apply();
        prefs.edit().putString(getString(R.string.USERINFO), result).apply();
        prefs.edit().putString(getString(R.string.U_TYPE), "SELLER").apply();
        startActivity(new Intent(getContext(), SellerActivity.class));
        getActivity().finish();
    }
}