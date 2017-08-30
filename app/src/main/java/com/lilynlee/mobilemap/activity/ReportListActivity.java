package com.lilynlee.mobilemap.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lilynlee.mobilemap.R;
import com.lilynlee.mobilemap.view.ViewPagerIndicator;
import com.lilynlee.mobilemap.adapter.ReportPageAdapter;
import com.lilynlee.mobilemap.fragment.NoReportFragment;
import com.lilynlee.mobilemap.fragment.ReportedFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportListActivity extends FragmentActivity{

    private ViewPager mViewPager;

    private ViewPagerIndicator mIndicator;
    private List<String> mTitles = Arrays.asList("已上传","未上传");
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlist_layout);

        initView();

        initViewData();

        mIndicator.setVisibleTabCount(2);
        mIndicator.setTabItemTitles(mTitles);

        /*ViewPager设置适配器*/
        mViewPager.setAdapter(new ReportPageAdapter(getSupportFragmentManager(),mFragments));
        mIndicator.setViewPager(mViewPager,0);


    }

    private void initViewData() {
        ReportedFragment reportedFragment = new ReportedFragment();
        NoReportFragment noReportFragement = new NoReportFragment();

        mFragments.add(noReportFragement);
        mFragments.add(reportedFragment);
    }

    private void initView() {
        /*ViewPager部分*/
        mViewPager = (ViewPager) findViewById(R.id.reportlist_viewpager);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.reportlist_indicator);

    }
}
