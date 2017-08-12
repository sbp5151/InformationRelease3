package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;

/**
 * Created by boping on 2017/8/8.
 */

public class AlertTextDialog extends DialogFragment {

    private Context mContext;
    private String mDialogContent;
    private OnAlertTextListen mAlertTextListen;

    public AlertTextDialog(Context context, String dialogContent, OnAlertTextListen listen) {
        mContext = context;
        mDialogContent = dialogContent;
        mAlertTextListen = listen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_text_dialog, null);
        TextView dialog_content = (TextView) view.findViewById(R.id.tv_alert_dialog_content);
        dialog_content.setText(mDialogContent);
        Button btn_cancle = (Button) view.findViewById(R.id.btn_change_nike_cancel);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button btn_confirm = (Button) view.findViewById(R.id.btn_change_nike_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertTextListen.onConfirm();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dialog;
    }

    public interface OnAlertTextListen {
        void onConfirm();
    }
}
