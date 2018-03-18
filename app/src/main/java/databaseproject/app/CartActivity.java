package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import databaseproject.app.Utility.HttpConnection;

public class CartActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        new GetProducts().execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class GetProducts extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            try {

                JSONArray jsonArray = new JSONArray(
                        prefs.getString(getString(R.string.USERINFO), null)
                );

                return HttpConnection
                        .dbConnection("SELECT " +
                                "CART.C_ID, CART.P_ID, CART.U_ID, CART.QUANTITY, CART.P_ID, " +
                                "PRODUCT.PRODUCT_NAME, PRODUCT.CATEGORY, PRODUCT.IMAGE " +
                                "FROM CART " +
                                "JOIN PRODUCT " +
                                "ON " +
                                "CART.P_ID = PRODUCT.P_ID " +
                                "WHERE CART.U_ID = " +
                                jsonArray.getJSONObject(0).getString("U_ID")
                        );

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray = new JSONArray(s);

                if (jsonArray.length() == 0)
                    Toast.makeText(CartActivity.this, "Empty Cart", Toast.LENGTH_LONG).show();
                else {

                    List<JSONObject> jsonObjects = new ArrayList<>();
                    ListView listView = (ListView) findViewById(R.id.listView);

                    for (int i = 0; i < jsonArray.length(); i++)
                        jsonObjects.add(jsonArray.getJSONObject(i));

                    ListViewAdapter adapter = new ListViewAdapter(jsonObjects, getApplicationContext());
                    listView.setAdapter(adapter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}