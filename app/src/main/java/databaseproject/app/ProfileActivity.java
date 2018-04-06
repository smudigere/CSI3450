package databaseproject.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import databaseproject.app.Utility.HttpConnection;
import databaseproject.app.Utility.Queries;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        TextView mName = findViewById(R.id.name);
        TextView mEmail = findViewById(R.id.email);
        TextView mPhone = findViewById(R.id.phone);
        TextView mAddress = findViewById(R.id.address);

        try {

            JSONArray jsonArray = new JSONArray(
                    prefs.getString(getString(R.string.USERINFO), null)
            );

            mName.append(jsonArray.getJSONObject(0).getString("U_FNAME") + " " + jsonArray.getJSONObject(0).getString("U_LNAME"));
            mEmail.append(jsonArray.getJSONObject(0).getString("U_EMAIL"));
            mPhone.append(jsonArray.getJSONObject(0).getString("U_ACODE") + jsonArray.getJSONObject(0).getString("U_PHONE"));
            mAddress.append(
                            jsonArray.getJSONObject(0).getString("U_S_ADDRESS") + "\n\t\t\t\t\t\t\t\t\t\t\t" +
                            jsonArray.getJSONObject(0).getString("U_CITY") + "\n\t\t\t\t\t\t\t\t\t\t\t" +
                            jsonArray.getJSONObject(0).getString("U_STATE") + "\n\t\t\t\t\t\t\t\t\t\t\t" +
                            jsonArray.getJSONObject(0).getString("U_ZIPCODE")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}