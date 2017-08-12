package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jld.InformationRelease.R;

/**
 * Created by boping on 2017/8/10.
 */

/**
 * 拍照/相册选择
 */
public class PortraitDialog extends DialogFragment{

    public PortraitDialog(OnPortraitListen onPortraitListen) {
        mOnPortraitListen = onPortraitListen;
    }

    public interface OnPortraitListen{
        void capturePicture();//拍照
        void phonePicture();//相册
    }
    OnPortraitListen mOnPortraitListen;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 点击头像图片的点击事件，弹出更改头像的对话框,设置头像
        View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_photo_choose, null);
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        ImageView closeView = (ImageView) view
                .findViewById(R.id.dialog_close_iv);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    dialog.dismiss();
            }
        });
        Button capture_picture = (Button) view
                .findViewById(R.id.capture_picture);
        capture_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mOnPortraitListen.capturePicture();
                dialog.dismiss();
            }
        });
        Button phohe_picture = (Button) view.findViewById(R.id.phohe_picture);
        phohe_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mOnPortraitListen.phonePicture();
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        // 设置显示动画

        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dialog;
    }



}
