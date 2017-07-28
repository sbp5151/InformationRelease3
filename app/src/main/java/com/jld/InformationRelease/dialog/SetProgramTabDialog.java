package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.ToastUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/7 17:27
 */
public class SetProgramTabDialog extends DialogFragment {

    private Context mContext;
    OnProgramTabListen mListen;

    public SetProgramTabDialog(Context context, OnProgramTabListen listen) {
        mContext = context;
        mListen = listen;
    }
    public interface OnProgramTabListen {
        void onSetTab(String tab);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.set_name_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.dialog1_title);
        ImageView close = (ImageView) view.findViewById(R.id.dialog1_close);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
        final EditText content = (EditText) view.findViewById(R.id.dialog1_content);
        view.findViewById(R.id.dialog1_confirm);
        title.setText(getString(R.string.setTab_dialog_title));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (mListen != null)
                    mListen.onSetTab(getString(R.string.default_program_tab));
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置节目名称
                if (TextUtils.isEmpty(content.getText().toString())) {
                    ToastUtil.showToast(mContext, getString(R.string.please_input_tab), 3000);
                } else {
                    if (mListen != null)
                        mListen.onSetTab(content.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        //dialog
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
}
