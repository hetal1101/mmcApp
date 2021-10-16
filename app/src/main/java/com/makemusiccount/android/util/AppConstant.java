package com.makemusiccount.android.util;

/*
 * Created by Gautam on 11-12-2017.
 */

import com.makemusiccount.android.model.TutorialList;
import com.makemusiccount.android.model.TutorialSelectList;

import java.util.ArrayList;
import java.util.List;

public final class AppConstant {

public static String songName="";
public static String songId="";
    public static int  tcount=0;
  public static List<TutorialSelectList> TutorialLists=new ArrayList<>();

    private static final String API_URL = "https://www.makemusiccount.online/mmc20/";

    private static final String API_HOME = "index.php?view=";

    public static final String API_LOGIN = API_URL + API_HOME + "signin&username=";

    public static final String API_SOCIAL_LOGIN = API_URL + API_HOME + "social_login";

    public static final String API_Payment = API_URL + API_HOME + "payment_result";

    public static final String API_FORGET = API_URL + API_HOME + "forget_pass&username=";

    public static final String API_SIGNUP = API_URL + API_HOME + "signup&username=";

    public static final String API_VERIFY_OTP = API_URL + API_HOME + "email_verify&userID=";

    public static final String API_RESEND_OTP = API_URL + API_HOME + "email_otp&userID=";

    public static final String API_SONG_LIST = API_URL + API_HOME + "songs&userID=";
    public static final String API_SONG_NODES = API_URL + API_HOME + "songs_node&userID=";

    public static final String API_Tutorials_Category = API_URL + API_HOME + "tutorials_category&userID=";

    public static final String API_SONG_ALL = API_URL + API_HOME + "songs_all_pagewise&userID=";

    public static final String API_USER_DATA = API_URL + API_HOME + "profile_info&page=get_data&userID=";
    public static final String API_PROFILE_DATA = API_URL + API_HOME + "profile_data&userID=";

    public static final String API_LEADER_LIST = API_URL + API_HOME + "leaderboard&userID=";
    public static final String API_LEADER_LIST_DETAIL = API_URL + API_HOME + "leaderboard_detail&userID=";

    public static final String API_CATEGORY = API_URL + API_HOME + "subjects&userID=";

    public static final String API_Dashboard = API_URL + API_HOME + "dashboard&userID=";

    public static final String API_SUB_CATEGORY = API_URL + API_HOME + "sub_subjects&userID=";
    public static final String API_SUB_TUTORIALS = API_URL + API_HOME + "sub_tutorials&userID=";

    public static final String API_SONG_EQUATION = API_URL + API_HOME + "songs_eq&userID=";

    public static final String API_Tutorial_EQUATION = API_URL + API_HOME + "tutorials_question&tutorialID=";

    public static final String API_SONG_COMPLETE = API_URL + API_HOME + "songs_complete&userID=";

    public static final String API_SONG_QUESTION = API_URL + API_HOME + "song_question&userID=";

    public static final String API_TUTORIALS = API_URL + API_HOME + "tutorials&userID=";

    public static final String API_NOTIFICATION_LIST = API_URL + API_HOME + "notification&userID=";

    public static final String API_GET_PROGRESS = API_URL + API_HOME + "progress&userID=";

    public static final String API_Batches_LIST = API_URL + API_HOME + "batches&userID=";
    public static final String API_AVATAR_LIST = API_URL + API_HOME + "avatar&userID=";
    public static final String API_AVATAR_CHANGE = API_URL + API_HOME + "change_avatar&userID=";
    public static final String API_SHOP_PURCHASE = API_URL + API_HOME + "shop_purchase&userID=";
    public static final String API_SHOP_ITEMS = API_URL + API_HOME + "shop&userID=";

    public static final String API_CHANGE_PASSWORD = API_URL + API_HOME + "change_pass&userID=";

    public static final String API_PACKAGE_LIST = API_URL + API_HOME + "packages&userID=";

    public static final String API_CHECK_PROMOCODE = API_URL + API_HOME + "check_coupon&userID=";

    public static final String API_PACKAGE_History = API_URL + API_HOME + "user_package_history&userID=";

    public static final String API_POINT_HISTORY = API_URL + API_HOME + "point_history&userID=";

    public static final String API_AUTO_PLAY_KEY = API_URL + API_HOME + "songs_autoplay1&userID=";

    public static final String API_TUTORIAL_AUTO_PLAY_KEY = API_URL + API_HOME + "tutorial_autoplay&userID=";

    public static final String API_Song_Record = API_URL + API_HOME + "songs_record";

    public static final String API_SONG_QUESTION_ANS = API_URL + API_HOME + "song_question_ans&userID=";

    public static final String API_SONG_COMPLETE_DATA = API_URL + API_HOME + "songs_complete_data&userID=";

    public static final String API_Tutorial_COMPLETE = API_URL + API_HOME + "tutorials_complete&userID=";

    public static final String API_Tutorial_COMPLETE_DATA = API_URL + API_HOME + "tutorials_complete_data&userID=";

    public static final String API_Meet_The_Founder = API_URL + API_HOME + "meet_founder";

    public static final String API_How_It_Work = API_URL + API_HOME + "how_it_works";

    public static final String API_Help = API_URL + API_HOME + "help&userID=";

    public static final String Help_Video = "https://www.makemusiccount.online/uploads/videos/Blackwell19REV.mp4";

    public static final int NO_NETWORK_REQUEST_CODE = 20;

    public static final String API_TUTORIALS_SELECT = API_URL + API_HOME + "tutorials_intro&userID=";
    public static final String API_INTRO_VIDEO = API_URL + API_HOME + "app_intro";

    public static final String API_RECENT_PLAY_LIST = API_URL + API_HOME + "songs_recent&userID=";

}