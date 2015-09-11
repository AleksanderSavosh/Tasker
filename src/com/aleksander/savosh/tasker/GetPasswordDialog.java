package com.aleksander.savosh.tasker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.aleksander.savosh.tasker.util.StringUtil;

public class GetPasswordDialog extends DialogFragment {

    public GetPasswordDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText editText = new EditText(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String password = editText.getText().toString();
                    if(!StringUtil.isEmpty(password)) {
                        Intent intent = new Intent(NoticeActivity.BROADCAST_ACTION);
                        intent.putExtra(NoticeActivity.PARAM_PASSWORD, password);
                        getActivity().sendBroadcast(intent);
                    }
                    dialog.dismiss();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            })
            .setView(editText);
        return builder.create();
    }
}