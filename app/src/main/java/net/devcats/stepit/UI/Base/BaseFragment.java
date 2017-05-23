package net.devcats.stepit.UI.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import net.devcats.stepit.Dagger.Components.AppComponent;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.Utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ken Juarez on 12/17/16.
 * This is the base class used for all fragments
 */

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;
    private PushFragmentInterface pushFragmentListener;
    private FABControls fabControls;

    public interface PushFragmentInterface {
        void pushFragment(Fragment fragment);
        void removeFragment(Fragment fragment);
    }

    public interface FABControls {
        void setFABVisible(boolean visible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            fabControls = (FABControls) context;
        } catch (ClassCastException e) {
            LogUtils.e("Error: Class does not have PushFragmentInterface implemented.");
        }
    }

    public void setFABVisible(boolean visisble) {
        fabControls.setFABVisible(visisble);
    }

    public abstract void onFabTouched();

    public void pushFragment(Fragment fragment) {
        pushFragmentListener.pushFragment(fragment);
    }

    public void removeFragment(Fragment fragment) {
        pushFragmentListener.removeFragment(fragment);
    }
}
