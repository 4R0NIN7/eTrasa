package com.r0nin.etrasa;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class InformationDialog extends Dialog implements android.view.View.OnClickListener {
    private Activity activity;
    protected Dialog dialog;
    private TextView information, title;
    private Button btnReturn;
    protected String circleInfo, circleTitle;

    public InformationDialog(Activity activity, String circleInfo, String circleTitle){
        super(activity);
        this.activity = activity;
        this.circleInfo = circleInfo;
        this.circleTitle = circleTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.information_window_dialog);
        information = findViewById(R.id.information);
        title = findViewById(R.id.titleDialog);

        title.setText(circleTitle);
        information.setText(circleInfo);

        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                dismiss();
                break;
        }
        dismiss();
    }
}
