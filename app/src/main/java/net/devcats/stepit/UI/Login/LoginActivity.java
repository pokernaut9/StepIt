package net.devcats.stepit.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devcats.stepit.Api.StepItApi;
import net.devcats.stepit.BuildConfig;
import net.devcats.stepit.Api.Responses.LoginResponse;
import net.devcats.stepit.UI.SignUp.CreateAccountActivity;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.MainActivity;
import net.devcats.stepit.Model.User;
import net.devcats.stepit.R;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.Utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Ken Juarez on 1/12/17.
 * Activity used to login to an existing account
 */

public class LoginActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Inject
    StepItApi stepItApi;
    @Inject
    UserHandler userHandler;

    @BindView(R.id.txtEmailAddress)
    EditText txtEmailAddress;
    @BindView(R.id.txtPassword)
    EditText txtPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvSignUp)
    TextView tvSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StepItApplication.getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);

        initToolbar();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateForm()) {
                    login(txtEmailAddress.getText().toString(), txtPassword.getText().toString());
                } else {

                    // If we fail form validation, should we continue anyways? Since we are in debug?
                    if (BuildConfig.ENVIRONMENT.equals("debug") && txtEmailAddress.getText().toString().equals("ken9")) {

                        Toast.makeText(LoginActivity.this, "DEBUG MODE: Setting userid to 1", Toast.LENGTH_LONG).show();

                        User user = userHandler.getUser();

                        user.setId(1);
                        user.setEmail("pokernaut9@gmail.com");
                        user.setName("Ken Juarez");

                        userHandler.saveUser();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login(String email, String password) {

        String encryptedPassword = StringUtils.encrypt(password);

        Call<LoginResponse> loginResponseCall = stepItApi.login(email, encryptedPassword);

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (loginResponse.getLoginDetails().getSuccess()) {
                    userHandler.setUser(loginResponse.getLoginDetails().getUser());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_logging_into_account), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }

    private boolean validateForm() {

        boolean hasError = false;
        txtEmailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmailAddress.getText()).matches()) {
            txtEmailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

        if (txtPassword.getText().length() < 4 || txtPassword.getText().length() > 25) {
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attention, 0);
            hasError = true;
        }

        return !hasError;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
