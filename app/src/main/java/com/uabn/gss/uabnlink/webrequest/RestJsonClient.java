package com.uabn.gss.uabnlink.webrequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class RestJsonClient {
    private Context _context;


    public static JSONObject connect(String url) {
        // TODO Auto-generated method stub
        System.out.println("url in restjsonclient is " + url);
        HttpClient httpclient = new DefaultHttpClient();


        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        JSONObject json = new JSONObject();
        System.out.println("httpget is " + httpget);

        try {
            response = httpclient.execute(httpget);
            System.out.println("response in restjoson cleis- " + response);
            Log.v("response code ", response.getStatusLine().getStatusCode() + "");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                json = new JSONObject(result);
                instream.close();
            }
        } catch (ClientProtocolException e) {
            System.out.println("client");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            System.out.println("illegal");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexcepiton");
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("jsonex");
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject post(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        JSONObject json = new JSONObject();
        UrlEncodedFormEntity form;

        try {
            form = new UrlEncodedFormEntity(nameValuePairs);
            form.setContentEncoding(HTTP.UTF_8);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            Charset chars = Charset.forName("UTF-8"); // Setting up the encoding
            StringBody stringB;  // Now lets add some extra information in a StringBody
            for (int index = 0; index < nameValuePairs.size(); index++) {

                stringB = new StringBody(nameValuePairs.get(index).getValue(), chars);
                entity.addPart(nameValuePairs.get(index).getName(), stringB);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost, localContext);

            System.out.println("response in restjoson cleis- " + response);
            Log.v("response code ", response.getStatusLine().getStatusCode() + "");
            HttpEntity httpentity = response.getEntity();

            if (httpentity != null) {
                InputStream instream = httpentity.getContent();
                String result = convertStreamToString(instream);
                System.out.println("res:::" + result);
                json = new JSONObject(result);
                instream.close();
            }
        } catch (ClientProtocolException e) {
            System.out.println("client");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            System.out.println("illegal");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexcepiton");
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("jsonex");
            e.printStackTrace();
        }
        return json;
    }
    public static JSONObject post2(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            Charset chars = Charset.forName("UTF-8");
            StringBody stringB;
            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getName().equalsIgnoreCase("file_name") ||
                        nameValuePairs.get(index).getName().startsWith("profile_picture")
                    ||nameValuePairs.get(index).getName().equalsIgnoreCase("img")||
                          nameValuePairs.get(index).getName().equalsIgnoreCase("photo1")
                ) {
                    try {
                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    stringB = new StringBody(nameValuePairs.get(index).getValue(), chars);
                    entity.addPart(nameValuePairs.get(index).getName(), stringB);
                }
            }
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            System.out.println("response in restjoson cleis- " + response);
            Log.v("response code ", response.getStatusLine().getStatusCode() + "");
            HttpEntity httpentity = response.getEntity();
            if (httpentity != null) {
                InputStream instream = httpentity.getContent();
                String result = convertStreamToString(instream);
                Log.d("data", result);
                json = new JSONObject(result);
//				jsonArray = new JSONArray(result);
                instream.close();
            }
        } catch (ClientProtocolException e) {
            System.out.println("client");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            System.out.println("illegal");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexcepiton");
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("jsonex");
            e.printStackTrace();
        }
        return json;
//		return  jsonArray;
    }
    public static JSONObject SendData(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            Charset chars = Charset.forName("UTF-8");
            StringBody stringB;
            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getName().equalsIgnoreCase("file_name")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("image")
                            ||nameValuePairs.get(index).getName().contains("photo") ||
                        nameValuePairs.get(index).getName().contains("document") ||
                         nameValuePairs.get(index).getName().equalsIgnoreCase("profile_picture")) {
                    try {
//                       String type = null;
//                        String extension = MimeTypeMap.getFileExtensionFromUrl(nameValuePairs.get(index).getValue());
//                        if (extension != null) {
//                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                        }
//                              to upload image file
//                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())),type);
                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Normal string data
//                    stringB = new StringBody(nameValuePairs.get(index).getValue(),chars);
//                    // Normal string data
//                    entity.addPart(nameValuePairs.get(index).getName(), stringB);
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue(), chars));
                }
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            System.out.println("response in restjoson cleis- " + response);
            Log.v("response code ", response.getStatusLine().getStatusCode() + "");
            HttpEntity httpentity = response.getEntity();

            if (httpentity != null) {
                InputStream instream = httpentity.getContent();
                String result = convertStreamToString(instream);
                Log.d("data", result);
                json = new JSONObject(result);
//				jsonArray = new JSONArray(result);
                instream.close();
            }
        } catch (ClientProtocolException e) {
            System.out.println("client");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            System.out.println("illegal");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexcepiton");
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("jsonex");
            e.printStackTrace();
        }
        return json;
//		return  jsonArray;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().replace("<pre>", "").replace("<p>", "").replace("<em>", "").replace("</p>", "").replace("</pre>", "");
    }


    @SuppressLint("LongLogTag")
    public static boolean isNetworkAvailable1(Activity activity) {
        {
            Log.i("Context", "value of Activity is " + activity);
            System.out.println("hi eerror");
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                    if (ni.isConnected()) {
                        haveConnectedWifi = true;
                        Log.v("WIFI CONNECTION ", "AVAILABLE");
                    } else {
                        Log.v("WIFI CONNECTION ", "NOT AVAILABLE");
                    }
                }
                if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                    if (ni.isConnected()) {
                        haveConnectedMobile = true;
                        Log.i("MOBILE INTERNET CONNECTION ", "AVAILABLE");
                    } else {
                        Log.d("MOBILE INTERNET CONNECTION ", "NOT AVAILABLE");
                    }
                }
            }
            return haveConnectedWifi || haveConnectedMobile;
        }


    }


    public static JSONObject postAdv(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            Charset chars = Charset.forName("UTF-8");
            StringBody stringB;

            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getName().equalsIgnoreCase("img1")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img2")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img3")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img4")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img5")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img6")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img7")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img8")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("img9")
                        || nameValuePairs.get(index).getName().equalsIgnoreCase("im10")) {
                    try {
//                       String type = null;
//                        String extension = MimeTypeMap.getFileExtensionFromUrl(nameValuePairs.get(index).getValue());
//                        if (extension != null) {
//                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                        }
//                              to upload image file
//                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())),type);
                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Normal string data
//                    stringB = new StringBody(nameValuePairs.get(index).getValue(),chars);
//                    // Normal string data
//                    entity.addPart(nameValuePairs.get(index).getName(), stringB);
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue(), chars));
                }
            }

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            System.out.println("response in restjoson cleis- " + response);
            Log.v("response code ", response.getStatusLine().getStatusCode() + "");
            HttpEntity httpentity = response.getEntity();

            if (httpentity != null) {
                InputStream instream = httpentity.getContent();
                String result = convertStreamToString(instream);
                Log.d("data", result);
                json = new JSONObject(result);
//				jsonArray = new JSONArray(result);
                instream.close();
            }
        } catch (ClientProtocolException e) {
            System.out.println("client");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            System.out.println("illegal");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexcepiton");
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("jsonex");
            e.printStackTrace();
        }
        return json;
//		return  jsonArray;
    }
}