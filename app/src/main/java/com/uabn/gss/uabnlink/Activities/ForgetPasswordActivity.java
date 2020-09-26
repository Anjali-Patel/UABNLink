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

public class ForgetPasswordActivity extends AppCompatActivity {
EditText email_address,new_password;
Button Submit;
    ProgressBar pb;
String str_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email_address=findViewById(R.id.email_address);
//        new_password=findViewById(R.id.new_password);
//        str_email=getIntent().getStringExtra("mail");
//        str_resetkey=getIntent().getStringExtra("reset_key");
        Submit=findViewById(R.id.Submit);
        pb=findViewById(R.id.ProgessLoad);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            str_confirm=email_address.getText().toString();
//                str_new=new_password.getText().toString();
                if(str_confirm.equalsIgnoreCase("")){
                    Toast.makeText(ForgetPasswordActivity.this,"Please enter your registered Email address",Toast.LENGTH_LONG).show();
//                }else if(str_confirm.equalsIgnoreCase("")){
//                    Toast.makeText(ForgetPasswordActivity.this,"Please enter your confirm password",Toast.LENGTH_LONG).show();
//
////                }else if(!(str_confirm==str_new)){
//                    Toast.makeText(ForgetPasswordActivity.this,"New and Confirm password not matching",Toast.LENGTH_SHORT).show();

                }else{
                    forgotPassword(str_confirm);
                    pb.setVisibility(View.VISIBLE);
                }

            }
        });



    }
    public void   forgotPassword(String str_email) {
        String url = (CommonUtils.BASE_URL) + "forgot_password";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("email", str_email)
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

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = null;

                                        try {
                                            json = new JSONObject(myResponse);
                                            String success = json.getString("status");
                                            if (success.equalsIgnoreCase("success")) {
                                                pb.setVisibility(View.GONE);
                                                Toast.makeText(ForgetPasswordActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();
                                                Intent intent= new Intent(ForgetPasswordActivity.this,Login.class);
                                                startActivity(intent);


//                                                reset_key=json.getString("reset_key");
//                                                Intent intent= new Intent(ForgetPasswordActivity.this,ForgetPasswordActivity.class);
//                                                intent.putExtra("reset_key",reset_key);
//                                                intent.putExtra("mail",email);
//                                                startActivity(intent);


                                            } else {
                                                pb.setVisibility(View.GONE);

                                                Toast.makeText(ForgetPasswordActivity.this,json.getString("message"),Toast.LENGTH_LONG).show();

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

