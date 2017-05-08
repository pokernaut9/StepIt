package net.devcats.stepit.UI.Home;

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
import android.widget.Toast;

import net.devcats.stepit.Base.BaseFragment;
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

public class HomeFragment extends BaseFragment implements HomeFragmentPresenter.HomeFragmentView {

    private DashboardAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    private HomeFragmentPresenter presenter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new HomeFragmentPresenter();
        presenter.attach(this);
        presenter.present();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public void setupUI() {
        View view = getView();

        if (view != null) {
            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.refresh();
                }
            });

            RecyclerView rvDashboard = (RecyclerView) view.findViewById(R.id.rvDashboard);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvDashboard.setLayoutManager(mLayoutManager);

            List<Post> emptyList = new ArrayList<>();
            adapter = new DashboardAdapter(emptyList);
            rvDashboard.setAdapter(adapter);

            // TODO: Load user profile image here with Glide
            adapter.updateProfilePicture(null); // TODO: Change null to actual image from Glide
        }
    }

    @Override
    public void onStepsReceived(int steps) {
        adapter.updateSteps(steps);
        swipeContainer.setRefreshing(false);
        LogUtils.d("STEPS!!!! " + steps);
    }

    @Override
    public void updatePosts(List<Post> posts) {
        if (adapter != null) {
            adapter.updatePosts(posts);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
    }

    private class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

        void updatePosts(List<Post> posts) {
            mPosts.clear();
            mPosts.addAll(posts);
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
//                imgProfileImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        deviceHandler.removeConnectedDevice();
//                    }
//                });
                tvStepCount = (TextView) itemView.findViewById(R.id.tvStepCount);
            }
        }
    }
}
