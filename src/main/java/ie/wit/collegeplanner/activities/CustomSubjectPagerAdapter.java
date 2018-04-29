package ie.wit.collegeplanner.activities;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ie.wit.collegeplanner.model.SubjectViewPager;

public class CustomSubjectPagerAdapter extends PagerAdapter {

    private Context mContext;

    public CustomSubjectPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        SubjectViewPager subjectViewPager = SubjectViewPager.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(SubjectViewPager.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return SubjectViewPager.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SubjectViewPager customPagerEnum = SubjectViewPager.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

}