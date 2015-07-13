/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package happyappy.com.pdesigns.happyappy.usersStuffs;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import happyappy.com.pdesigns.happyappy.ApplicationResources.AppStatus;
import happyappy.com.pdesigns.happyappy.ApplicationResources.ImageCache;
import happyappy.com.pdesigns.happyappy.ApplicationResources.ImageFetcher;
import happyappy.com.pdesigns.happyappy.ApplicationResources.OsUtils;
import happyappy.com.pdesigns.happyappy.BuildConfig;
import happyappy.com.pdesigns.happyappy.R;
import happyappy.com.pdesigns.happyappy.provider.databaseImages;


/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener, Serializable {
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;

    //Diaglog to show db connections status
    private ProgressDialog myDialog;

    // create a json parser object
    JSONParser jsonParser = new JSONParser();


    ArrayList<HashMap<String, String>> imagesList;

    databaseImages dbi = new databaseImages();


    // the url get all images list
    private static String url_all_images = "http://192.168.0.109/happyAppy_connect/get_all_pictures.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_IMAGES = "images";
    private static final String TAG_PID = "picId";
    private static final String TAG_NAME = "picUrl";

    // images JSON Array
    JSONArray images = null;


    private ArrayList<String> mStrings;


    private ImageAdapter adapter;

    private static Context mContext;

    private GridView mGridView = null;

    /**
     * Empty constructor as per the Fragment documentation
     */

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);


    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //hashmap for GridView
        imagesList = new ArrayList<HashMap<String, String>>();
        mStrings = new ArrayList<String>();
        new LoadAllImages().execute();
        mStrings = dbi.getImages();


        final View v = inflater.inflate(R.layout.activity_main, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);


        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!OsUtils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (OsUtils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        return v;


    }


    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //sending the itemClick to single
        Intent i = new Intent(getActivity(), FullScreenImageViewActivity.class);
        // Create a Bundle and Put Bundle in to it

        i.putExtra(FullScreenImageViewActivity.EXTRA_IMAGE, (int) id);
        i.putStringArrayListExtra("IMAGES_LIST", dbi.getImages());
        i.putStringArrayListExtra(String.valueOf(FullScreenImageViewActivity.IMAGES_ARRAY), mStrings);


        i.putExtra("data", dbi.getImages());


        if (OsUtils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getActivity(), R.string.clear_cache_menu,
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row
            return dbi.getImages().size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : dbi.getItem(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage(dbi.getItem(position - mNumColumns), imageView);
            return imageView;
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }

    // A background async task to load all images by making http request
    private class LoadAllImages extends AsyncTask<String, String, String> {
            // Before starting background threads, show the progress dialog
            @Override
            protected void onPreExecute () {
            super.onPreExecute();
            myDialog = new ProgressDialog(getActivity());
            myDialog.setMessage("Loading some awesome images.  Please wait....");
            myDialog.setIndeterminate(false);
            myDialog.setCancelable(false);
            myDialog.show();
        }

            // getting all the images from url
            @Override
            protected String doInBackground (String...args){
            // Building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
                try {
            //getting json string from url
                JSONObject jsonObject = jsonParser.makeHttpRequest(url_all_images, "GET", params);

            // log information for the jspon responce
            Log.d("All images", jsonObject.toString());



                // check for success tag
                System.out.println("CONNNECTION --------------------------------------------------");
                int success = jsonObject.getInt(TAG_SUCCESS);
                System.out.println(success + "");
                // the images was found
                // Getting the array of the images
                images = jsonObject.getJSONArray(TAG_IMAGES);

                // time to loop through all the images
                for (int i = 0; i < images.length(); i++) {
                    JSONObject c = images.getJSONObject(i);

                    //Storing each json item in varible
                    String id = c.getString(TAG_PID);
                    String name = c.getString(TAG_NAME);

                    // creating a new hashmap
                    //   HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to the hashMap key --> value
                    // map.put(TAG_PID, id);
                    // map.put(TAG_NAME, name);

//                    imagesList.add(map);


                    // mStrings.add(i, name);
                    dbi.addImages(i, name);

                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), R.string.no_network_connection_toast,
                        Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

            return null;
        } /**
         * After completing background task Dismiss the progress dialog
         * **/

        protected void onPostExecute(String file_url) {

            // dismiss the dialog after getting all products
            myDialog.dismiss();
            // updating UI from Background Thread
            // updating UI from Background Thread


            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    mGridView.setAdapter(mAdapter);

                }
            });
        }


    }


}



