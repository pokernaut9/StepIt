package net.devcats.stepit.UI.SelectDevice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.R;
import net.devcats.stepit.StepItApplication;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment used to select which type of device we are working with
 */

public class SelectDeviceFragment extends BaseFragment {

    @Inject
    DeviceHandler deviceHandler;
    @Inject
    UserHandler userHandler;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.gvSelectDevice)
    GridView gvSelectDevice;

    public static SelectDeviceFragment newInstance() {
        return new SelectDeviceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StepItApplication.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_divice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFABVisible(false);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userHandler.removeUser();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        gvSelectDevice.setAdapter(new GridViewAdapter());
        gvSelectDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                switch (position) {
                    case 0:
                        // Google Fit
                        deviceHandler.connectDevice(getActivity(), Device.TYPE_GOOGLE_FIT);
                        break;

                    case 1:
                        // FitBit
                        deviceHandler.connectDevice(getActivity(), Device.TYPE_FIT_BIT);
                        break;
                }
            }
        });
    }

    @Override
    public void refresh() {
        // nothing
    }

    @Override
    public void onFabTouched() {
        // nothing
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int i) {
            return mThumbIds[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(getContext());
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.ic_google_fit,
                R.drawable.ic_fitbit_logo
        };
    }
}
