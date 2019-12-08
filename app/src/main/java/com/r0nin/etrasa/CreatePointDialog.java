package com.r0nin.etrasa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CreatePointDialog extends AppCompatDialogFragment {

    private CreatePointDialogListener createPointDialogListener;
    private EditText editTextPointName, editTextPointRadius, editTextDescription;
    private Button buttonSelectImageCreateWindowDialog, buttonSaveCreateWindowDialog;
    private static int numberPoint = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_window_dialog, null);
        builder.setView(view)
                .setTitle(R.string.title_create_dialog)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int radius;
                        String pointName;
                        if(!editTextPointName.getText().toString().isEmpty() && !editTextPointRadius.getText().toString().isEmpty()) {
                            pointName = editTextPointName.getText().toString();
                            radius = Integer.parseInt(editTextPointRadius.getText().toString());
                            createPointDialogListener.applyData(pointName,radius);
                            numberPoint++;
                        }
                        else{
                            pointName = "Point";
                            radius = 100;
                            createPointDialogListener.applyData(numberPoint+" "+pointName,radius);
                            numberPoint++;
                        }



                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        editTextPointName = view.findViewById(R.id.editTextPointName);
        editTextPointRadius = view.findViewById(R.id.editTextPointRadius);

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            createPointDialogListener = (CreatePointDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement listener");
        }

    }


    public interface CreatePointDialogListener{
        void applyData(String pointName, int radius);
    }

}

