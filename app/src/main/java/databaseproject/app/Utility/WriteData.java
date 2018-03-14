package databaseproject.app.Utility;


import android.os.AsyncTask;

import org.json.JSONArray;


public class WriteData extends AsyncTask<String, Void, String> {

    private String link = "http://service.cpw.bz/get_coupons_by_location?API_KEY=1fDtlEz81XfjY9W49JvgsJ2mqK&radius=0.5&lat=42.5803122&lon=-83.0302033";

    @Override
    protected String doInBackground(String... params) {

        try {

            return HttpConnection.dbConnection(
                    "INSERT INTO PRODUCT (`P_ID`, `DATE_ADDED`, `PRODUCT_NAME`, `DESCRIPTION`, `CATEGORY`, `IMAGE`, `U_ID`, `QUANTITY`) VALUES (NULL," +
                            "'" +   params[0]   +   "'," +
                            "'" +   params[1]   +   "'," +
                            "'" +   params[2]   +   "'," +
                            "'" +   params[3]   +   "'," +
                            "'" +   params[4]   +   "'," +
                            "'" +   params[5]   +   "'," +
                            "'1');"
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

            JSONArray jsonArray = new JSONArray(result);

            int start = 10;
            int end = 30;

            if (end < jsonArray.length()) {



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}