package com.kushal.stylista;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HelpPagerAdapter extends FragmentStateAdapter {

    public HelpPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FAQFragment();
        } else {
            return new ContactUsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
