package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
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
                new CheckOut().execute();
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

                U_ID = jsonArray.getJSONObject(0).getInt("U_ID");

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

    @SuppressLint("StaticFieldLeak")
    private class CheckOut extends AsyncTask<Void, Void, Float> {

        String API_RESULT;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (jsonObjects.size() == 0) {
                Toast.makeText(CartActivity.this, "Empty Cart", Toast.LENGTH_SHORT).show();
                cancel(true);
                finish();
            }
        }

        @Override
        protected Float doInBackground(Void... voids) {

            try {

                API_RESULT = HttpConnection.dbConnection("SELECT CART.C_ID, PRODUCT.P_ID, PRODUCT.PRICE, CART.QUANTITY, PRODUCT.QUANTITY AS P_QUANTITY FROM CART " +
                        "JOIN PRODUCT " +
                        "ON " +
                        "CART.P_ID = PRODUCT.P_ID " +
                        "WHERE CART.U_ID = 6 AND " +
                        "CART.QUANTITY != 0");

                JSONArray jsonArray = new JSONArray(API_RESULT);
                float totalPrice = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    totalPrice += jsonArray.getJSONObject(i).getDouble("PRICE") *
                                    jsonArray.getJSONObject(i).getInt("QUANTITY");
                }

                return totalPrice;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0f;
        }

        @Override
        protected void onPostExecute(Float aFloat) {
            super.onPostExecute(aFloat);

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(CartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(CartActivity.this);
            }
            builder.setTitle("Check Out")
                    .setMessage("Your Total at Check Out is " + aFloat)
                    .setPositiveButton("Check Out", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new UpdateProducts().execute(API_RESULT);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateProducts extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {

            try {

                //1. Update quantity in Product Table
                //2. Delete all the Cart Rows.

                JSONArray jsonArray = new JSONArray(params[0]);

                for (int i = 0; i < jsonArray.length(); i++) {
                    int q = jsonArray.getJSONObject(i).getInt("P_QUANTITY") -
                                jsonArray.getJSONObject(i).getInt("QUANTITY");
                    HttpConnection.dbConnection("UPDATE PRODUCT SET QUANTITY = " + q +
                                                    " WHERE P_ID = " + jsonArray.getJSONObject(i).getInt("P_ID"));
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    HttpConnection.dbConnection("DELETE FROM CART WHERE C_ID = " +
                                                    jsonArray.getJSONObject(i).getInt("C_ID"));
                }

                } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            finish();
        }
    }
}