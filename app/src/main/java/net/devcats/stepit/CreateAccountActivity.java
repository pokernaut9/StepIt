package net.devcats.stepit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devcats.stepit.Utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ken Juarez on 1/12/17.
 * Activity used to create a new account
 */

public class CreateAccountActivity extends AppCompatActivity {

    private Unbinder unbinder;

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
        setContentView(R.layout.activity_create_account);
        unbinder = ButterKnife.bind(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {

                    // TODO: create call to server to create account
                    Toast.makeText(CreateAccountActivity.this, "Ah yeah!", Toast.LENGTH_LONG).show();
                } else {

                    // TODO: highlight field with error
                    Toast.makeText(CreateAccountActivity.this, "Error in form...", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private boolean validateForm() {

        if (txtName.getText().length() <= 3) {
            LogUtils.d("name");
            return false;
        } else if (txtEmailAddress.getText().length() < 10 || !txtEmailAddress.getText().toString().contains("@") || !txtEmailAddress.getText().toString().contains(".")) {
            LogUtils.d("email");
            return false;
        } else if (txtPassword.getText().length() < 4 || txtPassword.getText().length() > 25) {
            LogUtils.d("password");
            return false;
        } else if (!txtConfirmPassword.getText().toString().equals(txtPassword.getText().toString())) {
            LogUtils.d("password check");
            return false;
        }

        return true;
    }
}
