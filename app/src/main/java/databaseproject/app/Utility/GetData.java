package databaseproject.app.Utility;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;


public class GetData extends AsyncTask<Void, Void, String> {

    private String link = "";

    @Override
    protected String doInBackground(Void... voids) {

        try {

            return HttpConnection.httppostConnection(link);
            /*HttpConnection.dbConnection(
                    "INSERT INTO PRODUCT (`P_ID`, `DATE_ADDED`, `PRODUCT_NAME`, `DESCRIPTION`, `CATEGORY`, `IMAGE`, `U_ID`) VALUES (NULL, '2018-03-12', 'Internal Solid State Drive (SSD) ', 'Upgrade your laptop or desktop computer and feel the difference with super-fast OS boot times and application loads ', 'Electronics', 'https://images-na.ssl-images-amazon.com/images/I/51dLyw45hpL.jpg', '1');"
            );*/


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {

            JSONArray jsonArray = new JSONArray(result);

            int start = 0;
            int end = 2;

            if (end < jsonArray.length()) {

                for (int i = start; i < end; i++) {
                    Log.i("JSON - " + i, jsonArray.getJSONObject(i).toString());
                    new WriteData().execute(
                            jsonArray.getJSONObject(i).getString("date_added").substring(0, 9),
                            jsonArray.getJSONObject(i).getString("productName"),
                            jsonArray.getJSONObject(i).getString("description"),
                            jsonArray.getJSONObject(i).getString("subcategory_name"),
                            jsonArray.getJSONObject(i).getString("image"),
                            "2" //U_ID
                    );

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}