package com.uclgroupgh.form.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;


public class ListDialogFragment extends DialogFragment {

    private OnListDialogItemSelect listener;
    private String title;
    private String[] list;
    private String componentname;

    public ListDialogFragment() {
    }


    @SuppressLint("ValidFragment")
    public ListDialogFragment(OnListDialogItemSelect listener, String[] list, String title, String componentname) {
        this.listener = listener;
        this.list = list;
        this.title = title;
        this.componentname = componentname;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putString("componentname", componentname);
        setArguments(bundle);


        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setCancelable(false)
                .setItems(list, (dialog, item) -> {
                    listener.onListItemSelected(list[item]);
                    getDialog().dismiss();
                    ListDialogFragment.this.dismiss();
                }).create();
    }

    public interface OnListDialogItemSelect {
        public void onListItemSelected(String selection);
    }

}

