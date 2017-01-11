package net.devcats.stepit.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.R;

import butterknife.BindView;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment used to create a StepIt account
 */

public class CreateAccountFragment extends BaseFragment {

    @BindView(R.id.txtPhoneNumber)
    EditText txtPhoneNumber;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                txtPhoneNumber.setText(formatPhoneNumber(txtPhoneNumber.getText().toString()));
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
                UserHandler.getInstance().saveUser(getContext());

                pushFragment(SelectDeviceFragment.newInstance());
                removeFragment(CreateAccountFragment.this);
            }
        });

    }

    public static String formatPhoneNumber(String s) {
        String formattedPhoneNumber = "";
        if (!TextUtils.isEmpty(s)) {
            boolean hasCountryCode = hasCountryCode(s);
            String digitsOnly = extractDigits(s);
            if (hasCountryCode) {
                digitsOnly = digitsOnly.substring(1);
            }
            if (digitsOnly.length() > 0) {
                formattedPhoneNumber = "+1 ";
                if (digitsOnly.length() <= 3) {
                    formattedPhoneNumber += String.format("(%s)", digitsOnly);
                } else if (digitsOnly.length() <= 6) {
                    formattedPhoneNumber += String.format("(%s) %s", digitsOnly.substring(0, 3), digitsOnly.substring(3));
                } else {
                    formattedPhoneNumber += String.format("(%s) %s-%s", digitsOnly.substring(0, 3), digitsOnly.substring(3, 6), digitsOnly.substring(6));
                }
            }
        }
        return formattedPhoneNumber;
    }

    public static String extractDigits(String s) {
        return !TextUtils.isEmpty(s) ? s.replaceAll("\\D+","") : "";
    }

    public static boolean hasCountryCode(String s) {
        return !TextUtils.isEmpty(s) && s.startsWith("+1");
    }

}
