package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import databaseproject.app.Utility.HttpConnection;
import databaseproject.app.Utility.Queries;

public class CartActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private List<JSONObject> jsonObjects;

    private int U_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        try {

            JSONArray jsonArray = new JSONArray(prefs.getString(getString(R.string.LOGIN_STATUS), null));
            U_ID = jsonArray.getJSONObject(0).getInt("U_ID");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetProducts().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkout:



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        .dbConnection(Queries.GETCARTOFUSER.e +
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

                for (int i = 0; i < jsonArray.length(); i++)
                    if (jsonArray.getJSONObject(i).getString("QUANTITY").equals("0"))
                        jsonArray.remove(i);

                if (jsonArray.length() == 0)
                    Toast.makeText(CartActivity.this, "Empty Cart", Toast.LENGTH_LONG).show();
                else {

                    jsonObjects = new ArrayList<>();
                    ListView listView = findViewById(R.id.listView);

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


    private class CheckOut extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
//UPDATE `databaseProject`.`PRODUCT` SET `QUANTITY` = '3' WHERE `PRODUCT`.`P_ID` = 1;
                for (JSONObject jsonObject: jsonObjects) {

                    jsonObject.getString("QUANTITY");

                }

                HttpConnection.dbConnection("DELETE FROM CART WHERE U_ID = " + U_ID
                                                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}