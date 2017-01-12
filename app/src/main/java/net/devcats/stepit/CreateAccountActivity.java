package net.devcats.stepit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.devcats.stepit.Handlers.UserHandler;

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

        txtName.setCompoundDrawables(null, null, null, null);
        txtEmailAddress.setCompoundDrawables(null, null, null, null);
        txtPassword.setCompoundDrawables(null, null, null, null);
        txtConfirmPassword.setCompoundDrawables(null, null, null, null);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("name", txtName.getText().toString());
                        object.put("email", txtEmailAddress.getText().toString());
                        object.put("password", txtPassword.getText().toString());

                        new CreateAccountTask().execute(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    private class CreateAccountTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... objects) {

            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, objects[0].toString());
                Request request = new Request.Builder()
                        .url(BuildConfig.SIGN_UP_URL)
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

                if (UserHandler.getInstance().parseAndSaveUserFromJSON(CreateAccountActivity.this, object)) {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    throw new Exception(getString(R.string.error_creating_account));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CreateAccountActivity.this, getString(R.string.error_creating_account), Toast.LENGTH_LONG).show();
            }
        }
    }
}
