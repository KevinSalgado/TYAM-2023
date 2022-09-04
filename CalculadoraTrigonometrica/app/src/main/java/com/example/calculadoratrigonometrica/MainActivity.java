package com.example.calculadoratrigonometrica;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;




public class MainActivity extends  AppCompatActivity {
    private RadioButton edtSIN, edtCOS, edtTAN, edt45, edt90, edt180;
    //private RadioGroup edtfunciones, edgrados;
    private static String TAG="calculadoratrigonometrica";
    FormViewModel formViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d (TAG, "OnCreate");
        setContentView(R.layout.activity_main); // load the activity_main.xml content

        formViewModel = new ViewModelProvider (this).get (FormViewModel.class);


        edtTAN = findViewById(R.id.radioTAN);
        edtTAN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.TAN = editable.equals(true);
            }
        });

        edtSIN = findViewById(R.id.radioSIN);
        edtSIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.SIN = editable.equals(true);
            }
        });

        edtCOS = findViewById(R.id.radioCOS);
        edtCOS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.COS = editable.equals(true);
            }
        });

        edt45 = findViewById(R.id.radio45);
        edt45.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.ff = editable.equals(true);
            }
        });

        edt90 = findViewById(R.id.radio90);
        edt90.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.nc = editable.equals(true);
            }
        });

        edt180 = findViewById(R.id.radio180);
        edt180.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formViewModel.ohe = editable.equals(true);
            }
        });
    }

    @Override
    protected void onStart () {
        super.onStart ();
        Log.d (TAG, "OnStart");
    }

    @Override
    protected void onResume () {
        super.onResume ();
        Log.d (TAG, "OnResume");

        edtTAN.setChecked(formViewModel.TAN);
        edtCOS.setChecked(formViewModel.COS);
        edtSIN.setChecked(formViewModel.SIN);
        edt45.setChecked(formViewModel.ff);
        edt90.setChecked(formViewModel.nc);
        edt180.setChecked(formViewModel.ohe);
    }

    @Override
    protected void onPause () {
        super.onPause ();
        Log.d (TAG, "OnPause");
    }

    @Override
    protected void onStop () {
        super.onStop ();
        Log.d (TAG, "OnStop");
    }

    @Override
    protected void onRestart () {
        super.onRestart ();
        Log.d (TAG, "OnRestart");
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        Log.d (TAG, "OnDestroy");
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState (outState);
        Log.d (TAG, "OnSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);
        Log.d (TAG, "OnRestoreInstanceState");

    }

    /*@NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return null;
    }*/
}
