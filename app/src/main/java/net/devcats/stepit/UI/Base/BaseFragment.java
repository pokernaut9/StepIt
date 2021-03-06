package net.devcats.stepit.UI.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import net.devcats.stepit.Interfaces.RefreshCallbacks;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.Utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements RefreshCallbacks {

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
        ((MainActivity) getActivity()).registerRefreshCallback(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        ((MainActivity) getActivity()).unregisterRefreshCallback(this);
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

}
