package net.devcats.stepit.UI.Home;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;

import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.R;
import net.devcats.stepit.Utils.LogUtils;
import net.devcats.stepit.Utils.StringUtils;
import net.devcats.stepit.Utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

        List<Competition> emptyList = new ArrayList<>();
        adapter = new DashboardAdapter(emptyList);
        rvDashboard.setAdapter(adapter);

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
    public void onCompetitionsReceived(List<Competition> competitions) {
        adapter.updateCompetitions(competitions);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void setName(String name) {
        if (!StringUtils.isEmpty(name)) {
            adapter.updateName(name);
        }
    }

    @Override
    public void onStepsReceived(int steps) {
        adapter.updateSteps(steps);
        swipeContainer.setRefreshing(false);
        LogUtils.d("STEPS!!!! " + steps);
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateProfilePicture(String profilePicturePath) {
        new FetchProfileImageTask().execute(profilePicturePath);
    }

    private class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER = 0;
        private static final int OTHER = 1;

        private int stepCount;
        private String name;
        private Bitmap profilePicture;
        private List<Competition> mCompetitions;

        DashboardAdapter(List<Competition> competitions) {
            mCompetitions = competitions;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_fragment_home, viewGroup, false);
                return new ViewHolderHeader(v);
            } else if (viewType == OTHER){
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_competition, viewGroup, false);
                return new ViewHolderCompetitions(v);
            } else
                throw new RuntimeException("Could not inflate layout");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ViewHolderCompetitions) {

                Competition competition = getItem(position);

                ViewHolderCompetitions holderCompetitions = (ViewHolderCompetitions) holder;
                holderCompetitions.tvTitle.setText(competition.getName());
            } else if (holder instanceof ViewHolderHeader) {
                ViewHolderHeader holderHeader = (ViewHolderHeader) holder;

                // Set profile image
                if (profilePicture != null) {
                    holderHeader.imgProfileImage.setImageBitmap(profilePicture);
                }

                // Set name
                if (!StringUtils.isEmpty(name)) {
                    holderHeader.tvName.setText(name);
                }

                // Set step count
                holderHeader.tvStepCount.setText(String.format(getString(R.string.step_count), UiUtils.formatNumber(stepCount)));

            } else {
                LogUtils.e("no instance of view holder found");
            }
        }

        @Override
        public int getItemCount() {
            return mCompetitions.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == HEADER)
                return HEADER;
            else
                return OTHER;
        }

        private Competition getItem(int position) {
            return mCompetitions.get(position - 1);
        }

        void updateProfilePicture(Bitmap bitmap) {
            profilePicture = bitmap;
            notifyDataSetChanged();
        }

        void updateSteps(int steps) {
            stepCount = steps;
            notifyDataSetChanged();
        }

        void updateCompetitions(List<Competition> competitions) {
            mCompetitions.clear();
            mCompetitions.addAll(competitions);
            notifyDataSetChanged();
        }

        public void updateName(String name) {
            this.name = name;
        }

        private class ViewHolderCompetitions extends RecyclerView.ViewHolder {
            private TextView tvTitle;

            ViewHolderCompetitions(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            }
        }

        private class ViewHolderHeader extends RecyclerView.ViewHolder {

            private CircleImageView imgProfileImage;
            private TextView tvName;
            private TextView tvStepCount;

            ViewHolderHeader(View itemView){
                super(itemView);

                imgProfileImage = (CircleImageView) itemView.findViewById(R.id.imgProfileImage);
                imgProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.removeConnectedDevice();
                    }
                });
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvStepCount = (TextView) itemView.findViewById(R.id.tvStepCount);
            }
        }
    }

    private class FetchProfileImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap profilePicture = null;

            try {
                for (String path : params) {
                    profilePicture = Glide
                            .with(getActivity())
                            .load(path)
                            .asBitmap()
                            .into(100, 100)
                            .get();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return profilePicture;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            adapter.updateProfilePicture(bitmap);
        }
    }
}
