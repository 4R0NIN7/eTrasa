package com.r0nin.etrasa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
public class CreatePointDialog extends Dialog implements android.view.View.OnClickListener {
    private Activity activity;
    protected Dialog dialog;
    private EditText editTextPointName, editTextPointRadius, editTextDescription;
    private Button buttonSelectImageCreateWindowDialog, buttonSaveCreateWindowDialog;
    public CreatePointDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View view) {

    }
}
