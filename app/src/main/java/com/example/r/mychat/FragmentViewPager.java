package com.example.r.mychat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by r on 1/9/2018.
 */

public class FragmentViewPager extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        TextView tv = (TextView) v.findViewById(R.id.title);
        tv.setText(getArguments().getString("text"));

        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        imageView.setBackgroundResource(getArguments().getInt("img"));

        return v;
    }

    public static FragmentViewPager newInstance(String text, int image) {

        FragmentViewPager f = new FragmentViewPager();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("img", image);

        f.setArguments(b);

        return f;
    }
}
