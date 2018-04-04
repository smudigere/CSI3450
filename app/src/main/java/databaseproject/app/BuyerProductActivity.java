package databaseproject.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import databaseproject.app.Utility.HttpConnection;

public class BuyerProductActivity extends AppCompatActivity {

    private TextView mDateAdded, mProductName, mDesc, mCategory;
    private ImageView mCenterImage;
    private Spinner mSpinner;
    private Button mAddToCart;

    private JSONObject jsonObject;

    private SharedPreferences prefs;

    private int P_ID, U_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_product);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        findViewById(R.id.center_image_layout).setClipToOutline(true);

        mCenterImage = (ImageView) findViewById(R.id.center_image);
        mProductName = (TextView) findViewById(R.id.productName);
        mDateAdded = (TextView) findViewById(R.id.dateAdded);
        mCategory = (TextView) findViewById(R.id.category);
        mDesc = (TextView) findViewById(R.id.description);
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mAddToCart = (Button) findViewById(R.id.addToCart);

        try {

            Bundle bundle = getIntent().getExtras();
            jsonObject = new JSONObject(bundle != null ? bundle.getString("JSON") : null);

            updateUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {

        try {

            Glide.with(getApplicationContext())
                    .load(jsonObject.getString("IMAGE"))
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher))
                    .into(mCenterImage);

            mProductName.setText(jsonObject.getString("PRODUCT_NAME"));
            mDateAdded.append(jsonObject.getString("DATE_ADDED"));
            mCategory.append(jsonObject.getString("CATEGORY"));
            mDesc.setText(jsonObject.getString("DESCRIPTION"));

            String[] spinnerQ = new String[jsonObject.getInt("QUANTITY")];

            for (int i = 0; i < spinnerQ.length; i++)
                spinnerQ[i] = String.valueOf(i + 1);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerQ);
            mSpinner.setAdapter(adapter);

            if (Objects.equals(prefs.getString(getString(R.string.U_TYPE), null), "SELLER")) {
                mAddToCart.setText(R.string.Update_Quantity);

                mAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateQuantity().execute();
                    }
                });
            } else {

                mAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new GetProductCount().execute();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetProductCount extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {

                JSONArray jsonArray = new JSONArray(
                        prefs.getString(getString(R.string.USERINFO), null)
                );

                P_ID = jsonObject.getInt("P_ID");
                U_ID = jsonArray.getJSONObject(0).getInt("U_ID");

                return HttpConnection
                        .dbConnection("SELECT " +
                                "COUNT(*) AS COUNT " +
                                "FROM CART " +
                                "JOIN PRODUCT " +
                                "ON " +
                                "CART.P_ID = PRODUCT.P_ID " +
                                "WHERE CART.U_ID = " +
                                U_ID +
                                " AND " +
                                "CART.P_ID = " +
                                P_ID
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

                AddToCart addToCart = new AddToCart();

                if (jsonArray.getJSONObject(0).getString("COUNT").equals("0"))
                    addToCart.execute(
                            "INSERT INTO CART (`C_ID`, `P_ID`, `U_ID`, `QUANTITY`) VALUES (NULL, " +
                                    "'" + P_ID + "'," +
                                    "'" + U_ID + "'," +
                                    "'" + mSpinner.getSelectedItem() + "');"
                    );
                else
                    addToCart.execute(
                            "UPDATE CART SET `QUANTITY` = " +
                                    "'" + mSpinner.getSelectedItem() + "' " +
                                    "WHERE " +
                                    "U_ID = '" + U_ID + "' AND " +
                                    "P_ID = '" + P_ID + "'"
                    );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("StaticFieldLeak")
        private class AddToCart extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {

                    return HttpConnection.dbConnection(params[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                finish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateQuantity extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
//UPDATE `databaseProject`.`PRODUCT` SET `QUANTITY` = '4' WHERE `PRODUCT`.`P_ID` = 6;
                HttpConnection.dbConnection("UPDATE PRODUCT SET `QUANTITY` = "+
                        "'" + mSpinner.getSelectedItem() + "' "+
                        "WHERE " +
                        "U_ID = '" + U_ID + "' AND " +
                        "P_ID = '" + P_ID + "'");

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