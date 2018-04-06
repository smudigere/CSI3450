package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import databaseproject.app.Utility.HttpConnection;

public class AddProductActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    @SuppressLint("StaticFieldLeak")
    private class AddProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                JSONArray jsonArray = new JSONArray(
                        prefs.getString(getString(R.string.USERINFO), null)
                );

                return HttpConnection.dbConnection("SELECT * FROM `PRODUCT` WHERE U_ID = " +
                        jsonArray.getJSONObject(0).getString("U_ID"));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
