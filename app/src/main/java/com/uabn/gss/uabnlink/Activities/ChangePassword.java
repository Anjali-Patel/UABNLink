package com.uabn.gss.uabnlink.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class ChangePassword extends AppCompatActivity {
EditText confirm_password,new_password,old_psw;
    Button Submit;
    SharedPreferenceUtils preferances;
    String str_confirm,str_new,str_old,user_id;
    ProgressBar ProgessLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        confirm_password=findViewById(R.id.confirm_password);
        new_password=findViewById(R.id.new_password);
        old_psw=findViewById(R.id.old_psw);
        Submit=findViewById(R.id.Submit);
        preferances = SharedPreferenceUtils.getInstance(ChangePassword.this);
        user_id=preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        ProgessLoad=findViewById(R.id.ProgessLoad);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_old = old_psw.getText().toString();
                str_confirm = confirm_password.getText().toString();
                str_new = new_password.getText().toString();
                if (str_old.equalsIgnoreCase("")) {
                    Toast.makeText(ChangePassword.this, "Please enter your old password", Toast.LENGTH_LONG).show();
                } else if (str_new.equalsIgnoreCase("")) {
                    Toast.makeText(ChangePassword.this, "Please enter your new password", Toast.LENGTH_LONG).show();
                } else if (str_confirm.equalsIgnoreCase("")) {
                    Toast.makeText(ChangePassword.this, "Please enter your confirm password", Toast.LENGTH_LONG).show();
                } else if(!(str_confirm.equalsIgnoreCase(str_new))){
                    Toast.makeText(ChangePassword.this, "Confirm Password and New Password not matching", Toast.LENGTH_LONG).show();
                }
                else {
                    changePassword(user_id,str_new,str_old);
                    ProgessLoad.setVisibility(View.VISIBLE);

                }
            }
        });

    }
    public void changePassword(String user_id,String str_new,String str_old){
        String url = (CommonUtils.BASE_URL) + "update_password";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id",user_id )
                .add("old_password",str_old )
                .add("new_password",str_new )
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        final String myResponse = responseBody.string();
                        ChangePassword.this.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = null;
                                        ProgessLoad.setVisibility(View.GONE);
                                        try {
                                            json = new JSONObject(myResponse);
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                Toast.makeText(ChangePassword.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                                preferances.setValue(CommonUtils.MEMBER_ID, "");
                                                preferances.setValue(CommonUtils.NAME, "");
                                                preferances.setValue(CommonUtils.MEMBEREMAIL, "");
                                                Intent intent = new Intent(ChangePassword.this, Login.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(ChangePassword.this, json.getString("message"), Toast.LENGTH_LONG).show();
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
