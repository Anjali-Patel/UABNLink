package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class InviteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String str_email, str_message;
    EditText email, message;
    Button ShareButton;
    String UserId;
    SharedPreferenceUtils preferances;
    private OnFragmentInteractionListener mListener;

    public InviteFragment() {

    }


    public static InviteFragment newInstance(String param1, String param2) {
        InviteFragment fragment = new InviteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        email = view.findViewById(R.id.email);
        message = view.findViewById(R.id.message);
        ShareButton = view.findViewById(R.id.ShareButton);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_email = email.getText().toString();
                str_message = message.getText().toString();

//                InviteConnection(str_email, str_message);
                str_email=email.getText().toString();
                str_message=message.getText().toString();
                if(str_email.equalsIgnoreCase("")){
                    Toast.makeText(getContext(),"Please enter your Email Address",Toast.LENGTH_SHORT).show();
                }else if(str_message.equalsIgnoreCase("")){
                    Toast.makeText(getContext(),"Please enter your  Message",Toast.LENGTH_SHORT).show();

                }else if(!isValidEmail(email.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                }

                else {
                    InviteConnection(str_email, str_message);
                }
            }
        });

        return view;
    }
//    public static boolean isValidEmail(CharSequence target) {
//        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
//    }
    private boolean isValidEmail(CharSequence target) {
        boolean emailflag = false;
        String emailArr[] = target.toString().split("[,]");
        for (int i = 0; i < emailArr.length; i++) {
            emailflag = Patterns.EMAIL_ADDRESS.matcher(
                    emailArr[i].trim()).matches();
        }
        return emailflag;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void InviteConnection(String str_email, String str_message) {

        String url = (CommonUtils.BASE_URL)+"invite_contact";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", UserId)
                .add("emails", str_email)
                .add("message", str_message)
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(myResponse);
                                String success = json.getString("status");
                                if (success.equalsIgnoreCase("success")) {
                                    Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                    email.setText("");
                                    message.setText("");
                                } else {
                                    Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                    email.setText("");
                                    message.setText("");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        );
    }

}
































