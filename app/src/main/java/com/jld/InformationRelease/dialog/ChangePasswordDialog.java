package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * Created by boping on 2017/8/8.
 */

public class ChangePasswordDialog extends DialogFragment {

    private Context mContext;
    private SetChangePasswordListen mListen;

    public ChangePasswordDialog(Context context, SetChangePasswordListen listen) {
        mContext = context;
        mListen = listen;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.change_password_dialog, null);
        final EditText et_original_password = (EditText) view.findViewById(R.id.et_original_password);
        final EditText et_new_password = (EditText) view.findViewById(R.id.et_new_password);
        final EditText et_new_password_confirm = (EditText) view.findViewById(R.id.et_new_password_confirm);
        Button confirm = (Button) view.findViewById(R.id.btn_change_password_confirm);
        ImageView close = (ImageView) view.findViewById(R.id.dialog1_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String original_password = et_original_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                String password_confirm = et_new_password_confirm.getText().toString();
                if (TextUtils.isEmpty(original_password)) {
                    ToastUtil.showToast(mContext, getResources().getString(R.string.please_input_original_password), 3000);
                } else if (TextUtils.isEmpty(new_password)) {
                    ToastUtil.showToast(mContext, getResources().getString(R.string.please_input_new_password), 3000);
                } else if (TextUtils.isEmpty(password_confirm)) {
                    ToastUtil.showToast(mContext, getResources().getString(R.string.please_confirm_new_password), 3000);
                } else if (!password_confirm.equals(new_password)) {
                    ToastUtil.showToast(mContext, getResources().getString(R.string.two_password_different), 3000);
                } else
                    mListen.onConfirm(original_password, new_password);
            }
        });
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        return dialog;
    }

    public interface SetChangePasswordListen {
        void onConfirm(String originalPassword, String newPassword);
    }
}
