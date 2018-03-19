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
import databaseproject.app.Utility.Queries;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener{

    private SharedPreferences prefs;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (!prefs.getBoolean(getString(R.string.LOGIN_STATUS), false))
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean(getString(R.string.LOGIN_STATUS), false))
            new GetProducts().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Toast.makeText(this, "Show Profile!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                return true;
            case R.id.logout:

                Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean(getString(R.string.LOGIN_STATUS), false).apply();
                prefs.edit().remove(getString(R.string.USERINFO)).apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        try {

            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra("JSON", jsonArray.getJSONObject(i).toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetProducts extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            try {

                return HttpConnection
                        .dbConnection(Queries.GETALLPRODUCTS.e);

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                jsonArray = new JSONArray(s);
                List<JSONObject> jsonObjects = new ArrayList<>();
                ListView listView = (ListView) findViewById(R.id.listView);

                for (int i = 0; i < jsonArray.length(); i++)
                    jsonObjects.add(jsonArray.getJSONObject(i));

                ListViewAdapter adapter = new ListViewAdapter(jsonObjects, getApplicationContext());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(MainActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}