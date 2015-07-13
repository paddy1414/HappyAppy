package happyappy.com.pdesigns.happyappy.usersStuffs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import happyappy.com.pdesigns.happyappy.R;

/**
 * Created by Paddy on 28/05/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater=null;
    public loadTheBloodyImage imageLoader;

    private String[] mFrom;

    ArrayList<HashMap<String, String>> dataList;

    public ImageAdapter(Activity a, ArrayList<String> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new loadTheBloodyImage(activity.getApplicationContext());
    }





  //  public ImageAdapter(Context c) {
    //    myContext = c;
    //}

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    //create a new imageView for each item refereanced by the apapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi =  convertView;
        //if the image is not recucled, intialize some varibles
        if(convertView == null) {
            vi = inflater.inflate(R.layout.each_picture, null);
            ImageView image=(ImageView) vi.findViewById(R.id.single_grid_picture);
            imageLoader.DisplayImage(data.get(position), image);

        }
        return vi;
    }

    public void clearTheCache() {
    imageLoader.clearCache();
    }






}

