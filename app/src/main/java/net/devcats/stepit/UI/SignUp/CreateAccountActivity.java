package net.devcats.stepit.UI.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.Api.Responses.CreateUserResponse;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.UI.Login.LoginActivity;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.R;
import net.devcats.stepit.Utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

public class CreateAccountActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Inject
    StepItApi stepItApi;
    @Inject
    UserHandler userHandler;

    @BindView(R.id.txtName)
    EditText txtName;

    @BindView(R.id.txtEmailAddress)
    EditText txtEmailAddress;

    @BindView(R.id.txtPassword)
    EditText txtPassword;

    @BindView(R.id.txtConfirmPassword)
    EditText txtConfirmPassword;

    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    @BindView(R.id.tvLogin)
    TextView tvLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StepItApplication.getAppComponent().inject(this);
        setContentView(R.layout.activity_create_account);
        unbinder = ButterKnife.bind(this);

        txtName.setCompoundDrawables(null, null, null, null);
        txtEmailAddress.setCompoundDrawables(null, null, null, null);
        txtPassword.setCompoundDrawables(null, null, null, null);
        txtConfirmPassword.setCompoundDrawables(null, null, null, null);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    createUser(txtName.getText().toString(), txtEmailAddress.getText().toString(), txtPassword.getText().toString());
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    private void createUser(String name, String email, String password) {
        String encryptedPassword = StringUtils.encrypt(password);

        Call<CreateUserResponse> createUserResponseCall = stepItApi.createUser(name, email, encryptedPassword, "Female", 7, "FitBit", "", 3);

        createUserResponseCall.enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(Call<CreateUserResponse> call, retrofit2.Response<CreateUserResponse> response) {
                CreateUserResponse createUserResponse = response.body();

                if (createUserResponse.getSuccess()) {
                    userHandler.setUser(createUserResponse.getDetails().getUser());
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateAccountActivity.this, getString(R.string.error_logging_into_account), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private boolean validateForm() {

        boolean hasError = false;
        txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        txtEmailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        txtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (txtName.getText().length() <= 3) {
            txtName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

//        if (txtEmailAddress.getText().length() < 10 || !txtEmailAddress.getText().toString().contains("@") || !txtEmailAddress.getText().toString().contains(".")) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmailAddress.getText()).matches()) {
            txtEmailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

        if (txtPassword.getText().length() < 4 || txtPassword.getText().length() > 25) {
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

        if (txtConfirmPassword.getText().length() == 0 || !txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
            txtConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

        return !hasError;
    }
}
