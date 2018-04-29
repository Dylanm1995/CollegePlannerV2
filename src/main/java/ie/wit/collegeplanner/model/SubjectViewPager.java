package ie.wit.collegeplanner.model;

import ie.wit.collegeplanner.R;

/**
 * Created by Dylan on 24/04/2018.
 */

public enum SubjectViewPager {

    SUBJECT(R.string.subject, R.layout.view_subject_viewpager),
    VIEWSUBJECTS(R.string.viewSubjects, R.layout.view_viewsubjects_viewpager);

    private int mTitleResId;
    private static int mLayoutResId;

    SubjectViewPager(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        //mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public static int getLayoutResId() {
        return mLayoutResId;
    }

}