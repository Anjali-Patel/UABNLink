package com.uabn.gss.uabnlink.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.Activities.ChangePassword;
import com.uabn.gss.uabnlink.Activities.GraduateSchoolActivity;
import com.uabn.gss.uabnlink.Activities.HighSchoolActivity;
import com.uabn.gss.uabnlink.Activities.MainActivity;
import com.uabn.gss.uabnlink.Activities.UnderGraduateSchoolActivity;
import com.uabn.gss.uabnlink.Activities.UserProfileData;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.model.CertificationModel;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.CountryWiseStateModel;
import com.uabn.gss.uabnlink.model.HomeCertifiedBusinessModel;
import com.uabn.gss.uabnlink.model.IncomeModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
import com.uabn.gss.uabnlink.model.NationModel;
import com.uabn.gss.uabnlink.model.PoliticalAffiliationModel;
import com.uabn.gss.uabnlink.model.ProfessionalStatusModel;
import com.uabn.gss.uabnlink.model.ServiceModel;
import com.uabn.gss.uabnlink.model.SportModel;
import com.uabn.gss.uabnlink.model.TribalReligiousModel;
import com.uabn.gss.uabnlink.webrequest.RestJsonClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UserProfile extends Fragment implements BSImagePicker.OnMultiImageSelectedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner country_list, state, business_list, professional_status, service_provider, from, inductry_focus;
    Spinner current_country,political_affiliation,sport_interest,certifications,income_range,investment_type,tribal_affiliation;
    SharedPreferenceUtils preferances;
    ArrayList<ServiceModel> ServiceModelArrayList = new ArrayList<>();
    ArrayList<ServiceModel> ServiceModelArrayListTmp = new ArrayList<>();
    ArrayList<NationModel> NationModelArraylist = new ArrayList<>();
    ArrayList<NationModel> NationModelArraylistTemp = new ArrayList<>();
    ArrayList<TribalReligiousModel> TribalModelArrayList = new ArrayList<>();
    ArrayList<TribalReligiousModel> TribalModelArrayListTemp = new ArrayList<>();
    ArrayList<SportModel> SportModelArraylist = new ArrayList<>();
    ArrayList<SportModel> SportModelArraylistTemp = new ArrayList<>();
    ArrayList<IncomeModel> IncomeModelArrayList = new ArrayList<>();
    ArrayList<IncomeModel> IncomeModelArrayListTemp = new ArrayList<>();
    ArrayList<String> nation_list;
    String UserId;
    ImageView userImg, ChangeUserImage;
    EditText Fname, Lname, city, phone, mobile, employer, bio;
    ProgressBar progess_load;
    Button change_psw, update, cancel,upload_resume;
    ArrayList<String> ServiceList;
    ArrayList<String> tribal_affiliation_list;
    ArrayList<String> searchState;
    ArrayList<String> SearchCountry;
    ArrayList<String> searchProfessional;
    ArrayList<String> searchCertified;
    ArrayList<String> industryFocus;
    ArrayList<String> ListState;
    ArrayList<String> currentCountryList;
    ArrayList<String> politicalAffiliationList;
    ArrayList<String> sportInterestList;
    ArrayList<String> CertificationList;
    String DocPath="";
    ArrayList<String> IncomeRangeList;
    ArrayList<String> DocumentList = new ArrayList<>();
    ArrayList<String> InvestmentTypeList;
    ArrayList<CountryModel> CountryModelArrayList = new ArrayList<>();
    ArrayList<CountryModel> CountryModelArrayListTmp = new ArrayList<>();
    ArrayList<CertificationModel> CertificationModelList = new ArrayList<>();
    ArrayList<CertificationModel> CertificationModelListTemp = new ArrayList<>();
    ArrayList<PoliticalAffiliationModel> PoliticalAffiliationModelArrayList = new ArrayList<>();
    ArrayList<PoliticalAffiliationModel> PoliticalAffiliationModelArrayListTemp = new ArrayList<>();
    ArrayList<CountryWiseStateModel> StateModelArrayList;
    ArrayList<HomeCertifiedBusinessModel> homeCertifiedBusinessModels;
    String SelectedServiceId = "", SelectedCountryId = "", FromSelectedId = "", SelectedStateId = "";
    ArrayList<HomeCertifiedBusinessModel> homeCertifiedBusinessModelsTemp;
    ArrayList<ProfessionalStatusModel> professionalStatusModels;
    ArrayList<ProfessionalStatusModel> professionalStatusModelsTemp;
    ArrayList<IndustryModel> IndystryFocusList;
    String SelectedProfessionalStatusId = "", SelectedCertifiedId = "", SelectedIndustryFocusId = "",Tribal_affiliationId="",PoliticalAffiliationId="",IncomeId="",CertificationId="",SportId="",CurrentCountryId="";
    String UserImgPath = "";
    RadioGroup AftricanLivingRadio;
    RadioButton LivingYes, LivingNo;
    TextView FromNationLable;
    RelativeLayout FromNationSpinnerRelative;
    TextView graduate_school,undergraduate_school,high_school;
    EditText current_city;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    int position = 0;
    CheckBox checkbox_job,checkbox_business;
    TextView uploaded_document;
    String IsProfesionalLiving = "0";
    String str_current_city="",str_interestedin_job,str_interested_inbusiness;
    String SelectedCountry = "", SelectedState = "", SelectedService = "", SelectedProfessionaStatus = "", SelectedCertified = "", SelectedIndustryFocus = "", SelectedNation = "";
    public UserProfile() {
        // Required empty public constructor
    }
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        preferances = SharedPreferenceUtils.getInstance(getActivity());
        UserId = preferances.getStringValue(CommonUtils.MEMBER_ID, "");
        userImg = view.findViewById(R.id.userImg);
        ChangeUserImage = view.findViewById(R.id.ChangeUserImage);
        Fname = view.findViewById(R.id.Fname);
        current_city = view.findViewById(R.id.current_city);
        uploaded_document=view.findViewById(R.id.uploaded_document);
        tribal_affiliation = view.findViewById(R.id.tribal_affiliation);
        checkbox_job=view.findViewById(R.id.checkbox_job);
        graduate_school = view.findViewById(R.id.graduate_school);
        checkbox_business=view.findViewById(R.id.checkbox_business);
        undergraduate_school = view.findViewById(R.id.undergraduate_school);
        high_school = view.findViewById(R.id.high_school);
        Lname = view.findViewById(R.id.Lname);
        upload_resume=view.findViewById(R.id.upload_resume);
        city = view.findViewById(R.id.city);
        phone = view.findViewById(R.id.phone);
        mobile = view.findViewById(R.id.mobile);
        from = view.findViewById(R.id.from);
        current_country=view.findViewById(R.id.current_country);
        political_affiliation=view.findViewById(R.id.political_affiliation);
        sport_interest=view.findViewById(R.id.sport_interest);
        certifications=view.findViewById(R.id.certifications);
        income_range=view.findViewById(R.id.income_range);
        investment_type=view.findViewById(R.id.investment_type);
        employer = view.findViewById(R.id.employer);
        bio = view.findViewById(R.id.bio);
        update = view.findViewById(R.id.update);
        cancel = view.findViewById(R.id.cancel);
        AftricanLivingRadio = view.findViewById(R.id.AftricanLivingRadio);
        LivingYes = view.findViewById(R.id.LivingYes);
        LivingNo = view.findViewById(R.id.LivingNo);
        FromNationLable = view.findViewById(R.id.FromNationLable);
        FromNationSpinnerRelative = view.findViewById(R.id.FromNationSpinnerRelative);
        progess_load = view.findViewById(R.id.progess_load);
        homeCertifiedBusinessModels = new ArrayList<>();
        homeCertifiedBusinessModelsTemp = new ArrayList<>();
        professionalStatusModels = new ArrayList<>();
        professionalStatusModelsTemp = new ArrayList<>();
        TribalModelArrayList = new ArrayList<>();
        TribalModelArrayListTemp = new ArrayList<>();
        IndystryFocusList = new ArrayList<>();
        StateModelArrayList = new ArrayList<CountryWiseStateModel>();
        searchCertified = new ArrayList<>();
        searchProfessional = new ArrayList<>();
        currentCountryList=new ArrayList<>();
        politicalAffiliationList=new ArrayList<>();
        sportInterestList=new ArrayList<>();
        CertificationList=new ArrayList<>();
        IncomeRangeList=new ArrayList<>();
        nation_list = new ArrayList<>();
        ListState = new ArrayList<>();
        tribal_affiliation_list= new ArrayList<>();
        tribal_affiliation_list.add("Select Religion");
        currentCountryList.add("Select Current Country");
        politicalAffiliationList.add("Select");
        sportInterestList.add("select");
        CertificationList.add("Select");
        IncomeRangeList.add("Select");
        InvestmentTypeList=new ArrayList<>();
        InvestmentTypeList.add("Select Investment Type");
        nation_list.add("Select Nation");
        upload_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent,"Choose file"), 100);
            }
        });
