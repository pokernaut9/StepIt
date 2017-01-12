package net.devcats.stepit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Utils.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ken Juarez on 1/12/17.
 * Activity used to create a new account
 */

public class CreateAccountActivity extends AppCompatActivity {

    private Unbinder unbinder;
    
    @BindView(R.id.txtPhoneNumber)
    EditText txtPhoneNumber;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        unbinder = ButterKnife.bind(this);

        initToolbar();

        txtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // format phone number, remove then re-add text watcher to avoid infinite loop
                txtPhoneNumber.removeTextChangedListener(this);
                txtPhoneNumber.setText(UiUtils.formatPhoneNumber(txtPhoneNumber.getText().toString()));
                int phoneNumberLength = txtPhoneNumber.getText().length();
                if (phoneNumberLength > 0) {
                    txtPhoneNumber.setSelection(txtPhoneNumber.getText().charAt(phoneNumberLength - 1) == ')' ? phoneNumberLength - 1 : phoneNumberLength);
                }
                txtPhoneNumber.addTextChangedListener(this);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserHandler.getInstance().getUser().setId(1);
                UserHandler.getInstance().saveUser(CreateAccountActivity.this);

                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
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