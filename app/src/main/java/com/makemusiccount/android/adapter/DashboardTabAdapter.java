package com.makemusiccount.android.adapter;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.makemusiccount.android.fragment.LeaderBoardFragment;
import com.makemusiccount.android.fragment.LoginFragment;
import com.makemusiccount.android.fragment.RecentlyPlayListFragment;
import com.makemusiccount.android.fragment.RecordSongList;
import com.makemusiccount.android.fragment.Shop;
import com.makemusiccount.android.fragment.SubjectFragment;
import com.makemusiccount.android.fragment.Subscrption_new;
import com.makemusiccount.android.util.Util;

public class DashboardTabAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    Activity activity;

    public DashboardTabAdapter(Activity dashboardActivity, FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        activity=dashboardActivity;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:{
                 if(Util.getUserId(activity.getApplicationContext())==null)
                         {
                            return new LoginFragment();
                         }
                    else
                        {
                            return new LeaderBoardFragment();
                        }
                }
            case 1:
                return new SubjectFragment();
            case 2:
                if(Util.getUserId(activity.getApplicationContext())==null)
                {
                    return new LoginFragment();
                }
                else
                {
                    return new RecentlyPlayListFragment();
                }
        //RecentlyPlayListFragment
            //case 3:
              //  return new Subscrption_new();
            case  3:
                if(Util.getUserId(activity.getApplicationContext())==null)
                {
                    return new LoginFragment();
                }
                else
                {
                    return new Shop();
                }
            case 4:
                return new RecordSongList();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
