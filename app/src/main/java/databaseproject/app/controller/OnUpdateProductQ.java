package databaseproject.app.controller;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import databaseproject.app.Utility.HttpConnection;

public class OnUpdateProductQ extends AsyncTask<Void, Void, Void> {

    private int P_ID, P_Quantity;

    public OnUpdateProductQ(int P_ID, int p_Quantity) {
        this.P_ID = P_ID;
        this.P_Quantity = p_Quantity;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            String allCart = HttpConnection.dbConnection("SELECT * FROM CART WHERE P_ID = " + P_ID);
            JSONArray jsonArray = new JSONArray(allCart);

            for (int i = 0; i < jsonArray.length(); i++) {

                if (jsonArray.getJSONObject(i).getInt("QUANTITY") > P_Quantity) {

                    String updateQ = HttpConnection.dbConnection("UPDATE CART SET QUANTITY = " + P_Quantity + " WHERE P_ID = " +
                                                    P_ID);

                    Log.i("UPDATE QUERY", updateQ);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}