package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SubCategoryActivity;
import com.makemusiccount.android.activity.SuccessActivity;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.PlaySound;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    View view;

    TextView tvQuestion, tvOp1, tvOp2, tvOp3, tvHeading, tvPointText, tvPage, tvNote;

    Activity context;

    Global global;

    String UserId = "", resMessage = "", resCode = "", answer = "";

    ProgressDialog progressDialog;

    String question = "", ans_1 = "", ans_2 = "", ans_3 = "", right_ans = "", questionID = "", note = "";

    ImageView iv1, iv2, iv3;

    private TextView txtProgress;

    private int pStatus = 100;

    private Handler handler = new Handler();

    LinearLayout ll1, ll2, ll3;

    RelativeLayout rlQuize;

    LinearLayout llAnswer, llContinue;

    TextView tvAnswer, tvRightWrong;

    String selectedAns = "";

    public TestFragment() {
        // Required empty public constructor
    }

    ImageView ivHelp;
    TextView tvHelpHint;

    @Override
    public void onResume() {
        super.onResume();
        ivHelp.setVisibility(View.GONE);
        tvHelpHint.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test, container, false);

        context = getActivity();

        global = new Global(context);

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        progressDialog = new ProgressDialog(context);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvHeading = view.findViewById(R.id.tvHeading);
        tvPage = view.findViewById(R.id.tvPage);
        tvNote = view.findViewById(R.id.tvNote);
        tvPointText = view.findViewById(R.id.tvPointText);
        tvOp1 = view.findViewById(R.id.tvOp1);
        tvOp2 = view.findViewById(R.id.tvOp2);
        tvOp3 = view.findViewById(R.id.tvOp3);
        iv1 = view.findViewById(R.id.iv1);
        iv2 = view.findViewById(R.id.iv2);
        iv3 = view.findViewById(R.id.iv3);
        ll1 = view.findViewById(R.id.ll1);
        ll2 = view.findViewById(R.id.ll2);
        ll3 = view.findViewById(R.id.ll3);
        rlQuize = view.findViewById(R.id.rlQuize);
        llAnswer = view.findViewById(R.id.llAnswer);
        llContinue = view.findViewById(R.id.llContinue);
        tvAnswer = view.findViewById(R.id.tvAnswer);
        tvRightWrong = view.findViewById(R.id.tvRightWrong);
        txtProgress = view.findViewById(R.id.txtProgress);

        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);

        rlQuize.setVisibility(View.VISIBLE);
        llAnswer.setVisibility(View.GONE);

        if (global.isNetworkAvailable()) {
            new GetQuestion().execute();
        } else {
            retryInternet("question");
        }

        tvPage.setText("Question");

        TextView tvDate = view.findViewById(R.id.tvDate);
        Util.setDate(tvDate);

        ll1.setOnClickListener(view -> clickOnAnswer(tvOp1, iv1, ll1));

        ll2.setOnClickListener(view -> clickOnAnswer(tvOp2, iv2, ll2));

        ll3.setOnClickListener(view -> clickOnAnswer(tvOp3, iv3, ll3));

        llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupClose();
            }
        });

        return view;
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("question")) {
                    new GetQuestion().execute();
                }
            }
        }
    }

    private void clickOnAnswer(TextView textView, ImageView imageView, LinearLayout ll) {
        TypedValue typedValue=new TypedValue();
        Resources.Theme theme=context.getTheme();
        theme.resolveAttribute(R.attr.new_light,typedValue,true);
        @ColorInt int color =typedValue.data;
        textView.setTextColor(color);
        ll.setBackground(context.getResources().getDrawable(R.drawable.ans__selecte_bg));
        iv1.setEnabled(false);
        iv2.setEnabled(false);
        iv3.setEnabled(false);
        selectedAns = textView.getText().toString();
        if (textView.getText().toString().equals(right_ans)) {
            imageView.setImageResource(R.drawable.right);
            answer = "Yes";
        } else {
            imageView.setImageResource(R.drawable.wrong);
            answer = "No";
        }
        new SubmitAnswer().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SubmitAnswer extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_SONG_QUESTION_ANS + UserId
                    + "&questionID=" + questionID
                    + "&ans=" + answer
                    + "&songsID=" + SubCategoryActivity.SongId
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
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
                rlQuize.setVisibility(View.GONE);
                llAnswer.setVisibility(View.VISIBLE);
                tvAnswer.setText(selectedAns);
                if (answer.equals("Yes")) {
                    tvRightWrong.setText("Right!");
                    TypedValue typedValue=new TypedValue();
                    Resources.Theme theme=context.getTheme();
                    theme.resolveAttribute(R.attr.new_light,typedValue,true);
                    @ColorInt int color =typedValue.data;
                    tvRightWrong.setTextColor(color);
                } else {
                    tvRightWrong.setTextColor(context.getResources().getColor(R.color.red));
                    tvRightWrong.setText("Wrong!");
                }
                //openRightPopup();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void openRightPopup() {
        try {
            if (answer.equals("Yes")) {
                PlaySound.play(context, R.raw.coin);
            } else {
                PlaySound.play(context, R.raw.wrong_answer_sound);
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_right_key_press, null);
            RelativeLayout llMain = alertLayout.findViewById(R.id.llMain);
            TextView tvMsg = alertLayout.findViewById(R.id.tvMsg);
            ImageView ivImage = alertLayout.findViewById(R.id.ivImage);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            tvMsg.setText(resMessage);
            if (answer.equals("Yes")) {
                ivImage.setImageResource(R.drawable.right_key);
            } else {
                ivImage.setImageResource(R.drawable.wron_key);
            }
            final AlertDialog finalAlertDialog = alertDialog;
            llMain.setOnClickListener(v -> {
                finalAlertDialog.dismiss();
                onPopupClose();
            });

            alertDialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            alertDialog.getWindow().setAttributes(lp);

            alertDialog.setOnCancelListener(dialog -> {
                finalAlertDialog.dismiss();
                onPopupClose();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPopupClose() {
        Intent intent = new Intent(context, SuccessActivity.class);
        intent.putExtra("song_id", SubCategoryActivity.SongId);
        startActivity(intent);
        context.finish();
        context.overridePendingTransition(0, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetQuestion extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_SONG_QUESTION + UserId
                    + "&songsID=" + SubCategoryActivity.SongId
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("question_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                    questionID = jsonObjectList.getString("questionID");
                                    question = jsonObjectList.getString("question");
                                    ans_1 = jsonObjectList.getString("ans_1");
                                    ans_2 = jsonObjectList.getString("ans_2");
                                    ans_3 = jsonObjectList.getString("ans_3");
                                    note = jsonObjectList.getString("note");
                                    right_ans = jsonObjectList.getString("right_ans");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                tvQuestion.setText(question);
                tvOp1.setText(ans_1);
                tvOp2.setText(ans_2);
                tvOp3.setText(ans_3);
                tvNote.setText(note);
                new Thread(() -> {
                    while (pStatus >= 0) {
                        handler.post(() -> txtProgress.setText((pStatus / 5) + ""));
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus--;
                    }
                }).start();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}