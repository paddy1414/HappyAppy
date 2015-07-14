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

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import happyappy.com.pdesigns.happyappy.ApplicationResources.ImageFetcher;
import happyappy.com.pdesigns.happyappy.ApplicationResources.ImageWorker;
import happyappy.com.pdesigns.happyappy.ApplicationResources.OsUtils;
import happyappy.com.pdesigns.happyappy.R;


/**
 * This fragment will populate the children of the ViewPager from {@link happyappy.com.pdesigns.happyappy.usersStuffs.FullScreenImageViewActivity}.
 */
public class FullScreenImageViewFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FullScreenImageViewFragment newInstance(String imageUrl) {
        final FullScreenImageViewFragment f = new FullScreenImageViewFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public FullScreenImageViewFragment() {
    }

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link FullScreenImageViewFragment#newInstance(String)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.fragment_slide_screen_page, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (FullScreenImageViewActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((FullScreenImageViewActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && OsUtils.hasHoneycomb()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.setWallPaper:

                // Set as wallpaper
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getActivity().getApplicationContext());
                try {
                    ImageView img = (ImageView) getView().findViewById(R.id.imageView);
                    Bitmap bmd = ((BitmapDrawable) img.getDrawable()).getBitmap();

                    File mFile = savebitmap(bmd);

                    myWallpaperManager.setBitmap(bmd);


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("Wallpaper");
                    builder1.setMessage("Wallpaper has been set");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    return true;

                }

                return true;
            case R.id.share:
//Sharing the image
                // getting the current image on display ...
           //     final int extraCurrentItem = getActivity().getIntent().getIntExtra(extra_image_data, -1);
                // String key = mImageCache.hashKeyForDisk(IMAGESArray.get(extraCurrentItem ));
                ImageView img = (ImageView) getView().findViewById(R.id.imageView);
                Bitmap bmd = ((BitmapDrawable) img.getDrawable()).getBitmap();


                try {
                    File mFile = savebitmap(bmd);


                    Intent shareIntent = new Intent();

                    Uri u = null;
                    u = Uri.fromFile(mFile);

                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, u);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Sent From HappyAppy");
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_via_menu)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;


        }

        return super.onOptionsItemSelected(item);

    }

    private File savebitmap(Bitmap bmp) throws IOException {
        OutputStream outStream = null;

        //you can create a new file name "test.jpeg"
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + "tmp.jpeg");
        if (f.exists()) {
            f.delete();
            //you can create a new file name "test.jpeg"
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + "tmp.jpeg");
        }

        try {

            outStream = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            Log.d("done", "done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();


        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

}
