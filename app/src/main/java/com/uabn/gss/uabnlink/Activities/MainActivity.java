package com.uabn.gss.uabnlink.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uabn.gss.uabnlink.R;
import com.uabn.gss.uabnlink.Utility.CommonUtils;
import com.uabn.gss.uabnlink.Utility.SharedPreferenceUtils;
import com.uabn.gss.uabnlink.fragment.AboutUsFragment;
import com.uabn.gss.uabnlink.fragment.AddEvent;
import com.uabn.gss.uabnlink.fragment.AddGroupFragment;
import com.uabn.gss.uabnlink.fragment.AddPBCRFragment;
import com.uabn.gss.uabnlink.fragment.AllConnections;
import com.uabn.gss.uabnlink.fragment.CertifiedBusinessFragment;
import com.uabn.gss.uabnlink.fragment.ContactUsFragment;
import com.uabn.gss.uabnlink.fragment.DocumentResourcesFragment;
import com.uabn.gss.uabnlink.fragment.EventFragment;
import com.uabn.gss.uabnlink.fragment.FindContactsFragment;
import com.uabn.gss.uabnlink.fragment.GroupFragment;
import com.uabn.gss.uabnlink.fragment.HomeFragment;
import com.uabn.gss.uabnlink.fragment.InviteContact;
import com.uabn.gss.uabnlink.fragment.InviteFragment;
import com.uabn.gss.uabnlink.fragment.MembersFragment;
import com.uabn.gss.uabnlink.fragment.MyConnections;
import com.uabn.gss.uabnlink.fragment.MyProfileview;
import com.uabn.gss.uabnlink.fragment.NotificationsFragment;
import com.uabn.gss.uabnlink.fragment.PBCR_Edit;
import com.uabn.gss.uabnlink.fragment.PBCRfragment;
import com.uabn.gss.uabnlink.fragment.PostFragment;
import com.uabn.gss.uabnlink.fragment.RequestFragment;
import com.uabn.gss.uabnlink.fragment.UserProfile;
import com.roughike.bottombar.BottomBar;
import com.uabn.gss.uabnlink.model.BusinessdataModel;
import com.uabn.gss.uabnlink.model.DocumentDataModel;
import com.uabn.gss.uabnlink.model.EventsModel;
import com.uabn.gss.uabnlink.model.GroupDataModel;
import com.uabn.gss.uabnlink.model.IndustryModel;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,HomeFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener, MembersFragment.OnFragmentInteractionListener,
        RequestFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener,
        InviteFragment.OnFragmentInteractionListener, PBCRfragment.PbcrFragmentInterface,
        PBCRfragment.OnFragmentInteractionListener, CertifiedBusinessFragment.OnFragmentInteractionListener,
        EventFragment.OnFragmentInteractionListener, GroupFragment.OnFragmentInteractionListener,
        DocumentResourcesFragment.OnFragmentInteractionListener, ContactUsFragment.OnFragmentInteractionListener,
        AddGroupFragment.OnFragmentInteractionListener, FindContactsFragment.OnFragmentInteractionListener,
        InviteContact.OnFragmentInteractionListener, MyConnections.OnFragmentInteractionListener,
        AllConnections.OnFragmentInteractionListener, UserProfile.OnFragmentInteractionListener,
        MyProfileview.OnFragmentInteractionListener, AddEvent.OnFragmentInteractionListener,
        PBCR_Edit.OnFragmentInteractionListener, AboutUsFragment.OnFragmentInteractionListener
{


    //implements View.OnClickListener
    //private MaterialSheetFab materialSheetFab;


    Fragment fragment;
    Toolbar toolbar;
    int id;
    boolean flag,f_assitance,f_groups,f_events,f_business,f_contact,f_document,f_about_us,profile_view;
    String NotificationType;
    String count = "0";
    String notificationCount;
    BottomNavigationView navigation;
    BottomNavigationMenuView menuView;
    BottomNavigationItemView itemView, notificcationcountView;
    View notificationBadge;
    TextView textView;

//    LinearLayout SignOutLinear;
    SharedPreferenceUtils preferances;

    public static String profile_pic_url,EVENT_IMAGE,user_update_image,user_update_doc,pbcr_image_path,pbcr_doc_path,cb_image_url,group_image,group_bg_image, library_img_url;
    static String user_id;

    public static ImageView user_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("NotificationType")){
            NotificationType = getIntent().getStringExtra("NotificationType");
            if (NotificationType.equalsIgnoreCase("friend_req")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new RequestFragment());
                transaction.addToBackStack(null);
                toolbar.setTitle("Friend Requests");
                transaction.commit();
            }
            else if(NotificationType.equalsIgnoreCase("pbcr")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new CertifiedBusinessFragment());
                transaction.addToBackStack(null);
                setVisibiltyOnOff(false);
                toolbar.setTitle("Registered Business");
                transaction.commit();
            }
            else if(NotificationType.equalsIgnoreCase("walltowall")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new MyConnections());
                transaction.addToBackStack(null);
                toolbar.setTitle("My connections");
                transaction.commit();
            }
        }
        navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        itemView = (BottomNavigationItemView) menuView.getChildAt(4);
        notificcationcountView = (BottomNavigationItemView) menuView.getChildAt(3);
        preferances= SharedPreferenceUtils.getInstance(this);
        user_id=preferances.getStringValue(CommonUtils.MEMBER_ID,"");
        count = preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNT,"");
        notificationCount =  preferances.getStringValue(CommonUtils.NOTIFICATIONCOUNTBGDGE,"");


        if (!count.equalsIgnoreCase("")){

            notificationBadge = LayoutInflater.from(this).inflate(R.layout.chat_notification_count, navigation, false);
            textView = notificationBadge.findViewById(R.id.notification_badge);
            textView.setText(" "+count+" ");
            itemView.addView(notificationBadge);

            }
        else{

        }

        if (!notificationCount.equalsIgnoreCase("") && !notificationCount.equalsIgnoreCase("0")){

            notificationBadge = LayoutInflater.from(this).inflate(R.layout.chat_notification_count, navigation, false);
            textView = notificationBadge.findViewById(R.id.notification_badge);
            textView.setText(" "+notificationCount+" ");
            notificcationcountView.addView(notificationBadge);

        }
        else{

        }


        checkPermissionForSDK();
        //setupFab();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("UABN");

