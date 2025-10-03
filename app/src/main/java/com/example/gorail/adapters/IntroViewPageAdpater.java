package com.example.gorail.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.gorail.R;

import java.util.List;

public class IntroViewPageAdpater extends PagerAdapter {

    Context mContext;
    List<ScreenItem> mlistScreen;

    public IntroViewPageAdpater(List<ScreenItem> mlistScreen, Context mContext) {
        this.mlistScreen = mlistScreen;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);
        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.introTitle);
        TextView description = layoutScreen.findViewById(R.id.intro_desciption);

        title.setText(mlistScreen.get(position).getTitle());
        description.setText(mlistScreen.get(position).getDescription());
        imgSlide.setImageResource(mlistScreen.get(position).getScreenImage());

        container.addView(layoutScreen);

        return layoutScreen;



    }

    @Override
    public int getCount() {
        return mlistScreen.size();
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