//        fromlist= new ArrayList<>();
//        fromlist.add("From");
        high_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), HighSchoolActivity.class);
                startActivity(intent);
            }
        });
        graduate_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), GraduateSchoolActivity.class);
                startActivity(intent);
            }
        });
        undergraduate_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), UnderGraduateSchoolActivity.class);
                startActivity(intent);
            }
        });
        high_school.setText(CommonUtils.high_school_id);
        String as=CommonUtils.graduate_school_id;
        graduate_school.setText(as);
        undergraduate_school.setText(CommonUtils.undergraduate_school_id);
        getProfessionalStatus();
        getHomeCertifiedBusiness();
        getServiceDetails();
        getCountryDetails();
        getNation();
        getIndusctryFocus();
        getUserData(UserId);
        getSportInterest();
        getPoliticalAffiliation();
        getIncomeRanges();
        getCertification();
        getReligiousTribal();
        country_list = view.findViewById(R.id.country_list);
        state = view.findViewById(R.id.state_list);
        business_list = view.findViewById(R.id.business_list);
        professional_status = view.findViewById(R.id.professional_status);
        service_provider = view.findViewById(R.id.service_provider);
        inductry_focus = view.findViewById(R.id.inductry_focus);
        change_psw = view.findViewById(R.id.change_psw);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("New");
        strings.add("New");
        strings.add("New");
        strings.add("New");
        searchCertified.add("Select Certified Business");
        searchProfessional.add("Select Professional Status");
        ServiceList = new ArrayList<>();
        ServiceList.add("Select Service");
        SearchCountry = new ArrayList<>();
        SearchCountry.add("Select Country");
        searchState = new ArrayList<>();
        IndystryFocusList = new ArrayList<>();
        industryFocus = new ArrayList<>();
        industryFocus.add("Select Industry");
        searchState.add("Select State");
        AftricanLivingRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.LivingYes:
                        FromNationLable.setVisibility(View.VISIBLE);
                        FromNationSpinnerRelative.setVisibility(View.VISIBLE);
                        IsProfesionalLiving = "1";
                        break;
                        case R.id.LivingNo:
                        FromNationLable.setVisibility(View.GONE);
                        FromNationSpinnerRelative.setVisibility(View.GONE);
                        IsProfesionalLiving = "0";
                        break;
                }
            }
        });
        change_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ChangeUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(1)
                        .setTag("Picker")
                        .build();
                pickerDialog.show(getChildFragmentManager(), "Picker");
            }
        });
        ArrayAdapter<String> CurrentCountryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, SearchCountry);
        current_country.setAdapter(CurrentCountryAdapter);
        current_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedService = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                    if (CountryModelArrayList.get(i).getCountryName().contains(SelectedService)) {
                        CountryModelArrayListTmp.add(CountryModelArrayList.get(i));
                        CurrentCountryId = CountryModelArrayList.get(position - 1).getCountryId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> PoliticalAffiliationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, politicalAffiliationList);
        political_affiliation.setAdapter(PoliticalAffiliationAdapter);
        political_affiliation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedService = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
                    if (PoliticalAffiliationModelArrayList.get(i).getPolitical_name().contains(SelectedService)) {
                        PoliticalAffiliationModelArrayListTemp.add(PoliticalAffiliationModelArrayList.get(i));
                        PoliticalAffiliationId = PoliticalAffiliationModelArrayList.get(position - 1).getPolitical_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> SportInterestAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sportInterestList);
        sport_interest.setAdapter(SportInterestAdapter);
        sport_interest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedService = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < SportModelArraylist.size(); i++) {
                    if (SportModelArraylist.get(i).getSport_name().contains(SelectedService)) {
                        SportModelArraylistTemp.add(SportModelArraylist.get(i));
                        SportId = SportModelArraylist.get(position - 1).getSport_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> CertificationsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text,CertificationList);
        certifications.setAdapter(CertificationsAdapter);
        certifications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < CertificationModelList.size(); i++) {
                    if (CertificationModelList.get(i).getCertification_name().contains(SelectedCountry)) {
                        CertificationModelListTemp.add(CertificationModelList.get(i));
                        CertificationId = CertificationModelList.get(position - 1).getCertification_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> IncomeRangeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, IncomeRangeList);
        income_range.setAdapter(IncomeRangeAdapter);
        income_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < IncomeModelArrayList.size(); i++) {
                    if (IncomeModelArrayList.get(i).getIncome_value().contains(SelectedCountry)) {
                        IncomeModelArrayListTemp.add(IncomeModelArrayList.get(i));
                        IncomeId = IncomeModelArrayList.get(position - 1).getIncome_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> InvestmentTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, tribal_affiliation_list);
        tribal_affiliation.setAdapter(InvestmentTypeAdapter);
        tribal_affiliation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < TribalModelArrayList.size(); i++) {
                    if (TribalModelArrayList.get(i).getName().contains(SelectedCountry)) {
                        TribalModelArrayListTemp.add(TribalModelArrayList.get(i));
                        Tribal_affiliationId = TribalModelArrayList.get(position - 1).getId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> ProfessionalAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, searchProfessional);
        professional_status.setAdapter(ProfessionalAdapter);
        professional_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < professionalStatusModels.size(); i++) {
                    if (professionalStatusModels.get(i).getProfessionalName().contains(SelectedCountry)) {
                        professionalStatusModelsTemp.add(professionalStatusModels.get(i));
                        SelectedProfessionalStatusId = professionalStatusModels.get(position - 1).getProfessionalId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> Serviceadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, ServiceList);
        service_provider.setAdapter(Serviceadapter);
        service_provider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedService = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < ServiceModelArrayList.size(); i++) {
                    if (ServiceModelArrayList.get(i).getServiceName().contains(SelectedService)) {
                        ServiceModelArrayListTmp.add(ServiceModelArrayList.get(i));
                        SelectedServiceId = ServiceModelArrayList.get(position - 1).getServiceId();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> Countryadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, SearchCountry);
        country_list.setAdapter(Countryadapter);
        country_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                    if (CountryModelArrayList.get(i).getCountryName().contains(SelectedCountry)) {
                        CountryModelArrayListTmp.add(CountryModelArrayList.get(i));
                        SelectedCountryId = CountryModelArrayList.get(position - 1).getCountryId();
                        getCountryWiseState(SelectedCountryId);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> FromCountry = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, nation_list);
        from.setAdapter(FromCountry);
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < NationModelArraylist.size(); i++) {
                    if (NationModelArraylist.get(i).getNation_name().contains(SelectedCountry)) {
                        NationModelArraylistTemp.add(NationModelArraylist.get(i));
                        FromSelectedId = NationModelArraylist.get(position - 1).getNation_id();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> CertifiedAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text, searchCertified);
        business_list.setAdapter(CertifiedAdapter);
        business_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedCountry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < homeCertifiedBusinessModels.size(); i++) {
                    if (homeCertifiedBusinessModels.get(i).getHomeName().contains(SelectedCountry)) {
                        homeCertifiedBusinessModelsTemp.add(homeCertifiedBusinessModels.get(i));
                        SelectedCertifiedId = homeCertifiedBusinessModels.get(position - 1).getHomeId();
                    } } }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> IndusctryFocusAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, industryFocus);
        inductry_focus.setAdapter(IndusctryFocusAdapter);
        inductry_focus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String SelectedIndustry = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < IndystryFocusList.size(); i++) {
                    if (IndystryFocusList.get(i).getIndustryName().contains(SelectedIndustry)) {
                        SelectedIndustryFocusId = IndystryFocusList.get(position - 1).getIndustryId();
                    }
                } }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = "", lname = "", Usercity = "", office = "", Usermobile = "", home = "", Useremployer = "0",
                        key_industry = "0", area_of_expertise = "0";
                if(checkbox_job.isChecked()){
                    str_interestedin_job ="true";
                }else{
                    str_interestedin_job ="false";
                }
                if(checkbox_business.isChecked()){
                    str_interested_inbusiness="true";
                }else{
                    str_interested_inbusiness="false";
                }
                str_current_city=current_city.getText().toString().trim();
                fname = Fname.getText().toString().trim();
                lname = Lname.getText().toString().trim();
                Usercity = city.getText().toString().trim();
                office = phone.getText().toString().trim();
                Usermobile = mobile.getText().toString().trim();
                Useremployer = employer.getText().toString().trim();
                if (fname.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter Group name", Toast.LENGTH_LONG).show();
                } else if (lname.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please Enter Group Description", Toast.LENGTH_LONG).show();
                } else {
                    new UpdateUserProfileAsync(getContext()).execute(UserId, fname, lname, SelectedCountryId, Usercity, office, Usermobile, SelectedCertifiedId, Useremployer, SelectedProfessionalStatusId, SelectedServiceId, SelectedIndustryFocusId, "0", IsProfesionalLiving,  FromSelectedId, SelectedStateId,str_current_city,PoliticalAffiliationId,Tribal_affiliationId,SportId,CertificationId,IncomeId,str_interestedin_job,str_interested_inbusiness,"","","","","","",UserImgPath);
                }
            }
        });
        return view;
    }

    private void getReligiousTribal() {
        TribalModelArrayList.clear();
        String url = (CommonUtils.BASE_URL)+"religious_affiliations";
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
//                                JSONArray json2 = json.getJSONArray("sports_interests");
//                                for (int i = 0; i < json2.length(); i++) {
//                                    JSONObject RegionList = json2.getJSONObject(i);
//                                    JSONObject RegionObj = RegionList.getJSONObject("sports_interests");
//                                    PoliticalAffiliationModel model = new PoliticalAffiliationModel();
//                                    model.setPolitical_id(RegionObj.getString("id"));
//                                    model.setPolitical_name(RegionObj.getString("name"));
//                                    model.setPolitical_status(RegionObj.getString("status"));
//                                    PoliticalAffiliationModelArrayList.add(model);
//                                }
//                                for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });

    }

    private void getCertification() {
        CertificationModelList.clear();
        String url = (CommonUtils.BASE_URL)+"certification";
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
//                                JSONArray json2 = json.getJSONArray("sports_interests");
//                                for (int i = 0; i < json2.length(); i++) {
//                                    JSONObject RegionList = json2.getJSONObject(i);
//                                    JSONObject RegionObj = RegionList.getJSONObject("sports_interests");
//                                    PoliticalAffiliationModel model = new PoliticalAffiliationModel();
//                                    model.setPolitical_id(RegionObj.getString("id"));
//                                    model.setPolitical_name(RegionObj.getString("name"));
//                                    model.setPolitical_status(RegionObj.getString("status"));
//                                    PoliticalAffiliationModelArrayList.add(model);
//                                }
//                                for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });

    }

    private void getIncomeRanges() {

        IncomeModelArrayList.clear();
        String url = (CommonUtils.BASE_URL)+"income_ranges";
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
//                                JSONArray json2 = json.getJSONArray("sports_interests");
//                                for (int i = 0; i < json2.length(); i++) {
//                                    JSONObject RegionList = json2.getJSONObject(i);
//                                    JSONObject RegionObj = RegionList.getJSONObject("sports_interests");
//                                    PoliticalAffiliationModel model = new PoliticalAffiliationModel();
//                                    model.setPolitical_id(RegionObj.getString("id"));
//                                    model.setPolitical_name(RegionObj.getString("name"));
//                                    model.setPolitical_status(RegionObj.getString("status"));
//                                    PoliticalAffiliationModelArrayList.add(model);
//                                }
//                                for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });



    }

    private void getPoliticalAffiliation() {
        PoliticalAffiliationModelArrayList.clear();
        String url = (CommonUtils.BASE_URL)+"political_affiliations";
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
//                                JSONArray json2 = json.getJSONArray("sports_interests");
//                                for (int i = 0; i < json2.length(); i++) {
//                                    JSONObject RegionList = json2.getJSONObject(i);
//                                    JSONObject RegionObj = RegionList.getJSONObject("sports_interests");
//                                    PoliticalAffiliationModel model = new PoliticalAffiliationModel();
//                                    model.setPolitical_id(RegionObj.getString("id"));
//                                    model.setPolitical_name(RegionObj.getString("name"));
//                                    model.setPolitical_status(RegionObj.getString("status"));
//                                    PoliticalAffiliationModelArrayList.add(model);
//                                }
//                                for (int i = 0; i < PoliticalAffiliationModelArrayList.size(); i++) {
//                                    final PoliticalAffiliationModel Items = PoliticalAffiliationModelArrayList.get(i);
//                                    politicalAffiliationList.add(Items.getPolitical_name());
//                                }
                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });

    }

    private void getSportInterest() {
        SportModelArraylist.clear();
        String url = (CommonUtils.BASE_URL)+"sports_interests";
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
                                JSONArray json2 = json.getJSONArray("sports_interests");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("sports_interests");
                                    SportModel model = new SportModel();
                                    model.setSport_id(RegionObj.getString("id"));
                                    model.setSport_name(RegionObj.getString("name"));
                                    model.setSport_status(RegionObj.getString("status"));
                                    SportModelArraylist.add(model);
                                }
                                for (int i = 0; i < SportModelArraylist.size(); i++) {
                                    final SportModel Items = SportModelArraylist.get(i);
                                    sportInterestList.add(Items.getSport_name());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });





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

    public void getUserData(String UserId) {

        progess_load.setVisibility(View.VISIBLE);


        String url = (CommonUtils.BASE_URL) + "viewUserInfo";

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("view_id", UserId)
                .add("user_id", UserId)
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
                        progess_load.setVisibility(View.GONE);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("status");
                            if (success.equalsIgnoreCase("success")) {
                                final RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.noimage)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .priority(Priority.HIGH);
                                String ProfileBase = json.getString("profile_pic_url");
                                JSONObject UserData = json.getJSONObject("userinfo");
                                JSONObject UserUpdate = UserData.getJSONObject("User");
                                JSONObject Region = UserData.getJSONObject("Region");
                                JSONObject UserType = UserData.getJSONObject("UserType");
                                JSONObject Country = UserData.getJSONObject("Country");
                                JSONObject Nation = UserData.getJSONObject("Nation");
                                JSONObject State = UserData.getJSONObject("State");
                                JSONObject Category = UserData.getJSONObject("Category");
                                JSONObject Agency = UserData.getJSONObject("Agency");
                                JSONArray IndustryFocus = json.getJSONArray("key_industry");
                                String ImageURL = ProfileBase + UserUpdate.getString("id") + "/" + UserUpdate.getString("profile_picture");
                                Glide.with(getActivity()).load(ImageURL).apply(options).into(userImg);
                                Fname.setText(UserUpdate.getString("fname").replace("null", ""));
                                Lname.setText(UserUpdate.getString("lname").replace("null", ""));
                                city.setText(UserUpdate.getString("city").replace("null", ""));
                                phone.setText(UserUpdate.getString("office_phone").replace("null", ""));
                                mobile.setText(UserUpdate.getString("mobile_phone").replace("null", ""));
                                employer.setText(UserUpdate.getString("employer").replace("null", ""));
                                bio.setText(UserUpdate.getString("bio").replace("null", ""));
//                                Picasso.with(getActivity()).load(ImageURL).error(R.drawable.ic_person_black_24dp).into(MainActivity.user_profile);
                                IsProfesionalLiving = UserUpdate.getString("african_professional_living").replace("null", "");
                                if (IsProfesionalLiving.equalsIgnoreCase("1")){
                                    LivingYes.setChecked(true);
                                    FromNationLable.setVisibility(View.VISIBLE);
                                    FromNationSpinnerRelative.setVisibility(View.VISIBLE);
                                    IsProfesionalLiving = "1";
                                }
                                else if (IsProfesionalLiving.equalsIgnoreCase("0")){
                                    LivingNo.setChecked(true);
                                    FromNationLable.setVisibility(View.GONE);
                                    FromNationSpinnerRelative.setVisibility(View.GONE);
                                    IsProfesionalLiving = "0";
                                }

                                SelectedCountryId = UserUpdate.getString("country_id").replace("null", "");
                                SelectedStateId = UserUpdate.getString("state_id").replace("null", "");
                                SelectedIndustryFocusId = UserUpdate.getString("key_industry").replace("null", "");
                                SelectedServiceId = UserUpdate.getString("core_expertise").replace("null", "");
                                FromSelectedId = Nation.getString("id").replace("null", "");
                                SelectedProfessionalStatusId = Category.getString("id").replace("null", "");

                               // SelectedCertifiedId = UserUpdate.getString("african_professional_living").replace("null", "");



//                                ArrayList<String> ServiceList;
//                                ArrayList<String> searchState;
//                                ArrayList<String> SearchCountry;
//                                ArrayList<String> searchProfessional;
//                                ArrayList<String> searchCertified;
//                                ArrayList<String> industryFocus;
//                                ArrayList<String> ListState;

                                SelectedCountry = Country.getString("name");
                                for(int i = 0; i < SearchCountry.size();i++) {
                                    if (SearchCountry.get(i).equalsIgnoreCase(SelectedCountry)) {
                                        country_list.setSelection(i);
                                        getCountryWiseState(Country.getString("id"));
                                        SelectedState = State.getString("name");

                                    }
                                }

                                SelectedCertified = Agency.getString("item_title");
                                for(int i = 0; i < searchCertified.size();i++) {
                                    if (searchCertified.get(i).equalsIgnoreCase(SelectedCertified)) {
                                        business_list.setSelection(i);
                                    }
                                }

                                SelectedProfessionaStatus = Category.getString("name");
                                for(int i = 0; i < searchProfessional.size();i++) {
                                    if (searchProfessional.get(i).equalsIgnoreCase(SelectedProfessionaStatus)) {
                                        professional_status.setSelection(i);
                                    }
                                }
                                SelectedService = json.getString("core_expertise");
                                for(int i = 0; i < ServiceList.size();i++) {
                                    if (ServiceList.get(i).equalsIgnoreCase(SelectedService)) {
                                        service_provider.setSelection(i);
                                    }
                                }
                                SelectedIndustryFocus = String.valueOf(IndustryFocus.get(0));
                                for(int i = 0; i < industryFocus.size();i++) {
                                    if (industryFocus.get(i).equalsIgnoreCase(SelectedIndustryFocus)) {
                                        inductry_focus.setSelection(i);
                                    }
                                }

                                SelectedNation = Nation.getString("name");
                                for(int i = 0; i < nation_list.size();i++) {
                                    if (nation_list.get(i).equalsIgnoreCase(SelectedNation)) {
                                        from.setSelection(i);
                                    }
                                }







                            } else {
                                progess_load.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    public void getProfessionalStatus() {

        professionalStatusModels.clear();

        String url = (CommonUtils.BASE_URL) + "getProfessionalStatusDropdown";
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
                                JSONArray json2 = json.getJSONArray("professional_status");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");
                                    ProfessionalStatusModel model = new ProfessionalStatusModel();
                                    model.setProfessionalId(RegionObj.getString("id"));
                                    model.setProfessionalName(RegionObj.getString("name"));
                                    model.setProfessionalStatus(RegionObj.getString("status"));
                                    professionalStatusModels.add(model);
                                }
                                for (int i = 0; i < professionalStatusModels.size(); i++) {
                                    final ProfessionalStatusModel Items = professionalStatusModels.get(i);
                                    searchProfessional.add(Items.getProfessionalName());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }); }
        });

    }public void getCountryWiseState(String CountryId){
        StateModelArrayList.clear();
        String url = (CommonUtils.BASE_URL) + "getStates";
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("country_id", CountryId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
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
                                ListState.add("Select State");
                                JSONArray json2 = json.getJSONArray("states");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject obj = RegionList.getJSONObject("state");
                                    CountryWiseStateModel model = new CountryWiseStateModel();
                                    model.setStateId(obj.getString("id"));
                                    model.setStateName(obj.getString("name"));
                                    model.setStateStatus(obj.getString("status"));
                                    ListState.add(obj.getString("name"));
                                    StateModelArrayList.add(model);
                                }
                                ArrayAdapter<String> statewadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListState);
                                state.setAdapter(statewadapter);
                                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String SelectedState = parent.getItemAtPosition(position).toString();
                                        for(int i = 0; i<StateModelArrayList.size();i++) {
                                            if (StateModelArrayList.get(i).getStateName().contains(SelectedState)) {
                                                try {
                                                    SelectedStateId = StateModelArrayList.get(position - 1).getStateId();
                                                }
                                                catch (Exception e){

                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                ListState.clear();
                                ArrayAdapter<String> statewadapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_text,ListState);
                                state.setAdapter(statewadapter);

                            }
                            for(int j = 0; j < ListState.size();j++) {
                                if (ListState.get(j).equalsIgnoreCase(SelectedState)) {
                                    state.setSelection(j);
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

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        for (int i = 0; i < uriList.size(); i++) {
            Uri selectedImage = uriList.get(i);
            File imageFile = new File(getRealPathFromURI(selectedImage));
            UserImgPath = String.valueOf(imageFile);
            if (UserImgPath.contains("file:")) {
                Glide.with(getActivity()).load(UserImgPath).into(userImg);
            } else if (!UserImgPath.contains("file:")) {
                Glide.with(getActivity()).load("file:"+ UserImgPath).into(userImg);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void getHomeCertifiedBusiness() {
        homeCertifiedBusinessModels.clear();
        String url = (CommonUtils.BASE_URL) + "getHomeCertBusinessDropdown";
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
                                JSONArray json2 = json.getJSONArray("agency");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject certified_business = json2.getJSONObject(i);
//                                JSONObject RegionObj = RegionList.getJSONObject("category");

                                    HomeCertifiedBusinessModel model = new HomeCertifiedBusinessModel();
                                    model.setHomeId(certified_business.getString("id"));
                                    model.setHomeName(certified_business.getString("item_title"));
                                    homeCertifiedBusinessModels.add(model);
                                }
                                for (int i = 0; i < homeCertifiedBusinessModels.size(); i++) {
                                    final HomeCertifiedBusinessModel Items = homeCertifiedBusinessModels.get(i);
                                    searchCertified.add(Items.getHomeName());
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
    public void getServiceDetails() {
        ServiceModelArrayList.clear();
        String url = (CommonUtils.BASE_URL) + "getServices";
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
                                JSONArray json2 = json.getJSONArray("services");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");
                                    ServiceModel model = new ServiceModel();
                                    model.setServiceId(RegionObj.getString("id"));
                                    model.setServiceName(RegionObj.getString("name"));
                                    model.setServiceStatus(RegionObj.getString("status"));
                                    ServiceModelArrayList.add(model);
                                }
                                for (int i = 0; i < ServiceModelArrayList.size(); i++) {
                                    final ServiceModel Items = ServiceModelArrayList.get(i);
                                    ServiceList.add(Items.getServiceName());
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

    public void getCountryDetails() {

        CountryModelArrayList.clear();

        String url = (CommonUtils.BASE_URL) + "getCountry";
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

                                JSONArray json2 = json.getJSONArray("countries");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("country");

                                    CountryModel model = new CountryModel();
                                    model.setCountryId(RegionObj.getString("id"));
                                    model.setCountryName(RegionObj.getString("name"));
                                    model.setCountryStatus(RegionObj.getString("status"));

                                    CountryModelArrayList.add(model);
                                }

                                for (int i = 0; i < CountryModelArrayList.size(); i++) {
                                    final CountryModel Items = CountryModelArrayList.get(i);
                                    SearchCountry.add(Items.getCountryName());
                                    if(Items.getCountryName().equals("United States")){
                                        position=i+1;
                                    }
                                }
                                ArrayAdapter<String> Countryadapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, SearchCountry);
                                country_list.setAdapter(Countryadapter);
                                country_list.setSelection(position);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public void getNation() {
        NationModelArraylist.clear();

        String url = (CommonUtils.BASE_URL) + "getNations";
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

                                JSONArray json2 = json.getJSONArray("nations");

                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("nations");

                                    NationModel model = new NationModel();
                                    model.setNation_id(RegionObj.getString("id"));
                                    model.setNation_name(RegionObj.getString("name"));
                                    model.setNation_status(RegionObj.getString("status"));

                                    NationModelArraylist.add(model);
                                }
                                for (int i = 0; i < NationModelArraylist.size(); i++) {
                                    final NationModel Items = NationModelArraylist.get(i);
                                    nation_list.add(Items.getNation_name());
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


    public void getIndusctryFocus() {
        IndystryFocusList.clear();
        String url = (CommonUtils.BASE_URL) + "getIndustryFocus";
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
                                JSONArray json2 = json.getJSONArray("industry_focus");
                                for (int i = 0; i < json2.length(); i++) {
                                    JSONObject RegionList = json2.getJSONObject(i);
                                    JSONObject RegionObj = RegionList.getJSONObject("category");
                                    IndustryModel model = new IndustryModel();
                                    model.setIndustryId(RegionObj.getString("id"));
                                    model.setIndustryName(RegionObj.getString("name"));
                                    model.setIndustryStatus(RegionObj.getString("status"));
//                                    industryFocus.add(RegionObj.getString("name"));
                                    IndystryFocusList.add(model);
                                }
                                for (int i = 0; i < IndystryFocusList.size(); i++) {
                                    final IndustryModel Items = IndystryFocusList.get(i);
                                    industryFocus.add(Items.getIndustryName());
                                } }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class UpdateUserProfileAsync extends AsyncTask<String, String, JSONObject> {
        JSONObject json;
        public UpdateUserProfileAsync(Context context) {
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", params[0]));
                nameValuePairs.add(new BasicNameValuePair("fname", params[1]));
                nameValuePairs.add(new BasicNameValuePair("lname", params[2]));
                nameValuePairs.add(new BasicNameValuePair("country_id", params[3]));
                nameValuePairs.add(new BasicNameValuePair("city", params[4]));
                nameValuePairs.add(new BasicNameValuePair("office_phone", params[5]));
                nameValuePairs.add(new BasicNameValuePair("mobile_phone", params[6]));
                nameValuePairs.add(new BasicNameValuePair("home_agency", params[7]));
                nameValuePairs.add(new BasicNameValuePair("employer", params[8]));
                nameValuePairs.add(new BasicNameValuePair("professional_status", params[9]));
                nameValuePairs.add(new BasicNameValuePair("core_expertise", params[10]));
                nameValuePairs.add(new BasicNameValuePair("key_industry", params[11]));
                nameValuePairs.add(new BasicNameValuePair("area_of_expertise", params[12]));
                nameValuePairs.add(new BasicNameValuePair("african_professional_living", params[13]));
                nameValuePairs.add(new BasicNameValuePair("current_country_id", params[14]));
                nameValuePairs.add(new BasicNameValuePair("state_id", ""));
                nameValuePairs.add(new BasicNameValuePair("current_city", params[16]));
                nameValuePairs.add(new BasicNameValuePair("political_affiliation_id", params[17]));
                nameValuePairs.add(new BasicNameValuePair("religious_affiliation_id", params[18]));
                nameValuePairs.add(new BasicNameValuePair("sports_interest_id", params[19]));
                nameValuePairs.add(new BasicNameValuePair("education_id", params[20]));
                nameValuePairs.add(new BasicNameValuePair("income_range_id", params[21]));
                nameValuePairs.add(new BasicNameValuePair("interested_in_new_job", params[22]));
                nameValuePairs.add(new BasicNameValuePair("interested_in_business", params[23]));
                nameValuePairs.add(new BasicNameValuePair("high_school_id", params[24]));
                nameValuePairs.add(new BasicNameValuePair("undergrad_school_id", params[25]));
                nameValuePairs.add(new BasicNameValuePair("grade_school_id", params[26]));
                nameValuePairs.add(new BasicNameValuePair("other_high_school", params[27]));
                nameValuePairs.add(new BasicNameValuePair("other_grade_school", params[28]));
                nameValuePairs.add(new BasicNameValuePair("other_undergrade_school", params[29]));
                nameValuePairs.add(new BasicNameValuePair("bio_document_path", params[30]));
                if (!UserImgPath.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("profile_picture", UserImgPath));
                    SharedPreferenceUtils.getInstance(getContext()).setValue(CommonUtils.MEMBER_IMAGE, UserImgPath);
                }
                Log.d("datap", nameValuePairs.toString());
                String Url = (CommonUtils.BASE_URL) + "update_profile";
                json = RestJsonClient.post2(Url, nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progess_load.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progess_load.setVisibility(View.GONE);
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    getUserData(UserId);
                    Fragment  fragment2 = new MyProfileview();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.contentContainer, fragment2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DocumentList.clear();
        if (requestCode == 100) {

            String displayName = null;
            StringBuilder fileName = new StringBuilder();

            if (data != null) {

                if (data.getClipData() != null) {
                    for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                        Uri uri = data.getClipData().getItemAt(index).getUri();
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        String path = myFile.getAbsolutePath();
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            String a =  Environment.getExternalStorageDirectory() + "/" + split[1];
                            DocumentList.add(a);
                            Log.i("Path", "onActivityResult: "+a);
                        }

//                        try {
//                            String mypath = PathUtil.getPath(getActivity(), uri);
//                            DocumentList.add(mypath);
//
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();

                        }
                        uploaded_document.setVisibility(View.VISIBLE);

                        DocPath = path + "/" + displayName;
                        Log.d("Path", myFile.getAbsolutePath());
                        fileName.append(displayName).append("\n");
                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));
                    }
                    uploaded_document.setText(fileName);
                    if (DocumentList.size() == 0) {
                        DocumentList.add(DocPath);
                    }

                } else {

                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();

                    //String result = myFile.getPath();


//                    final String docId = DocumentsContract.getDocumentId(uri);
//                    final String[] split = docId.split(":");
//                    final String type = split[0];

//                    if ("primary".equalsIgnoreCase(type)) {
//                        String a = Environment.getExternalStorageDirectory() + "/" + split[1];
//
//                        Log.i("Path", "onActivityResult: " + a);
//                    }

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();

                    }
                    DocPath = path + "/" + displayName;
                    if (DocPath.contains(".pdf")) {
                        uploaded_document.setVisibility(View.VISIBLE);

                        Log.d("Path", myFile.getAbsolutePath());
                        fileName.append(displayName);
                        uploaded_document.setText(fileName);

                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri));

                        Log.d("fileUri: ", String.valueOf(uri));

                        if (DocumentList.size() == 0) {
                            DocumentList.add(DocPath);
                        }
                    }else {
                        DocPath = "";
                        Toast.makeText(getContext(), "Invalid file type", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}

