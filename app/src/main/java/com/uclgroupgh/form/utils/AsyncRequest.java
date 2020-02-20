package com.uclgroupgh.form.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.uclgroupgh.form.pojo.Message;
import com.uclgroupgh.form.pojo.ResponseMessages;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Junior on 1/16/2018.
 */

public class AsyncRequest extends AsyncTask<String, Integer, String> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    OnAsyncRequestComplete caller;
    Context context;
    String method = "GET";
    Map<String, String> parameters = null;
    ProgressDialog pDialog = null;
    Message message;
    String query;
    String EQ;
    String AMP;
    Map.Entry<String, String> entry;
    Request request;
    Response response;

    // Three Constructors
    public AsyncRequest(Activity a, String m, Map<String, String> p) {
        caller = (OnAsyncRequestComplete) a;
        context = a;
        method = m;
        parameters = p;
    }

    public AsyncRequest(Activity a, String m) {
        caller = (OnAsyncRequestComplete) a;
        context = a;
        method = m;
    }

    public AsyncRequest(Activity a) {
        caller = (OnAsyncRequestComplete) a;
        context = a;
    }

    public String doInBackground(String... urls) {
        // get url pointing to entry point of API
        String address = urls[0].toString();
        //String jsonstring = urls[1].toString();
        if (method == "POST") {
            return post(address);//, jsonstring);
        }

        if (method == "GET") {
            return get(address);
        }

        return null;
    }

    public void onPreExecute() {
//        pDialog = new ProgressDialog(context);
//        pDialog.setMessage("Loading data.."); // typically you will define such
//        // strings in a remote file.
//        pDialog.show();
    }

    public void onProgressUpdate(Integer... progress) {
        // you can implement some progressBar and update it in this record
        // setProgressPercent(progress[0]);
    }

    public void onPostExecute(String response) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        caller.asyncResponse(response);
    }

    protected void onCancelled(String response) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        caller.asyncResponse(response);
    }

    //@SuppressWarnings("deprecation")
    private String get(String address) {
        String msg = "";
        try {

            if (parameters != null) {
                query = "";
                EQ = "=";
                AMP = "&";

                for (Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator();
                     it.hasNext(); ) {
                    entry = it.next();
                    query += entry.getKey() + EQ + URLEncoder.encode(entry.getValue()) + AMP;
                }

                if (query != "") {
                    address += "?" + query;
                }
            }
            request = new Request.Builder()
                    .url(address)
                    .build();

            response = client.newCall(request).execute();
            msg = response.body().string();
            // return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            message = new Message();
            message.setMessagetype(ResponseMessages.ERROR);
            message.setMsg("Error Connecting To Server...");
            msg = new Gson().toJson(message);

            // TODO Auto-generated catch block
        }
        return msg;
    }

    private String post(String address)//, String json)
    {
        String msg = "";
//        try
//        {
//            RequestBody body = RequestBody.create(JSON, json);
//            Request request = new Request.Builder()
//                    .url(address)
//                    .post(body)
//                    .build();
//            Response response = client.newCall(request).execute();
//            msg = response.body().string();
//        }
//        catch (Exception e)
//        {
//            message = new Message();
//            message.setMessagetype(ResponseMessages.ERROR);
//            message.setMsg("Error Connecting To Server...");
//            msg = new Gson().toJson(message);
//        }

        return msg;
    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        public void asyncResponse(String response);
    }


}
