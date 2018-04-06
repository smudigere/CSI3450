package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import databaseproject.app.Utility.HttpConnection;

public class SellerActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener{

    private SharedPreferences prefs;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.seller_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetSellerProducts().execute();
    }

    @Override
    public void onBackPressed() {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            case R.id.add_product:
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
                return true;
            case R.id.logout:

                Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean(getString(R.string.LOGIN_STATUS), false).apply();
                prefs.edit().remove(getString(R.string.USERINFO)).apply();
                prefs.edit().remove(getString(R.string.U_TYPE)).apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        try {

            Intent intent = new Intent(getApplicationContext(), SellerProductActivity.class);
            intent.putExtra("JSON", jsonArray.getJSONObject(i).toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSellerProducts extends AsyncTask<Void, Void, String> {

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

                jsonArray = new JSONArray(result);
                List<JSONObject> jsonObjects = new ArrayList<>();
                ListView listView = (ListView) findViewById(R.id.listView);

                for (int i = 0; i < jsonArray.length(); i++)
                    jsonObjects.add(jsonArray.getJSONObject(i));

                ListViewAdapter adapter = new ListViewAdapter(jsonObjects, getApplicationContext());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(SellerActivity.this);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
