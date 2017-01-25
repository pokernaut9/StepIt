package net.devcats.stepit.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Model.Post;
import net.devcats.stepit.R;
import net.devcats.stepit.Utils.LogUtils;
import net.devcats.stepit.Utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment that is used as a dashboard once user is logged in and has a selected device.
 */

public class HomeFragment extends BaseFragment implements DeviceHandler.DeviceResponseListener {

    private Device device;
    private DashboardAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    public interface UpdateUsernameListener {
        void setUsername(String username);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceHandler deviceHandler = DeviceHandler.getInstance();
        deviceHandler.setDeviceResponseListener(this);

        device = deviceHandler.getDevice();
        device.requestSteps();

        UserHandler userHandler = UserHandler.getInstance();

        UpdateUsernameListener usernameListener = (UpdateUsernameListener) getActivity();
        usernameListener.setUsername(userHandler.getUser().getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                device.requestSteps();
            }
        });

        RecyclerView rvDashboard = (RecyclerView) view.findViewById(R.id.rvDashboard);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvDashboard.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        List<Post> temp = new ArrayList<>(5);

        ///////////////// TODO: TEST CODE /////////////////
        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setTitle("TITLE " + i);

            temp.add(post);
        }
        ///////////////////////////////////////////////////

        adapter = new DashboardAdapter(temp);
        rvDashboard.setAdapter(adapter);

        fetchProfileImage();
//        if (device != null && device.getType() == Device.TYPE_FIT_BIT) {
//            tvToken.setText(((FitBitDevice) DeviceHandler.getInstance().getDevice()).getToken());
//        } else if (device != null && device.getType() == Device.TYPE_GOOGLE_FIT) {
//            tvToken.setText("GOOGLE FIT!!!");
//        }
//
//        btnDisconnectDevice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DeviceHandler.getInstance().removeConnectedDevice(getActivity());
//            }
//        });
//
//        btnGetSteps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DeviceHandler.getInstance().requestSteps();
//            }
//        });
//
//        btnClearPreferences.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private void fetchProfileImage() {
        // TODO: Call "void updateProfilePicture(Drawable drawable)" once image is returned from Glide
        adapter.updateProfilePicture(null); // TODO: Change null to actual image resource
    }

    @Override
    public void onStepsReceived(int steps) {
        adapter.updateSteps(steps);
        swipeContainer.setRefreshing(false);
        LogUtils.d("STEPS!!!! " + steps);
    }

    public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER = 0;
        private static final int OTHER = 1;

        private int stepCount;
        private Drawable profileImage;

        private List<Post> mPosts;

        DashboardAdapter(List<Post> posts) {
            mPosts = posts;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_fragment_home, viewGroup, false);
                return new ViewHolderHeader(v);
            } else if (viewType == OTHER){
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_post, viewGroup, false);
                return new ViewHolderComments(v);
            } else
                throw new RuntimeException("Could not inflate layout");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ViewHolderComments) {
                ((ViewHolderComments) holder).tvTitle.setText(mPosts.get(position).getTitle());
            } else if (holder instanceof ViewHolderHeader) {
                ViewHolderHeader holderHeader = (ViewHolderHeader) holder;

                // Set profile image
                if (profileImage != null) {
                    holderHeader.imgProfileImage.setImageDrawable(profileImage);
                }

                // Set step count
                holderHeader.tvStepCount.setText(String.format(getString(R.string.step_count), UiUtils.formatNumber(stepCount)));

            } else {
                LogUtils.e("no instance of view holder found");
            }
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == HEADER)
                return HEADER;
            else
                return OTHER;
        }

        void updateProfilePicture(Drawable drawable) {
            // TODO: Be able to receive image data from Glide, and update image
            profileImage = drawable;
            notifyDataSetChanged();
        }

        void updateSteps(int steps) {
            stepCount = steps;
            notifyDataSetChanged();
        }

        private class ViewHolderComments extends RecyclerView.ViewHolder {
            private TextView tvTitle;

            ViewHolderComments(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            }
        }

        private class ViewHolderHeader extends RecyclerView.ViewHolder {

            private CircleImageView imgProfileImage;
            private TextView tvStepCount;

            ViewHolderHeader(View itemView){
                super(itemView);

                imgProfileImage = (CircleImageView) itemView.findViewById(R.id.imgProfileImage);
                imgProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeviceHandler.getInstance().removeConnectedDevice(getActivity());
                    }
                });
                tvStepCount = (TextView) itemView.findViewById(R.id.tvStepCount);
            }
        }
    }
}
