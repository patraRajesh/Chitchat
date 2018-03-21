package com.example.r.mychat;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by r on 1/8/2018.
 */

class SectionpageAdapteer extends FragmentPagerAdapter {

    public SectionpageAdapteer(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){


                    case 0:
                        Lastestfriend ff =new Lastestfriend();
                        return ff;
                    case 1:

                        GroupChat ffr =new GroupChat();
                        return ffr;
                    case 2:
                        News news=new News();
                        return news;

                    case 3:

                        FriendReq ffr2 =new FriendReq();
                        return ffr2;
                    default:
                        return null;
                }
            }



    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "friends";

            case 3:
                return "Requests";
            default:
                return "";
        }
    }
}
