package com.uabn.gss.uabnlink.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIME = 3000;
    SharedPreferenceUtils preferances;
    String  user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferances = SharedPreferenceUtils.getInstance(this);
        // preferances.setValue(CommonUtils.DeviceId,"867273027336291");

        // startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        preferances = SharedPreferenceUtils.getInstance(this);
        user_id=(preferances.getStringValue(CommonUtils.MEMBER_ID,""));

        getNotificationCount();

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!user_id.equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    handler.removeCallbacks(this);
                } else{
                    Intent intent =new Intent(SplashActivity.this,Login.class);
                    startActivity(intent);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    handler.removeCallbacks(this);
                }


//                Intent mainIntent = new Intent(SplashActivity.this,
//                        MainActivity.class);
//                SplashActivity.this.startActivity(mainIntent);
//                SplashActivity.this.finish();
//                handler.removeCallbacks(this);
            }
        }, SPLASH_DISPLAY_TIME);

    }


    public void getNotificationCount() {
        String url = (CommonUtils.BASE_URL) +"notification_count";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                final String myResponse = responseBody.string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                String count = json.getString("notification_count");
                                
                                preferances.setValue(CommonUtils.NOTIFICATIONCOUNTBGDGE, String.valueOf(count));

                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
