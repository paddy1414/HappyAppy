package happyappy.com.pdesigns.happyappy.usersStuffs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
    /*  */

/*
 * JSON stands for JavaScript Object Notation.It is an independent data exchange format and is the best alternative for XML.
 * This class is used to get JSON from a URL.  This class suppoort two http request methods, GET and post to get json from URL
  * */

/**
 * Created by Paddy on 29/05/2015.
 */

public class JSONParser {
    //input stream -- A readable source of bytes.
    static InputStream inputStream = null;
    static JSONObject jObj = null;
    static  String  json="";

    public JSONParser () {

    }

    // method to get json from a url by making a http post or get

    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params)
    {
        //make the first http request
        try {
            //check for a request method
            // make sure to try .equals if it works
            if (method =="POST") {

                //request method is in fact POST
                 // defaullt http Client
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            } else if (method == "GET") {
                //if the request is a GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream =httpEntity.getContent();


            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line=bufferedReader.readLine()) !=null) {
                stringBuilder.append(line +"\n");
            }
            inputStream.close();
            json = stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // finally try to parse the string to a json object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return json String
        return jObj;
    }

}
