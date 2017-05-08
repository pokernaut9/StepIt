package net.devcats.stepit.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import net.devcats.stepit.Dagger.Components.AppComponent;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.Utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ken Juarez on 12/17/16.
 * This is the base class used for all fragments
 */

public class BaseFragment extends Fragment {

    private Unbinder unbinder;
    private PushFragmentInterface pushFragmentListener;

    public interface PushFragmentInterface {
        void pushFragment(Fragment fragment);
        void removeFragment(Fragment fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            pushFragmentListener = (PushFragmentInterface) context;
        } catch (ClassCastException e) {
            LogUtils.e("Error: Class does not have PushFragmentInterface implemented.");
        }
    }

    public AppComponent getComponent() {
        return ((StepItApplication) getActivity().getApplication()).getAppComponent();
    }

    public void pushFragment(Fragment fragment) {
        pushFragmentListener.pushFragment(fragment);
    }

    public void removeFragment(Fragment fragment) {
        pushFragmentListener.removeFragment(fragment);
    }
}
