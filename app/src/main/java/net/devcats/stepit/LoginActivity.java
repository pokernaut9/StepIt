package net.devcats.stepit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ken Juarez on 1/12/17.
 * Activity used to login to an existing account
 */

public class LoginActivity extends AppCompatActivity {

    private Unbinder unbinder;
    
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
        unbinder = ButterKnife.bind(this);

        initToolbar();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateForm()) {
                    try {
                        JSONObject object = new JSONObject();

                        object.put("email", txtEmailAddress.getText().toString());
                        object.put("password", txtPassword.getText().toString());

                        new LoginTask().execute(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    // If we fail form validation, should we continue anyways? Since we are in debug?
                    if (BuildConfig.ENVIRONMENT.equals("debug") && txtEmailAddress.getText().toString().equals("ken9")) {

                        Toast.makeText(LoginActivity.this, "DEBUG MODE: Setting userid to 1", Toast.LENGTH_LONG).show();

                        UserModel user = UserHandler.getInstance().getUser();

                        user.setId(1);
                        user.setEmail("pokernaut9@gmail.com");
                        user.setName("Ken Juarez");

                        UserHandler.getInstance().saveUser(LoginActivity.this);

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

    private class LoginTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... objects) {

            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, objects[0].toString());
                Request request = new Request.Builder()
                        .url(BuildConfig.LOGIN_URL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject object = new JSONObject(result);

                if (object.has("error")) {
                    Toast.makeText(LoginActivity.this, object.getString("error"), Toast.LENGTH_LONG).show();
                    throw new Exception(object.getString("error"));
                }

                if (UserHandler.getInstance().parseAndSaveUserFromJSON(LoginActivity.this, object)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_logging_into_account), Toast.LENGTH_LONG).show();
                    throw new Exception(getString(R.string.error_logging_into_account));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, getString(R.string.error_logging_into_account), Toast.LENGTH_LONG).show();
            }
        }
    }
}
