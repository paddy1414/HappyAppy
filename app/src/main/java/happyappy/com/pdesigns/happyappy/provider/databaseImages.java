package happyappy.com.pdesigns.happyappy.provider;

import java.util.ArrayList;

/**
 * Created by Paddy on 30/05/2015.
 */
public class databaseImages {

    private ArrayList<String> images;

    public static final ArrayList<String> imagesList = new ArrayList<String> ();
    public databaseImages () {
        images = new ArrayList<String>();
    }
    public  ArrayList<String> getImages() {
        return images;

    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void addImages(int postion, String item)
    {
        images.add(postion, item);
    }

    public  String getItem (int postion) {
        return images.get(postion);
    }
}
