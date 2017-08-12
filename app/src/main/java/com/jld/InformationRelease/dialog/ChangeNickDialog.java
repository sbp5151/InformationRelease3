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

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * Created by boping on 2017/8/8.
 */

public class ChangeNickDialog extends DialogFragment {

    private Context mContext;
    private SetChangeNickListen mListen;
    private String mNike;

    public ChangeNickDialog(Context context, SetChangeNickListen listen, String nike) {
        mContext = context;
        mListen = listen;
        mNike = nike;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);

        View view = LayoutInflater.from(mContext).inflate(R.layout.change_nick_dialog, null);
        final EditText setName = (EditText) view.findViewById(R.id.et_input_name);
        setName.setText(mNike);
        Button confirm = (Button) view.findViewById(R.id.btn_change_nike_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    mListen.onConfirm(name);
                    dialog.dismiss();
                } else
                    ToastUtil.showToast(mContext, getResources().getString(R.string.please_input_nick), 3000);
            }
        });
//        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        return dialog;
    }

    public interface SetChangeNickListen {
        void onConfirm(String name);
    }
}
