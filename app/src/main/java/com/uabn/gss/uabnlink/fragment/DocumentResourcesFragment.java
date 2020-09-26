package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.adapter.DocuemntAdapter;
import com.uabn.gss.uabnlink.model.DocumentCategoryModel;
import com.uabn.gss.uabnlink.model.DocumentDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class DocumentResourcesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
ProgressBar pb;
    private String mParam1;
    private String mParam2;
    SharedPreferenceUtils preferances;
    String UserId,imgae_url;
    RelativeLayout MainView;
    FrameLayout progressBarHolder;

    private OnFragmentInteractionListener mListener;
Button SearchButton;
    RecyclerView docuemtlist;
    RecyclerView.LayoutManager layoutmanager;
    ArrayList<DocumentDataModel> DocumentDataModelArrayList = new ArrayList<>();
    Context activityContext;
    DocuemntAdapter adapter;
EditText search_by_library;
    Spinner DocumentCategory;
    ArrayList<String> DocumentList;
    ArrayList<DocumentCategoryModel> CategoryModelArrayList = new ArrayList<>();
    ArrayList<DocumentCategoryModel> CategoryModelArrayListTmp = new ArrayList<>();
String SelectedCategoryId="",str_library="";
    public DocumentResourcesFragment() {

    }

    public static DocumentResourcesFragment newInstance(String param1, String param2) {
        DocumentResourcesFragment fragment = new DocumentResourcesFragment();
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
        View view =  inflater.inflate(R.layout.fragment_document_resources, container, false);
        SearchButton=view.findViewById(R.id.SearchButton);
        docuemtlist = view.findViewById(R.id.docuemtlist);
        pb=view.findViewById(R.id.pb);
        DocumentCategory = view.findViewById(R.id.document_category);
        search_by_library=view.findViewById(R.id.search_by_library);
        MainView = view.findViewById(R.id.MainView);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        DocumentList = new ArrayList<>();

        DocumentList.add("Select Category");
        getCategory();
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        getDocumentlist();
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_library=search_by_library.getText().toString().trim();
                if (!(search_by_library.getText().toString().equalsIgnoreCase(""))) {
                    str_library = search_by_library.getText().toString();
                }else{
     searchDocumentResource(UserId,str_library,SelectedCategoryId);
                }

            }
        });
        ArrayAdapter<String> Documentdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, DocumentList);
        DocumentCategory.setAdapter(Documentdapter1);
        DocumentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedDocuemnt = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < CategoryModelArrayList.size(); i++) {
                    if (CategoryModelArrayList.get(i).getDocument_name().contains(SelectedDocuemnt)) {

                        CategoryModelArrayListTmp.add(CategoryModelArrayList.get(i));
                        SelectedCategoryId = CategoryModelArrayList.get(position - 1).getCategory_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


public void getCategory(){
    String url = (CommonUtils.BASE_URL)+"getDocsResourceCategory";


    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url(url).get().build();

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
                            JSONArray json2 = json.getJSONArray("knowledgebase_categories");
                            for (int i = 0; i < json2.length(); i++) {
                                JSONObject RegionList = json2.getJSONObject(i);
                                JSONObject RegionObj = RegionList.getJSONObject("knowledgebase_categories");
                                DocumentCategoryModel model = new DocumentCategoryModel();
                                model.setCategory_id(RegionObj.getString("id"));
                                model.setDocument_name(RegionObj.getString("name"));
                                CategoryModelArrayList.add(model);
                            }
                            for (int i = 0; i < CategoryModelArrayList.size(); i++) {
                                final DocumentCategoryModel Items = CategoryModelArrayList.get(i);
                                DocumentList.add(Items.getDocument_name());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });


}
 public void  getDocumentlist(){

     MainView.setVisibility(View.GONE);
     progressBarHolder.setVisibility(View.VISIBLE);

     DocumentDataModelArrayList.clear();
    //ProgessLoad.setVisibility(View.VISIBLE);

    String url = (CommonUtils.BASE_URL)+"list_docs_and_resources";

    OkHttpClient client = new OkHttpClient();


    RequestBody formBody = new FormBody.Builder()
            .add("user_id", UserId)
            .add("category_id", "")
            .add("keyword", "")
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
                        String imgae_url=json.getString("imgae_url");

                        if (success.equalsIgnoreCase("success")) {
                            JSONArray update = json.getJSONArray("knowledgebase_data");
                            for (int i = 0; i < update.length(); i++) {
                                JSONObject UpdateDetails = update.getJSONObject(i);
                                DocumentDataModel model = new DocumentDataModel();
                                model.setDocumentName(UpdateDetails.getString("title").replace("null", ""));
                                Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                model.setDocumentPosted(CommentPostDate);

//                            model.setPBCRDate(UpdateDetails.getString("created_on").replace("null", ""));
                                model.setDocemntDetails(UpdateDetails.getString("description").replace("null", ""));
                                model.setId(UpdateDetails.getString("id").replace("null", ""));
                                model.setCat_id(UpdateDetails.getString("cat_id").replace("null", ""));

                                model.setDocumentIcon(imgae_url + UpdateDetails.getString("id") + "/" + UpdateDetails.getString("media_name"));
                                model.setMedia_type(UpdateDetails.getString("media_type").replace("null", ""));
                                model.setWebsite(UpdateDetails.getString("website").replace("null",""));//                                model.setPBCR_id(UpdateDetails.getString("pbcr_id").replace("null", ""));
                                DocumentDataModelArrayList.add(model);
//                                adapter.notifyDataSetChanged();
                            }

                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);

                            adapter = new DocuemntAdapter(getActivity(), DocumentDataModelArrayList, activityContext);
                            layoutmanager = new LinearLayoutManager(getActivity());
                            docuemtlist.setLayoutManager(layoutmanager);
                            docuemtlist.setAdapter(adapter);


                        } else {
                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);

                        Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
}
public void searchDocumentResource(String user_id,String keyword,String category_id){

    MainView.setVisibility(View.GONE);
    progressBarHolder.setVisibility(View.VISIBLE);

    DocumentDataModelArrayList.clear();
    //ProgessLoad.setVisibility(View.VISIBLE);

    String url = (CommonUtils.BASE_URL)+"list_docs_and_resources";

    OkHttpClient client = new OkHttpClient();


    RequestBody formBody = new FormBody.Builder()
            .add("user_id", user_id)
            .add("category_id", category_id)
            .add("keyword", keyword)
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
                        String imgae_url=json.getString("imgae_url");

                        if (success.equalsIgnoreCase("success")) {
                            JSONArray update = json.getJSONArray("knowledgebase_data");
                            for (int i = 0; i < update.length(); i++) {
                                JSONObject UpdateDetails = update.getJSONObject(i);
                                DocumentDataModel model = new DocumentDataModel();
                                model.setDocumentName(UpdateDetails.getString("title").replace("null", ""));
                                Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(UpdateDetails.getString("created_date"));
                                String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                model.setDocumentPosted(CommentPostDate);

                                model.setDocemntDetails(UpdateDetails.getString("description").replace("null", ""));
                                model.setCat_id(UpdateDetails.getString("id").replace("null", ""));

                                model.setDocumentIcon(UpdateDetails.getString("media_name").replace("null", ""));
                                model.setDocumentIcon(imgae_url + UpdateDetails.getString("id") + "/" + UpdateDetails.getString("media_name"));
                                model.setWebsite(UpdateDetails.getString("website").replace("null",""));
                                DocumentDataModelArrayList.add(model);
                            }

                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);

                            adapter = new DocuemntAdapter(getActivity(), DocumentDataModelArrayList, activityContext);
                            layoutmanager = new LinearLayoutManager(getActivity());
                            docuemtlist.setLayoutManager(layoutmanager);
                            docuemtlist.setAdapter(adapter);
                        } else {

                            MainView.setVisibility(View.VISIBLE);
                            progressBarHolder.setVisibility(View.GONE);
Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
}
}


