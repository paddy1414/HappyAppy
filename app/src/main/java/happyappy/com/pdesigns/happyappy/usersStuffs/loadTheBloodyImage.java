package happyappy.com.pdesigns.happyappy.usersStuffs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import happyappy.com.pdesigns.happyappy.R;

/**
 * Created by Patrick on 06/08/14.
 */
public class loadTheBloodyImage
{
    MemoryCach memoryCach = new MemoryCach();

    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    //handler to display images in the UI thread
    Handler handler = new Handler();

    public loadTheBloodyImage(Context c)
    {
        fileCache = new FileCach(c);
        executorService= Executors.newFixedThreadPool(5);
    }

    final int loading_id = R.drawable.loading1;
    public void DisplayImage (String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCach.get(url);
        if(bitmap!=null)
        {
            // sets the rescaled image to my specified size
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120,120, false));
        }
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(loading_id);
        }


    }

    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url)
    {
        File f = fileCache.getFile(url);

        //from SD card cach
        Bitmap b = decodeFile(f);
        if(b!=null)
        {
            return b;
        }

        //from web/// VERY IMPORTANT

        try
        {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            InputStream is = connection.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            connection.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        }
        catch(Throwable ex )
        {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
            {
                memoryCach.clear();

            }
            return null;
        }
    }

    public static Bitmap decodeFile (File f)
    {
        try {
        //first decode with inJudtDevodeBounds = true to check dimensions
          final BitmapFactory.Options options= new BitmapFactory.Options();
         options.inJustDecodeBounds=true;
            FileInputStream stream1 = new FileInputStream(f);
         BitmapFactory.decodeStream(stream1, null, options);

        //calculate inSampleSIze
            final int reqHeight =70;
            final int reqWidth = 70;
            final int oldHeight = options.outHeight;
            final int oldWidth = options.outWidth;

            int scale=1;

            if (oldHeight>reqHeight || oldWidth>reqHeight)
            {
                final int halfHeight =oldHeight/2;
                final int halfWidth = oldWidth/2;

                //calculate the largest inSameSize value that is a power of 2 and keep both
                //height and width larger than requested heighnt and width

                while((halfHeight/scale)>reqHeight&& (halfWidth/scale)>reqWidth)
                {
                    scale *=2;
                }
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);

            return  bitmap;
    }
        catch (FileNotFoundException e)
        {

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return  null;
    }

    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;

        public PhotoToLoad (String u, ImageView iv)
        {
            url = u;
            imageView = iv;
        }
    }

    private class PhotosLoader implements Runnable
    {
        PhotoToLoad photoToLoad1;
        PhotosLoader(PhotoToLoad photoToLoadInternal )
        {
            this.photoToLoad1=photoToLoadInternal;
        }

        @Override
        public void run() {
            try
            {
                if (imageViewReused(photoToLoad1))
                {
                    return;
                }
                Bitmap bmp = getBitmap(photoToLoad1.url);
                memoryCach.put(photoToLoad1.url, bmp);

                if (imageViewReused(photoToLoad1))
                {
                    return;
                }

                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad1);
                handler.post(bd);
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad)
    {
        String tag = imageViews.get(photoToLoad.imageView);
        if(tag == null|| !tag.equals(photoToLoad.url))
        {
            return true;
        }
        return  false;
    }

    private class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad3;
        public BitmapDisplayer (Bitmap b, PhotoToLoad p)
        {
            bitmap = b;
            photoToLoad3 = p;
        }

        @Override
        public void run()
        {
            if (imageViewReused(photoToLoad3))
            {
                return;
            }
            if (bitmap!=null)
            {
                photoToLoad3.imageView.setImageBitmap(bitmap);
            }
            else
            {
                photoToLoad3.imageView.setImageResource(loading_id);
            }

        }
    }


    FileCach fileCache;

    public void clearCache() {
        memoryCach.clear();
        fileCache.clear();
    }




}
