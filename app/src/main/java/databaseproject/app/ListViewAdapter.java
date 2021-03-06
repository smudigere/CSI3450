package databaseproject.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.List;

/**
 * A adapter class to populate the listview.
 */
public class ListViewAdapter extends BaseAdapter {

    private List<JSONObject> jsonObjects;
    private Context context;

    ListViewAdapter(List<JSONObject> jsonObjects, Context context) {
        this.jsonObjects = jsonObjects;
        this.context = context;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     * @return   Count of items.
     */
    @Override
    public int getCount() {
        try {
            return jsonObjects.size();
        } catch (Exception e)   {
            return 0;
        }
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * @param position    Position of the item whose data we want within the adapter's data set.
     * @return  The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Get the row id associated with the specified position in the list.
     * @param position   The position of the item within the adapter's data set whose row id we want.
     * @return    The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either create a
     * View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...)
     * will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a
     * root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView       The old view to reuse, if possible. Note: You should check that
     *                      this view is non-null and of an appropriate type before using.
     *                      If it is not possible to convert this view to display the correct data,
     *                      this method can create a new view. Heterogeneous lists can specify their number of
     *                      view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * @param parent      The parent that this view will eventually be attached to
     * @return    A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        try {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.wallet_list, parent, false);
            }

            try {

                ImageView listIcon_image = (ImageView) convertView.findViewById(R.id.imageView);

                Glide.with(convertView)
                        .load(jsonObjects.get(position).getString("IMAGE"))
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.ic_launcher))
                        .into(listIcon_image);

                FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.image_layout);
                frameLayout.setClipToOutline(true);

            } catch (Exception e)   {
                Log.v("CP", "Coupon");
            } catch (OutOfMemoryError e)    {
                e.printStackTrace();
            }

            try {

                TextView listHeader = (TextView) convertView.findViewById(R.id.pos_text);
                listHeader.setText(jsonObjects.get(position).getString("PRODUCT_NAME"));
                listHeader.setTextSize(20);

                TextView businessnames = (TextView) convertView.findViewById(R.id.business_name);
                businessnames.setText(jsonObjects.get(position).getString("CATEGORY"));
                businessnames.setTextSize(14);

                TextView descriptions = (TextView) convertView.findViewById(R.id.description);
                descriptions.setText("QUANTITY:\t" + jsonObjects.get(position).getString("QUANTITY"));
                descriptions.setTextSize(14);

            } catch (Exception e)   {
                Log.v("CW", "Coupon");
            }
            return convertView;
        }   catch (Exception | Error e) {
            return null;
        }
    }
}