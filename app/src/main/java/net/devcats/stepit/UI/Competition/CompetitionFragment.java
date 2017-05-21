package net.devcats.stepit.UI.Competition;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.devcats.stepit.R;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.UI.Home.HomeFragment;

public class CompetitionFragment extends BaseFragment {

    public static CompetitionFragment newInstance() {
        return new CompetitionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.competition_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
