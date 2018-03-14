package databaseproject.app.Utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import databaseproject.app.R;

public class HttpConnection {

    /**
     * A static method to make all API calls.
     *
     * @param link  <p> The API call that needs to made. </p>
     * @return  <p> The API result. </p>
     * @throws Exception    <p> Multiple Exceptions needed to be handled. </p>
     */
    public static String httppostConnection(String link) throws Exception {

        Log.i("URL", link);

        String result;

        URL urls = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
        conn.setReadTimeout(150000); //milliseconds
        conn.setConnectTimeout(15000); // milliseconds
        conn.setRequestMethod("POST");

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line);

            result = String.valueOf(sb);

            Log.i("Result", result);

            return result;
        } else {
            throw new UnknownHostException("Unsuccessful Connection");
        }
    }



    private static String dbPath = "http://76.230.142.194/database_project/sql_query.php?query=";

    public static String dbConnection(String query) throws Exception {

        String link = dbPath + query;
        link = replace(link);

        Log.i("URL", link);

        String result;

        URL urls = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
        conn.setReadTimeout(150000); //milliseconds
        conn.setConnectTimeout(15000); // milliseconds
        conn.setRequestMethod("POST");

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line);

            result = String.valueOf(sb);

            Log.i("Result", result);

            return result;
        } else {
            throw new UnknownHostException("Unsuccessful Connection");
        }
    }


    private static String replace(String url) {

        url = url.replaceAll(" ", "+");
        url = url.replaceAll("'", "%27");


        return url;
    }


    /**
     * Invoked to let the user know that there isn't an active connection
     * to make an API call.
     *
     * @param context Context.
     */
    public static void noInternetAlert(Context context) {

        try {

            Toast.makeText(context, context.getString(R.string.NetworkError), Toast.LENGTH_LONG).show();

        } catch (Exception e)   {
            Log.v("E", "CC");
        }
    }
}