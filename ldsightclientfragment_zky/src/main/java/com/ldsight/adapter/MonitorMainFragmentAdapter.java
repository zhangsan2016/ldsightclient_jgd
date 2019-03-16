package com.ldsight.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ldsight.fragment.monitor.NativeMonitorFragment;
import com.ldsight.fragment.monitor.NetMonitorFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldgd on 2018/2/28.
 */

public class MonitorMainFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private static final String[] CONTENT = new String[]{"本地监控", "网络监控", "Albums", "Songs", "Playlists", "Genres"};

    public MonitorMainFragmentAdapter(FragmentManager fm) {
        super(fm);

        NativeMonitorFragment t1 = new NativeMonitorFragment();
        NetMonitorFragment t2 = new NetMonitorFragment();
        fragments.add(t1);
        fragments.add(t2);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
