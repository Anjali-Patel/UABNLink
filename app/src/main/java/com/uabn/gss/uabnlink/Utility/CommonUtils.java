package com.uabn.gss.uabnlink.Utility;

import com.uabn.gss.uabnlink.model.BusinessdataModel;
import com.uabn.gss.uabnlink.model.CountryModel;
import com.uabn.gss.uabnlink.model.DocumentDataModel;
import com.uabn.gss.uabnlink.model.EventsModel;
import com.uabn.gss.uabnlink.model.GroupDataModel;
import com.uabn.gss.uabnlink.model.MemberDataModel;
import com.uabn.gss.uabnlink.model.PBCR_DataModel;
import com.uabn.gss.uabnlink.model.RegionModel;
import com.uabn.gss.uabnlink.model.UserTypeModel;

import java.util.ArrayList;

public class CommonUtils {
//    public static String BASE_URL = "https://uabnlink.com/apis/";
//    public static String BASE_URL = "http://192.168.1.155/newweb/icom_uabn/index.php/apis/";
    public static String BASE_URL = "http://demo1.geniesoftsystem.com/newweb/icom_uabn/index.php/apis/";
    public static String MEMBER_IMAGE ="image";
    public static String MEMBER_ID ="memberid";
    public static String NAME = "name";
    public static String MEMBEREMAIL ="memberemail";
    public static String FCMTOCKEN = "tocken";
    public static String POSTIMAGEBASE = "image";
    public static String POSTDOCBASE ="doc";
    public static String USERPROFILEBASE ="userdp";
    public static String NOTIFICATIONCOUNT="count";
    public static String NOTIFICATIONCOUNTBGDGE="notificationcount";
    public static ArrayList<RegionModel> REGIONARRAYLIST = new ArrayList<>();
    public static ArrayList<CountryModel> COUNTRYARRAYLIST = new ArrayList<>();
    public static ArrayList<UserTypeModel> USERTYPEARRAYLIST = new ArrayList<>();
    public static ArrayList<MemberDataModel> SEARCHPEOPLE = new ArrayList<>();
    public static ArrayList<EventsModel> SEARCHEVENT = new ArrayList<>();
    public static ArrayList<PBCR_DataModel> SEARCHPBCR = new ArrayList<>();
    public static ArrayList<BusinessdataModel> SEARCHCERTIFIEDBUSINESS = new ArrayList<>();
    public static ArrayList<GroupDataModel> SEARCHGROUP = new ArrayList<>();
    public static ArrayList<DocumentDataModel> SEARCHLIBRARY = new ArrayList<>();
    public static String POSTBASEURL = "postbaseurl";
    public static String PROFILEBASEURL = "profilebaseurl";
    public static String VIDEOBASEURL = "videobaseurl";
    public static String DOCUMENTBASEURL = "documentbaseurl";
    public static String high_school_id="hschool_id";
    public static String other_high_school_id="oh_school_id";
    public static String graduate_school_id="graduate_school_id";
    public static String othergraduate_school_id="othergraduate_school_id";
    public static String undergraduate_school_id="undergraduate_school_id";
    public static String other_undergraduate_school_id="other_undergraduate_school_id";

}
