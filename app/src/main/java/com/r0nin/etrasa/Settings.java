package com.r0nin.etrasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    protected SharedPreferences sharedpreferences;
    protected Switch switchRememberLogin, switchShowNotiffications, switchDebugMode;
    protected String REMEMBER_LOGIN = "remember_login";
    protected String SHOW_NOTIFICATIONS = "show_notifications";
    protected String DEBUG_MODE = "debug_mode";
    protected Button buttonBackSetting;
    protected String STATE_SWITCH_REMEMBER_LOGIN = "state_remember_login";
    protected String STATE_SWITCH_SHOW_NOTIFICATIONS = "state_show_notifications";
    protected String STATE_SWITCH_DEBUG_MODE = "state_debug_mode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedpreferences = getSharedPreferences(LoginActivity.STORE_LOG,
                Context.MODE_PRIVATE);
        switchRememberLogin = findViewById(R.id.switchRememberLogin);

        switchShowNotiffications = findViewById(R.id.switchShowNotiffications);
        switchDebugMode  = findViewById(R.id.switchDebugMode);
        buttonBackSetting = findViewById(R.id.buttonBackSettings);

        buttonBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        boolean stateLogin = sharedpreferences.getBoolean(STATE_SWITCH_REMEMBER_LOGIN,false);
        switchRememberLogin.setChecked(stateLogin);

        switchRememberLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(REMEMBER_LOGIN, true);
                    editor.putBoolean(STATE_SWITCH_REMEMBER_LOGIN,true);
                    editor.commit();
                }
                else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(STATE_SWITCH_REMEMBER_LOGIN,false);
                    editor.putBoolean(REMEMBER_LOGIN, false);
                    editor.commit();
                }
            }
        });


        boolean stateNotifications = sharedpreferences.getBoolean(STATE_SWITCH_SHOW_NOTIFICATIONS,true);
        switchShowNotiffications.setChecked(stateNotifications);

        switchShowNotiffications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(SHOW_NOTIFICATIONS, true);
                    editor.putBoolean(STATE_SWITCH_SHOW_NOTIFICATIONS,true);
                    editor.commit();
                }
                else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(SHOW_NOTIFICATIONS, false);
                    editor.putBoolean(STATE_SWITCH_SHOW_NOTIFICATIONS,false);
                    editor.commit();
                }
            }
        });
        boolean stateDebug = sharedpreferences.getBoolean(STATE_SWITCH_DEBUG_MODE,false);
        switchDebugMode.setChecked(stateDebug);
        switchDebugMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(DEBUG_MODE, true);
                    editor.putBoolean(STATE_SWITCH_DEBUG_MODE,true);
                    editor.commit();
                }
                else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(DEBUG_MODE, false);
                    editor.putBoolean(STATE_SWITCH_DEBUG_MODE,false);
                    editor.commit();
                }
            }
        });



    }
}
