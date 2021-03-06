package net.devcats.stepit.UI.Competition;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
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

import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.Model.User;
import net.devcats.stepit.R;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.Utils.StringUtils;
import net.devcats.stepit.Utils.UiUtils;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


public class CompetitionFragment extends BaseFragment implements CompetitionFragmentPresenter.CompetitionFragmentView {

    private static final String KEY_COMPETITION = "key_competition";

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvCompetition)
    RecyclerView rvCompetition;

    private CompetitionFragmentPresenter presenter;
    private CompetitionAdapter adapter;

    public static CompetitionFragment newInstance(int competitionId) {
        CompetitionFragment competitionFragment = new CompetitionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_COMPETITION, competitionId);
        competitionFragment.setArguments(bundle);
        return competitionFragment;
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

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        presenter = new CompetitionFragmentPresenter(getArguments().getInt(KEY_COMPETITION));
        presenter.attach(this);
        presenter.present();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        presenter.detach();
    }

    @Override
    public void refresh() {
        presenter.refresh();
    }

    @Override
    public void onFabTouched() {
        presenter.addUserToCompetition();
    }

    @Override
    public void setupUI() {
        View view = getView();
        if (view != null) {
            swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.refresh();
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvCompetition.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public void updateCompetition(Competition competition) {
        if (adapter == null) {
            adapter = new CompetitionAdapter(competition);
            rvCompetition.setAdapter(adapter);
        } else if (competition != null) {
            adapter.updateCompetition(competition);
        }

        swipeContainer.setRefreshing(false);
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void setAddNewCompetitionButtonVisibility(boolean visibile) {
        setFABVisible(visibile);
    }

    private class CompetitionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER = 0;
        private static final int OTHER = 1;

        private Competition competition;
        private List<User> users;

        CompetitionAdapter(Competition competition) {
            this.competition = competition;
            this.users = this.competition.getUsers();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_fragment_header, parent, false);
                return new ViewHolderHeader(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_fragment_item, parent, false);
                return new ViewHolderCompetition(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof  ViewHolderHeader) {
                ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
                viewHolderHeader.tvName.setText(competition.getName());

                if (!StringUtils.isEmpty(competition.getDescription())) {
                    viewHolderHeader.tvDescription.setText(competition.getDescription());
                    viewHolderHeader.tvDescription.setVisibility(View.VISIBLE);
                }

                viewHolderHeader.tvDateRange.setText(competition.getDateRange());
            } else {
                ViewHolderCompetition viewHolderCompetition = (ViewHolderCompetition) holder;
                User user = getItem(position);

                viewHolderCompetition.tvPosition.setText(String.valueOf(position));

                viewHolderCompetition.imgProfileImage.setImageBitmap(user.getProfileImage());
                new FetchProfileImageTask(user.getId(), user.getProfilePicture()).execute();

                viewHolderCompetition.tvName.setText(user.getName());

                viewHolderCompetition.tvStepCount.setText(UiUtils.formatNumber(user.getStepCount()));
            }
        }

        @Override
        public int getItemCount() {
            return users.size() + 1;
        }

        User getItem(int position) {
            return users.get(position - 1);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == HEADER)
                return HEADER;
            else
                return OTHER;
        }

        void updateCompetition(Competition competition) {
            this.competition = competition;
            this.users = this.competition.getUsers();
            notifyDataSetChanged();
        }

        void updateUserProfilePicture(int userId, Bitmap bitmap) {
            for (User user : users) {
                if (user.getId() == userId) {
                    user.setProfileImage(bitmap);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        private class ViewHolderHeader extends RecyclerView.ViewHolder {
            private TextView tvName;
            private TextView tvDescription;
            private TextView tvDateRange;

            ViewHolderHeader(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
                tvDateRange = (TextView) itemView.findViewById(R.id.tvDateRange);
            }
        }

        private class ViewHolderCompetition extends RecyclerView.ViewHolder {
            private TextView tvPosition;
            private CircleImageView imgProfileImage;
            private TextView tvName;
            private TextView tvStepCount;

            ViewHolderCompetition(View itemView) {
                super(itemView);
                tvPosition = (TextView) itemView.findViewById(R.id.tvPosition);
                imgProfileImage = (CircleImageView) itemView.findViewById(R.id.imgProfileImage);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvStepCount = (TextView) itemView.findViewById(R.id.tvStepCount);
            }
        }
    }

    private class FetchProfileImageTask extends AsyncTask<Void, Void, Bitmap> {

        private int userId;
        private String path;

        FetchProfileImageTask(int userId, String path) {
            this.userId = userId;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap profilePicture = null;
            Context context = getContext();
            try {
                if (context != null) {
                    profilePicture = Glide
                            .with(context)
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
            adapter.updateUserProfilePicture(userId, bitmap);
        }
    }

}
