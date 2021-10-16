package com.makemusiccount.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.isValidEmail;

public class LoginActivity extends AppCompatActivity {

    LinearLayout llOTP, llLogin, llMain, llOTPBox, llVerifyBox, llNotAccount,
            llSignUp, tvSignUp, tvLogin, llPrivacy;

    AlertDialog dialog_signup;

    EditText etUsername, etPassword, etOTP, etSignUpUsername, etSignUpPassword, etSignUpEmail, etSignUpMobile, etSignUpPromoCode;

    TextView tvVerify, tvSkip, tvResend, tvLoginText,tvLoginTextUpper, tvForget;

    String SignUpEmail, SignUpMobile = "", SignUpUsername, SignUpPassword, SignUpPromoCode;

    String userID = "", name = "", email = "", phone = "", image = "", account_type = "";

    String resMessage = "", resCode = "", otp = "", screen = "";

    String username, password, OTP;

    ProgressDialog progressDialog;

    Activity context;

    Global global;

    TextView tvHideShow, tvHideShow1;

    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 107;

    boolean IsAllowAgain = true;

    String page = "";

    LinearLayout llGoogle, llLoading, llGoogle_data;
    LinearLayout llClever;
    TextView txtclever, txtgoogle,tvError;
    LinearLayout llError;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    String strSocialType = "";
    String strSocialId = "";
    String strSocialName = "";
    String strSocialEmail = "";
    String cleverID = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_login);

        context = this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("509516753085-3o2sp0903rs5sbdtfo28lalhkdj5oln8.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        }

        global = new Global(context);

        if (Util.getUserId(context) != null) {
            startActivity(new Intent(context, DashboardActivity.class));
            finish();
        }

        initComp();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            page = bundle.getString("page", "");
        }

        if (page.equals("sign_up")) {
            llLogin.setVisibility(View.GONE);
            llOTP.setVisibility(View.GONE);
            llSignUp.setVisibility(View.VISIBLE);
            tvLoginText.setText("Already a member? Sign in");
            tvLoginTextUpper.setText("Get Started");
            txtclever.setText("Sign up with Clever");
            txtgoogle.setText("Sign up with Gmail");
        } else {
            llOTP.setVisibility(View.GONE);
            llLogin.setVisibility(View.VISIBLE);
            llSignUp.setVisibility(View.GONE);
            tvLoginText.setText("Not a member? Sign up!");
            tvLoginTextUpper.setText("Welcome Back");
            txtclever.setText("Sign in with Clever");
            txtgoogle.setText("Sign in with Gmail");
        }

        tvSkip.setOnClickListener(view -> {
            AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, "Guest");
            startActivity(new Intent(context, DashboardActivity.class));
        });

        tvLogin.setOnClickListener(view -> {
            cleverID = "";
            username = etUsername.getText().toString();
            username = username.replaceAll(" ", "");
            password = etPassword.getText().toString();
            if (username.equals("")) {
                Toast.makeText(context, "Please enter email / username", Toast.LENGTH_SHORT).show();
            } /*else if (!isValidEmail(username)) {
                Toast.makeText(context, "Enter valid email", Toast.LENGTH_SHORT).show();
            }*/ else if (password.equals("")) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new Login().execute();
                } else {
                    global.retryInternet("login");
                }
            }
        });

        tvVerify.setOnClickListener(view -> {
            OTP = etOTP.getText().toString();
            if (global.isNetworkAvailable()) {
                new VerifyOTP().execute();
            } else {
                global.retryInternet("verify_otp");
            }
        });

        tvResend.setOnClickListener(view -> {
            if (global.isNetworkAvailable()) {
                new Resend().execute();
            } else {
                global.retryInternet("resend");
            }
        });

        llNotAccount.setOnClickListener(view -> {
            if (tvLoginText.getText().toString().equals("Not a member? Sign up!")) {
                llLogin.setVisibility(View.GONE);
                llOTP.setVisibility(View.GONE);
                llSignUp.setVisibility(View.VISIBLE);
                tvLoginText.setText("Already a member? Sign in");
                tvLoginTextUpper.setText("Get Started");
                txtclever.setText("Sign up with Clever");
                txtgoogle.setText("Sign up with Gmail");

            } else {
                llOTP.setVisibility(View.GONE);
                llLogin.setVisibility(View.VISIBLE);
                llSignUp.setVisibility(View.GONE);
                tvLoginText.setText("Not a member? Sign up!");
                tvLoginTextUpper.setText("Welcome Back");
                txtclever.setText("Sign in with Clever");
                txtgoogle.setText("Sign in with Gmail");
            }
        });

        tvSignUp.setOnClickListener(view -> {
            cleverID = "";
            SignUpEmail = etSignUpEmail.getText().toString();
            SignUpEmail = SignUpEmail.replaceAll(" ", "");
            SignUpMobile = etSignUpMobile.getText().toString();
            SignUpUsername = etSignUpUsername.getText().toString();
            SignUpPromoCode = etSignUpPromoCode.getText().toString();
            SignUpPassword = etSignUpPassword.getText().toString();

            if (SignUpUsername.equals("")) {
                Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
            }/*else if (SignUpMobile.equals("")) {
                Toast.makeText(context, "Enter mobile no", Toast.LENGTH_SHORT).show();
            } */ else if (SignUpEmail.equals("")) {
                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(SignUpEmail)) {
                Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show();
            } else if (SignUpPassword.equals("")) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
            } else if (SignUpPassword.length() < 4) {
                Toast.makeText(context, "Min password length should be 4 characters", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new Signup().execute();
                } else {
                    global.retryInternet("signup");
                }
            }
        });

        tvForget.setOnClickListener(view -> {
            Intent i = new Intent(context, ForgetActivity.class);
            startActivity(i);
        });

        tvHideShow.setOnClickListener(view -> {
            if (tvHideShow.getText().equals("Hide")) {
                tvHideShow.setText("Show");
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etPassword.setSelection(etPassword.getText().length());
            } else if (tvHideShow.getText().equals("Show")) {
                tvHideShow.setText("Hide");
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        tvHideShow1.setOnClickListener(view -> {
            if (tvHideShow1.getText().equals("Hide")) {
                tvHideShow1.setText("Show");
                etSignUpPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etSignUpPassword.setSelection(etSignUpPassword.getText().length());
            } else if (tvHideShow1.getText().equals("Show")) {
                tvHideShow1.setText("Hide");
                etSignUpPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etSignUpPassword.setSelection(etSignUpPassword.getText().length());
            }
        });

        llPrivacy.setOnClickListener(view -> {
            String url = "https://www.makemusiccount.online/privacy-policy.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        llGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleverID = "";
                if (global.isNetworkAvailable()) {
                    strSocialType = "Google";
                    googlesignIn();
                    llLoading.setVisibility(View.VISIBLE);
                    llGoogle_data.setVisibility(View.GONE);
                } else {
                    global.retryInternet("Google");
                }
            }
        });

        llClever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleverID = "";
                Intent intent = new Intent(context, CleverWebViewActivity.class);
                startActivityForResult(intent, 1001);
            }
        });
    }

    private void googlesignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String personName = acct.getDisplayName(); //rahul patel
                            String personGivenName = acct.getGivenName(); //rahul
                            String personFamilyName = acct.getFamilyName(); //patel
                            String personEmail = acct.getEmail(); //rahulpatel@gmail.com
                            String personId = acct.getId(); //562166521915231156
                            Uri personPhoto = acct.getPhotoUrl(); //link in uri
                            strSocialId = personId;
                            strSocialName = personName;
                            strSocialEmail = personEmail;
                            Log.e("Id", personId);
                            Log.e("Name", personName);
                            Log.e("Email", personEmail);
                            Log.e("Photo", String.valueOf(acct.getPhotoUrl()));
                            Log.e("FamilyName", personFamilyName);
                            Log.e("GivenName", personGivenName);
                            /*dismissProgressDialog();*/
                            llLoading.setVisibility(View.GONE);
                            llGoogle_data.setVisibility(View.VISIBLE);
                            if (global.isNetworkAvailable()) {
                                new SocialLogin().execute();
                            } else {
                                global.retryInternet("SocialLogin");
                            }

                            /*Toast.makeText(context, "Email :- " + personEmail + "\n" + "Name :- " + personName
                                    + "\n" + "PersonId :- " + personId, Toast.LENGTH_SHORT).show();*/

                        } else {
                            llLoading.setVisibility(View.GONE);
                            llGoogle_data.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("login")) {
                    new Login().execute();
                } else if (extraValue.equalsIgnoreCase("verify_otp")) {
                    new VerifyOTP().execute();
                } else if (extraValue.equalsIgnoreCase("resend")) {
                    new Resend().execute();
                } else if (extraValue.equalsIgnoreCase("signup")) {
                    new Signup().execute();
                } else if (extraValue.equalsIgnoreCase("SocialLogin")) {
                    new SocialLogin().execute();
                } else if (extraValue.equalsIgnoreCase("Google")) {
                    googlesignIn();
                }
            }
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                progressDialog.show();
                progressDialog.setMessage("Logging you in...");
                progressDialog.setCancelable(false);
            } catch (ApiException e) {
                llLoading.setVisibility(View.GONE);
                llGoogle_data.setVisibility(View.VISIBLE);
                Log.e(TAG, e.getMessage());
            }
        } else if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra("status").equals("success")) {
                    cleverID = data.getStringExtra("cleverID");
                    if (global.isNetworkAvailable()) {
                        username = "";
                        password = "";
                        new Login().execute();
                    } else {
                        global.retryInternet("login");
                    }
                } else if (data.getStringExtra("status").equals("failed")) {
                    String cleverIDMsg = data.getStringExtra("cleverID");
                    Toast.makeText(context, cleverIDMsg, Toast.LENGTH_SHORT).show();
                }
            }
        } else {

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();
        if (addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
        if (addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return !shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    VideoView videoview;

    private void setBackground() {
        videoview = findViewById(R.id.videoview);
        final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnCompletionListener(mp -> {
            mp.reset();
            videoview.setVideoURI(uri);
            videoview.start();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setBackground();
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        llOTP = findViewById(R.id.llOTP);
        tvError = findViewById(R.id.tvError);
        llError = findViewById(R.id.llError);
        llLogin = findViewById(R.id.llLogin);
        llMain = findViewById(R.id.llMain);
        llSignUp = findViewById(R.id.llSignUp);
        llNotAccount = findViewById(R.id.llNotAccount);
        llOTPBox = findViewById(R.id.llOTPBox);
        llVerifyBox = findViewById(R.id.llVerifyBox);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etOTP = findViewById(R.id.etOTP);
        tvLogin = findViewById(R.id.tvLogin);
        tvVerify = findViewById(R.id.tvVerify);
        tvSkip = findViewById(R.id.tvSkip);
        tvResend = findViewById(R.id.tvResend);
        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpPromoCode = findViewById(R.id.etSignUpPromoCode);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpMobile = findViewById(R.id.etSignUpMobile);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvLoginText = findViewById(R.id.tvLoginText);
        tvLoginTextUpper = findViewById(R.id.tvLoginTextUpper);
        tvForget = findViewById(R.id.tvForget);
        tvHideShow = findViewById(R.id.tvHideShow);
        tvHideShow1 = findViewById(R.id.tvHideShow1);
        llPrivacy = findViewById(R.id.llPrivacy);

        llClever = findViewById(R.id.llClever);
        txtclever = findViewById(R.id.txtclever);

        llGoogle = findViewById(R.id.llgoogle);
        txtgoogle = findViewById(R.id.txtgoogle);
        llLoading = findViewById(R.id.llLoading);
        llGoogle_data = findViewById(R.id.llGoogle_data);
    }

    private void showSignUpPopup(String resMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_signup_error, null);

        alertDialogBuilder.setView(alert_layout);

        dialog_signup = alertDialogBuilder.create();

        dialog_signup.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvError);
        TextView tvTitle1 = alert_layout.findViewById(R.id.tvError1);
        TextView btnCancel = alert_layout.findViewById(R.id.tvContinue);
        tvTitle.setText(resMessage);
        tvTitle1.setOnClickListener(view -> dialog_signup.dismiss());
        btnCancel.setOnClickListener(view -> {
            llNotAccount.callOnClick();
            dialog_signup.dismiss();
        });

        dialog_signup.show();

       /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog_signup.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(460, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
                        IsAllowAgain = false;
                        CRateWhyDialog();
                    }
                    if (IsAllowAgain) {
                        insertDummyContactWrapper();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void CRateWhyDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_new_login_no_permission);
            dialog.setCancelable(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(true);
            TextView txtDialogBottomText = dialog.findViewById(R.id.txtDialogBottomText);
            txtDialogBottomText.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
                finish();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SocialLogin extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_SOCIAL_LOGIN
                    + "&socialID=" + strSocialId
                    + "&socialName=" + strSocialName
                    + "&socialEmail=" + strSocialEmail
                    + "&app_type=" + "Android";
            String strTrim = strLogin.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        otp = jsonObjectList.getString("otp");
                        screen = jsonObjectList.getString("screen");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                userID = jsonObjectList.getString("userID");
                                name = jsonObjectList.getString("name");
                                image = jsonObjectList.getString("image");
                                email = jsonObjectList.getString("email");
                                phone = jsonObjectList.getString("phone");
                                account_type = jsonObjectList.getString("account_type");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                if (screen.equals("home")) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                    Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (screen.equals("otp")) {
                    llLogin.setVisibility(View.GONE);
                    tvSkip.setVisibility(View.GONE);
                    llOTP.setVisibility(View.VISIBLE);
                    slideFromRightToLeft(llOTPBox, llMain);
                    slideFromRightToLeft(llVerifyBox, llMain);
                }
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Login extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_LOGIN + username
                    + "&userpass=" + password
                    + "&cleverID=" + cleverID
                    + "&app_type=" + "Android";
            String strTrim = strLogin.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        otp = jsonObjectList.getString("otp");
                        screen = jsonObjectList.getString("screen");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                userID = jsonObjectList.getString("userID");
                                name = jsonObjectList.getString("name");
                                image = jsonObjectList.getString("image");
                                email = jsonObjectList.getString("email");
                                phone = jsonObjectList.getString("phone");
                                account_type = jsonObjectList.getString("account_type");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                if (screen.equals("home")) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                    Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (screen.equals("otp")) {
                    llLogin.setVisibility(View.GONE);
                    tvSkip.setVisibility(View.GONE);
                    llOTP.setVisibility(View.VISIBLE);
                    slideFromRightToLeft(llOTPBox, llMain);
                    slideFromRightToLeft(llVerifyBox, llMain);
                }
                if(screen.equalsIgnoreCase("subscription"))
                {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                    Intent i = new Intent(context, SubscribePackageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }

            } else {
                llError.setVisibility(View.VISIBLE);
                tvError.setText(resMessage+"");
                llError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        llError.setVisibility(View.GONE);
                    }
                });
              //  Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Signup extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_SIGNUP + SignUpUsername
                    + "&useremail=" + SignUpEmail
                    + "&userpass=" + SignUpPassword
                    + "&userphone=" + SignUpMobile
                    + "&userpromocode=" + SignUpPromoCode
                    + "&app_type=" + "Android";

            String strTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strTrim", strTrim);
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            otp = jsonObjectList.getString("otp");
                            screen = jsonObjectList.getString("screen");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                userID = jsonObjectList.getString("userID");
                                name = jsonObjectList.getString("name");
                                image = jsonObjectList.getString("image");
                                email = jsonObjectList.getString("email");
                                phone = jsonObjectList.getString("phone");
                                account_type = jsonObjectList.getString("account_type");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                if (screen.equals("home")) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                    Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (screen.equals("otp")) {
                    llSignUp.setVisibility(View.GONE);
                    tvSkip.setVisibility(View.GONE);
                    llOTP.setVisibility(View.VISIBLE);
                    slideFromRightToLeft(llOTPBox, llMain);
                    slideFromRightToLeft(llVerifyBox, llMain);
                }
                if(screen.equalsIgnoreCase("subscription"))
                {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                    Intent i = new Intent(context, SubscribePackageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
            else if(resCode.equalsIgnoreCase("2"))
            {
                showSignUpPopup(resMessage);
            }
            else {

                llError.setVisibility(View.VISIBLE);
                tvError.setText(resMessage+"");
                llError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        llError.setVisibility(View.GONE);
                    }
                });
               Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Resend extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_RESEND_OTP + userID
                    + "&useremail=" + email
                    + "&app_type=" + "Android";

            String strTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strTrim", strTrim);
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                Log.e("APIString", APIString);

                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        otp = jsonObjectList.getString("otp");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VerifyOTP extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_VERIFY_OTP + userID
                    + "&useremail=" + email
                    + "&verify_otp=" + OTP
                    + "&app_type=" + "Android";

            String strTrim = strLogin.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                Log.e("APIString", APIString);

                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        account_type = jsonObjectList.getString("account_type");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            if (resCode.equalsIgnoreCase("0")) {
                AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                AppPreference.setPreference(context, AppPersistence.keys.User_Type, account_type);
                Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }
    }

    private void slideFromRightToLeft(View view, View main_layout) {
        TranslateAnimation animate;
        if (view.getHeight() == 0) {
            main_layout.getHeight(); // parent layout
            animate = new TranslateAnimation(main_layout.getWidth() / 2,
                    0, 0, 0);
        } else {
            animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        }
        animate.setDuration(700);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        if (videoview != null) {
            videoview.stopPlayback();
        }
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, DashboardActivity.class));
    }
}