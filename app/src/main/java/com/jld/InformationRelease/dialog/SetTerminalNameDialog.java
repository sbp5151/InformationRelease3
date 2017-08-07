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
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/26 15:32
 */
public class SetTerminalNameDialog extends DialogFragment {
    private Context mContext;
    private SetTerminalNameListen mListen;
    public SetTerminalNameDialog(Context context, SetTerminalNameListen listen) {
        mContext = context;
        mListen = listen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);

        View view = LayoutInflater.from(mContext).inflate(R.layout.set_name_dialog, null);
        final EditText setName = (EditText) view.findViewById(R.id.dialog1_content);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
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
                String name = setName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    mListen.onConfirm(name);
                    dialog.dismiss();
                } else
                    ToastUtil.showToast(mContext, getResources().getString(R.string.input_name), 3000);
            }
        });
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        dialog.setContentView(view);
        return dialog;
    }

    public interface SetTerminalNameListen{
        void onConfirm(String name);
    }
}
