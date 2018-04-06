package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import databaseproject.app.Utility.HttpConnection;

public class AddProductActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView mProductName, mDescription, mPrice, mCategory, mImage, mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        mProductName = findViewById(R.id.productName);
        mProductName.setText("Phone Holder");

        mDescription = findViewById(R.id.description);
        mDescription.setText("Set your phone on holder for easy viewing and accessing.");

        mPrice = findViewById(R.id.price);
        mPrice.setText("7.99");

        mCategory = findViewById(R.id.category);
        mCategory.setText("Utility");

        mImage = findViewById(R.id.image);
        mImage.setText("https://images-na.ssl-images-amazon.com/images/I/71tIQGkkChL._SX679_.jpg");

        mQuantity = findViewById(R.id.quantity);
        mQuantity.setText("5");

        Button submit = findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddProduct().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class AddProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                JSONArray jsonArray = new JSONArray(
                        prefs.getString(getString(R.string.USERINFO), null)
                );
/*
VALUES (NULL, '2018-04-06', 'Helicopter', 'A toy helicopter that can fly upto 20 feet in the air.', '79.99', 'Toys', 'https://s7d1.scene7.com/is/image/BedBathandBeyond/54943543631247p?$478$', '2', '3');
 */
                return HttpConnection.dbConnection("INSERT INTO PRODUCT (`P_ID`, `DATE_ADDED`, `PRODUCT_NAME`, `DESCRIPTION`, `PRICE`, `CATEGORY`, `IMAGE`, `U_ID`, `QUANTITY`) VALUES " +
                                                                "(NULL, '" +
                new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(System.currentTimeMillis())) + "', '" +
                        mProductName.getText().toString() + "', '" +
                        mDescription.getText().toString() + "', '" +
                        mPrice.getText().toString() + "', '" +
                        mCategory.getText().toString() + "', '" +
                        mImage.getText().toString() + "', '" +
                                jsonArray.getJSONObject(0).getString("U_ID") + "', '" +
                        mQuantity.getText().toString() + "');"
                );

            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
