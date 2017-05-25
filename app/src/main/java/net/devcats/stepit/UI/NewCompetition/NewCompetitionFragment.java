package net.devcats.stepit.UI.NewCompetition;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import net.devcats.stepit.Model.Competition;
import net.devcats.stepit.R;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.Utils.DateUtils;
import net.devcats.stepit.Utils.StringUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

public class NewCompetitionFragment extends BaseFragment implements NewCompetitionFragmentPresenter.NewCompetitionView {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.spinSize)
    Spinner spinSize;
    @BindView(R.id.etStartDate)
    EditText etStartDate;
    @BindView(R.id.etEndDate)
    EditText etEndDate;
    @BindView(R.id.btnCreate)
    Button btnCreate;

    private NewCompetitionFragmentPresenter presenter;
    private Calendar calStartDate;
    private Calendar calEndDate;
    private Competition competition;

    public static NewCompetitionFragment newInstance() {
        return new NewCompetitionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calStartDate = Calendar.getInstance();
        calEndDate = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_competition, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calStartDate.set(Calendar.YEAR, year);
                        calStartDate.set(Calendar.MONTH, month);
                        calStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        etStartDate.setText(DateUtils.formatDate(new Date(calStartDate.getTimeInMillis())));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calEndDate.set(Calendar.YEAR, year);
                        calEndDate.set(Calendar.MONTH, month);
                        calEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        etEndDate.setText(DateUtils.formatDate(new Date(calEndDate.getTimeInMillis())));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyInfo()) {
                    presenter.createNewCompetition(competition);
                } else {
                    // Display some sort of error
                }
            }
        });

        presenter = new NewCompetitionFragmentPresenter();
        presenter.attach(this);
        presenter.present();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detach();
    }

    private boolean verifyInfo() {

        competition = new Competition();
        competition.setName(etName.getText().toString());
        competition.setDescription(etDescription.getText().toString());

        competition.setSize(10); // TODO: FIX THIS

        competition.setStartDate(new Date(calStartDate.getTimeInMillis()));
        competition.setEndDate(new Date(calEndDate.getTimeInMillis()));

        return !StringUtils.isEmpty(competition.getName())
                && !StringUtils.isEmpty(competition.getDescription())
                && competition.getSize() > 0
                && (competition.getStartDate() != null)
                && (competition.getEndDate() != null);
    }

    @Override
    public void onFabTouched() {

    }

    @Override
    public void setAddNewCompetitionButtonVisibility(boolean visible) {
        setFABVisible(visible);
    }

    @Override
    public void refresh() {
        presenter.refresh();
    }
}