//        SignOutLinear = (LinearLayout) findViewById(R.id.SignOutLinear);


//        loadFragment(new HomeFragment());



        //setUpUi();


//        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
//        fab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(Gravity.LEFT);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        TextView userName = (TextView)header.findViewById(R.id.UserName);
        TextView userEmail = (TextView)header.findViewById(R.id.UserEmail);
        user_profile= (ImageView) header.findViewById(R.id.UserImage);
       // LinearLayout SignOutLinear = (LinearLayout) navigationView.findViewById(R.id.UserImage);

        userName.setText(preferances.getStringValue(CommonUtils.NAME,""));
        userEmail.setText(preferances.getStringValue(CommonUtils.MEMBEREMAIL,""));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.noimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        if (!preferances.getStringValue(CommonUtils.MEMBER_IMAGE,"").equalsIgnoreCase("")) {
            Glide.with(MainActivity.this).load(preferances.getStringValue(CommonUtils.MEMBER_IMAGE, "")).apply(options).into(user_profile);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentContainer, new HomeFragment());
        transaction.commit();

        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new MyProfileview());
                transaction.addToBackStack(null);
                setVisibiltyOnOff(false);
                toolbar.setTitle("Profile");
                drawer.closeDrawers();
                transaction.commit();
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new MyProfileview());
                transaction.addToBackStack(null);
                setVisibiltyOnOff(false);
                toolbar.setTitle("Profile");
                drawer.closeDrawers();
                transaction.commit();
            }
        });


    }

    Menu menu;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(flag) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            flag=false;

        }/* else if(profile_view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            profile_view = true;
        }*/
        else if(f_assitance) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_assitance = false;
        } else if(f_business) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_business = false;
        } else if(f_events) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_events = false;
        } else if(f_groups) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_groups = false;
        } else if(f_document) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_document = false;
        }  else if(f_about_us) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_about_us = false;
        }
        else if(f_contact) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
            navigation.setSelectedItemId(R.id.tab_home);
            f_contact = false;
        } else if (id == R.id.home || navigation.getSelectedItemId() == R.id.tab_home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create();
            builder.setMessage("Want to Exit ?");
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                }
            });
            builder.show();
        } else {
            if (id != R.id.home || navigation.getSelectedItemId() != R.id.tab_home) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new HomeFragment());
                transaction.addToBackStack(null);
                toolbar.setTitle("UABN Home");
                transaction.commit();
                navigation.setSelectedItemId(R.id.tab_home);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        setVisibiltyOnOff(true);
        return true;
    }



    public void setVisibiltyOnOff(boolean v)
    {
        menu.findItem(R.id.search).setVisible(v);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            DialogueSearch search = new DialogueSearch();
            search.show(getSupportFragmentManager(), DialogueSearch.class.getSimpleName());

        }
        else if (id == R.id.chat) {

            Intent i = new Intent(this,ChatList.class);

            startActivity(i);

//            navigation.setSelectedItemId(R.id.tab_home);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id = item.getItemId();

        if (id == R.id.home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new HomeFragment());
            transaction.addToBackStack(null);
            toolbar.setTitle("UABN Home");
            transaction.commit();
        }
        else if (id == R.id.pbcr) {
            f_assitance=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new PBCRfragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Assistance Request");
            transaction.commit();

        } else if (id == R.id.groups) {
            f_groups=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new GroupFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Groups");
            transaction.commit();

        } else if (id == R.id.events) {
            f_events=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, new EventFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Events");
            transaction.commit();

        } else if (id == R.id.business) {
            f_business=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new CertifiedBusinessFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Registered Business");
            transaction.commit();

        } else if (id == R.id.contact) {
            f_contact=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new ContactUsFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Contact Us");
            transaction.commit();


        } else if (id == R.id.about_us) {
            f_about_us = true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new AboutUsFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("About Us");
            transaction.commit();
        }
        else if (id == R.id.document) {
            f_document=true;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, new DocumentResourcesFragment());
            transaction.addToBackStack(null);
            setVisibiltyOnOff(false);
            toolbar.setTitle("Documents & Resources");
            transaction.commit();

        } else if (id == R.id.logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create();
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferances.setValue(CommonUtils.MEMBER_ID, "");
                    preferances.setValue(CommonUtils.NAME, "");
                    preferances.setValue(CommonUtils.MEMBEREMAIL, "");
                    Intent i = new Intent(MainActivity.this,Login.class);
                    startActivity(i);                }
            });


            builder.show();



        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFabButtonFindInteraction() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new FindContactsFragment());
        transaction.addToBackStack(null);
        toolbar.setTitle("Find Contacts");
        transaction.commit();
    }

    @Override
    public void onFabButtonInviteInteraction() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new InviteFragment());
        transaction.addToBackStack(null);
        toolbar.setTitle("Invite Connections");
        transaction.commit();
    }

    @Override
    public void addPbcr() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new AddPBCRFragment());
        transaction.addToBackStack(null);
        toolbar.setTitle("Add Assistance Request");
        transaction.commit();
    }

    @Override
    public void editpbcr(String PBCRId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, PBCR_Edit.newInstance(PBCRId));
        transaction.addToBackStack(null);
        toolbar.setTitle("Edit Assistance Request");
        transaction.commit();
    }

    @Override
    public void onFabButtonAddEventInteraction(String title, String EventId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, AddEvent.newInstance(title, EventId));
        transaction.addToBackStack(null);
        toolbar.setTitle(title);
        transaction.commit();
    }



    @Override
    public void onChangeTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void addGroup(String title, String GroupId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer,AddGroupFragment.newInstance(title, GroupId));
        transaction.addToBackStack(null);
        toolbar.setTitle(title);
        transaction.commit();
    }

    @Override
    public void OnFindContact() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new FindContactsFragment());
        transaction.addToBackStack(null);
        toolbar.setTitle("Find Contacts");
        transaction.commit();
    }
    @Override
    public void OnInviteContact() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new InviteContact());
        transaction.addToBackStack(null);
        toolbar.setTitle("Invite Contacts");
        transaction.commit();
    }

    @Override
    public void OnMyConnections() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new MyConnections());
        transaction.addToBackStack(null);
        toolbar.setTitle("My connections");
        transaction.commit();
    }

    @Override
    public void OnAllconnections() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new AllConnections());
        transaction.addToBackStack(null);
        toolbar.setTitle("All Connections");
        transaction.commit();
    }

    @Override
    public void editProfile() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new UserProfile());
        transaction.addToBackStack(null);
        toolbar.setTitle("Edit Profile");
        transaction.commit();
    }

    @Override
    public void ShowEvents() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new EventFragment());
        transaction.addToBackStack(null);
        setVisibiltyOnOff(false);
        toolbar.setTitle("Events");
        transaction.commit();
    }

    @Override
    public void ReloadGroups() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, new GroupFragment());
        transaction.addToBackStack(null);
        setVisibiltyOnOff(false);
        toolbar.setTitle("Groups");
        transaction.commit();
    }

    @Override
    public void AddPost() {
        flag= true;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_up);
        transaction.replace(R.id.contentContainer, new PostFragment());
        transaction.addToBackStack(null);
        setVisibiltyOnOff(false);
        toolbar.setTitle("Post");
        transaction.commit();
    }


    public static class DialogueSearch extends DialogFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogCustom);


        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.search_layout, null);

            ImageView close = (ImageView) dialogView.findViewById(R.id.search_close_btn);
            final ProgressBar progess_load = (ProgressBar) dialogView.findViewById(R.id.progess_load);
            final EditText searchtext=dialogView.findViewById(R.id.searchtext);
            Button dialogButton = (Button) dialogView.findViewById(R.id.send);

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CommonUtils.SEARCHPEOPLE.clear();
                    CommonUtils.SEARCHLIBRARY.clear();
                    CommonUtils.SEARCHCERTIFIEDBUSINESS.clear();
                    CommonUtils.SEARCHEVENT.clear();
                    CommonUtils.SEARCHPBCR.clear();
                    CommonUtils.SEARCHGROUP.clear();

                        String str_search = searchtext.getText().toString().trim();

                        if(str_search.equalsIgnoreCase("")){
                            Toast.makeText(getContext(), "Please enter search criteria", Toast.LENGTH_LONG).show();

                        }else {
                            progess_load.setVisibility(View.VISIBLE);
//                    BusinessDataModelArrayList.clear();
                            String url = (CommonUtils.BASE_URL) + "searchAll";
                            OkHttpClient client = new OkHttpClient();
                            RequestBody formBody = new FormBody.Builder()
                                    .add("user_id", user_id)
                                    .add("search_item", str_search)
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
                                                    profile_pic_url = json.getString("profile_pic_url");
                                                    EVENT_IMAGE = json.getString("EVENT_IMAGE");
                                                    user_update_image = json.getString("user_update_image");
                                                    user_update_doc = json.getString("user_update_doc");
                                                    pbcr_image_path = json.getString("pbcr_image_path");
                                                    pbcr_doc_path = json.getString("pbcr_doc_path");
                                                    cb_image_url = json.getString("cb_image_url");
                                                    group_image = json.getString("group_image");
                                                    group_bg_image = json.getString("group_bg_image");
                                                    library_img_url = json.getString("library_img_url");
                                                    JSONObject search_result=json.getJSONObject("search_result");
                                                    JSONArray people=search_result.getJSONArray("people");
                                                    JSONArray certified_business =search_result.getJSONArray("certified_business");
                                                    JSONArray group =search_result.getJSONArray("group");
                                                    JSONArray library =search_result.getJSONArray("library");
                                                    JSONArray event= search_result.getJSONArray("event");
                                                    JSONArray pbcr=search_result.getJSONArray("pbcr");
                                                    for (int i = 0; i < people.length(); i++) {
                                                        JSONObject Member = people.getJSONObject(i);
                                                        JSONObject User = Member.getJSONObject("User");
                                                        JSONObject Country = Member.getJSONObject("Country");
                                                        MemberDataModel model = new MemberDataModel();
                                                        model.setMemberName(User.getString("name").replace("null", ""));
                                                        model.setMemberAddress(Country.getString("name").replace("null", ""));
                                                        model.setMemberId(User.getString("id").replace("null", ""));
                                                        model.setMemberImage(profile_pic_url + User.getString("id")+ "/" +User.getString("profile_picture").replace("null", "").replace(" ", "%20"));
                                                        CommonUtils.SEARCHPEOPLE.add(model);
                                                    }

//                                               JSONObject Mar = pbcr.getJSONObject("Mar");
                                                    for (int i = 0; i < pbcr.length(); i++) {
                                                        JSONObject pbcr_detail = pbcr.getJSONObject(i);
                                                        JSONObject PBCRInfo = pbcr_detail.getJSONObject("Mar");
                                                        PBCR_DataModel pbcr_dataModel = new PBCR_DataModel();

                                                        pbcr_dataModel.setPBCRComments(PBCRInfo.getString("no_comments").replace("null", ""));
                                                        Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(PBCRInfo.getString("created_on"));
                                                        String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                                        pbcr_dataModel.setPBCRDate(CommentPostDate);
                                                        pbcr_dataModel.setPbcr_userId_(PBCRInfo.getString("user_id").replace("null", ""));
                                                        String ImageURL = pbcr_image_path + PBCRInfo.getString("id") + "/" + PBCRInfo.getString("image");
                                                        pbcr_dataModel.setPBCRImage(ImageURL);
                                                        pbcr_dataModel.setPBCRService(PBCRInfo.getString("mar_reason").replace("null", ""));
                                                        pbcr_dataModel.setPBCRTitle(PBCRInfo.getString("item_desc").replace("null", ""));
                                                        pbcr_dataModel.setPBCR_id(PBCRInfo.getString("id").replace("null", ""));
                                                        CommonUtils.SEARCHPBCR.add(pbcr_dataModel);
                                                    }
                                                    for (int i = 0; i < group.length(); i++) {
                                                        JSONObject group_detail = group.getJSONObject(i);
                                                        JSONObject Group = group_detail.getJSONObject("Group");
                                                        JSONObject User = group_detail.getJSONObject("User");
                                                        JSONObject GroupType = group_detail.getJSONObject("GroupType");
                                                        GroupDataModel model = new GroupDataModel();
                                                        model.setGroupId(Group.getString("id"));
                                                        model.setGroupCreatorUserId(User.getString("id"));
                                                        model.setGroupName(Group.getString("title").replace("null", ""));
                                                        model.setGroupType(GroupType.getString("type_title").replace("null", ""));
                                                        model.setGroupDate(Group.getString("created_date").replace("null", ""));
                                                        model.setGroupBy(User.getString("name").replace("null", ""));
                                                        model.setGroupViews(Group.getString("no_views").replace("null", ""));
                                                        model.setGroupComments(Group.getString("no_comments").replace("null", ""));

                                                        if (Group.has("isjoined")){
                                                            model.setIsJoined(Group.getString("isjoined").replace("null", ""));
                                                        }
                                                        else{
                                                            model.setIsJoined("0");
                                                        }

                                                        if (Group.has("islike")){
                                                            model.setIsGroupLiked(Group.getString("islike").replace("null", ""));
                                                        }
                                                        else{
                                                            model.setIsGroupLiked("0");
                                                        }

                                                        String IconUrl = group_image + Group.getString("id") +"/"+ Group.getString("image");
                                                        model.setGroupIcon(IconUrl);
                                                        CommonUtils.SEARCHGROUP.add(model);
                                                    }
                                                    for (int i = 0; i < event.length(); i++) {
                                                        JSONObject EventDetails = event.getJSONObject(i);
                                                        JSONObject Event = EventDetails.getJSONObject("Event");
                                                        JSONObject EventsType = EventDetails.getJSONObject("EventsType");
                                                        EventsModel model = new EventsModel();
                                                        model.setEvent_id(Event.getString("id"));
                                                        if (!Event.getString("image").equalsIgnoreCase("null")){
                                                            model.setEventImage(EVENT_IMAGE +Event.getString("id") +"/"+ Event.getString("image"));
                                                        }
                                                        else{
                                                            model.setEventImage("");
                                                        }
                                                        model.setTitle(Event.getString("event_title"));
                                                        model.setType(EventsType.getString("title"));
                                                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Event.getString("created_date"));
                                                        String newString = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(date); // 25-03-2019
                                                        model.setDate(newString);
                                                        model.setWhen(Event.getString("start_date"));
                                                        model.setWhere(Event.getString("city"));
                                                        model.setWebsite("");
                                                        model.setEventGuestList(Event.getString("show_guests_list"));
                                                        model.setEventOccurance("");
                                                        CommonUtils.SEARCHEVENT.add(model);
                                                    }

                                                    for (int i = 0; i < certified_business.length(); i++) {
                                                        JSONObject Business = certified_business.getJSONObject(i);
                                                        JSONObject Certified = Business.getJSONObject("Agency");
                                                        JSONObject Country = Business.getJSONObject("Country");

                                                        BusinessdataModel model = new BusinessdataModel();
                                                        model.setBusinesName(Certified.getString("item_title").replace("null", ""));
                                                        Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Certified.getString("created_on"));
                                                        String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                                        model.setBusinesDate(CommentPostDate);
                                                        model.setBusinesCountry(Country.getString("name").replace("null", ""));
                                                        model.setBusinesImage(cb_image_url + Certified.getString("id") + "/" + Certified.getString("image"));
                                                        model.setBusinesServices(Certified.getString("key_industryText").replace("null", ""));
                                                        model.setCb_id(Certified.getString("id").replace("null", ""));
                                                        model.setBusinesAddress(Certified.getString("main_office_address").replace("null", ""));
                                                        model.setBusinesComment(Certified.getString("no_comments").replace("null", ""));
                                                        CommonUtils.SEARCHCERTIFIEDBUSINESS.add(model);
                                                    }


                                                    for (int i = 0; i < library.length(); i++) {
                                                        JSONObject LiabraryJson = library.getJSONObject(i);

                                                        JSONObject LiabraryDetails = LiabraryJson.getJSONObject("Knowledgebase");


                                                        DocumentDataModel model = new DocumentDataModel();
                                                        model.setDocumentName(LiabraryDetails.getString("title").replace("null", ""));
                                                        Date Commentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(LiabraryDetails.getString("created_date"));
                                                        String CommentPostDate = new SimpleDateFormat("MMM dd yyyy hh:mm a").format(Commentdate); // 25-03-2019
                                                        model.setDocumentPosted(CommentPostDate);

                                                        model.setDocemntDetails(LiabraryDetails.getString("description").replace("null", ""));
                                                        model.setId(LiabraryDetails.getString("id").replace("null", ""));
                                                        model.setCat_id(LiabraryDetails.getString("cat_id").replace("null", ""));

                                                        model.setDocumentIcon(library_img_url + LiabraryDetails.getString("id") + "/" + LiabraryDetails.getString("media_name"));
                                                        model.setMedia_type(LiabraryDetails.getString("media_type").replace("null", ""));
                                                        model.setWebsite(LiabraryDetails.getString("website").replace("null",""));

                                                        CommonUtils.SEARCHLIBRARY.add(model);
                                                    }

                                                    Intent intent= new Intent(getContext(),MasterSearchActivity.class);
                                                    startActivity(intent);
                                                    dismiss();


                                                } else {
                                                    progess_load.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                                     dismiss();

                                                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            } catch (java.text.ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });

                        }

                    }
            });


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            return dialogView;
        }


    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.tab_home:
                    toolbar.setTitle("UABN Home");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    setVisibiltyOnOff(true);
                    return true;
                case R.id.tab_members:
                    toolbar.setTitle("Members");
                    fragment = new MembersFragment();
                    loadFragment(fragment);
                    setVisibiltyOnOff(false);
                    return true;
                case R.id.tab_request:
                    toolbar.setTitle("Friend Requests");
                    fragment = new RequestFragment();
                    loadFragment(fragment);
                    setVisibiltyOnOff(false);
                    return true;
                case R.id.tab_notify:
                    preferances.setValue(CommonUtils.NOTIFICATIONCOUNTBGDGE, "");
                    notificcationcountView.removeView(notificationBadge);
                    toolbar.setTitle("Notifications");
                    fragment = new NotificationsFragment();
                    loadFragment(fragment);
                    setVisibiltyOnOff(false);
                    return true;
                case R.id.tab_chat:
//                    tab_of__chat=true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigation.setSelectedItemId(R.id.tab_home);
                            preferances.setValue(CommonUtils.NOTIFICATIONCOUNT, "");
                            itemView.removeView(notificationBadge);
                            Intent i = new Intent(MainActivity.this,ChatList.class);
                            startActivity(i);
                        }
                    }, 1000);


                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private void checkPermissionForSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int Permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            Log.e("LogPermission", Permission1 + "/m");
            int Permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);
            Log.e("LogPermission", Permission2 + "/m");
            int Permission3 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            Log.e("LogPermission", Permission3 + "/m");
            int Permission4 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            Log.e("LogPermission", Permission4 + "/m");
            int Permission5 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            Log.e("LogPermission", Permission5 + "/m");
            int Permission6 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission6 + "/m");
            int Permission7 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.e("LogPermission", Permission7 + "/m");
            ArrayList<String> list = new ArrayList<>();

            if (Permission1 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.CAMERA);
            }
            if (Permission2 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
            }
            if (Permission3 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (Permission4 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (Permission5 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (Permission6 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (Permission7 != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (list.size() > 0) {
                ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), Constants.MULTI_PERMISSION);
            }

        }
    }



}
