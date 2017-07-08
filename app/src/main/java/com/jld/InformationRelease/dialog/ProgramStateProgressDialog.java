package com.jld.InformationRelease.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramStateDialogItem;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.view.my_program.adapter.ProgramStateDialogAdapter;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/7 9:29
 */
public class ProgramStateProgressDialog extends DialogFragment {

    private Activity mActivity;
    private ArrayList<ProgramStateDialogItem> mItems;
    public static final String TAG = "ProgramStateProgressDialog";

    private ProgramStateProgressDialog() {
    }

    public static ProgramStateProgressDialog getInstance(ArrayList<ProgramStateDialogItem> items) {
        ProgramStateProgressDialog dialog = new ProgramStateProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("items", items);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate:" + mItems);
        mActivity = getActivity();
        if (getArguments() != null) {
            mItems = getArguments().getParcelableArrayList("items");
        }
        super.onCreate(savedInstanceState);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View view = LayoutInflater.from(mActivity).inflate(R.layout.program_load_state_list, null);
//        RecyclerView list = (RecyclerView) view.findViewById(R.id.rv_program_load_state);
//        list.setLayoutManager(new LinearLayoutManager(mActivity));
//        ProgramStateDialogAdapter adapter = new ProgramStateDialogAdapter(mItems, mActivity);
//        list.setAdapter(adapter);
//        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ProgramStateProgressDialog.this.dismiss();
//            }
//        });
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return view;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        AlertDialog.Builder builder
//                = new AlertDialog.Builder(mActivity);
//        View view = LayoutInflater.from(mActivity).inflate(R.layout.program_load_state_list, null);
//        RecyclerView list = (RecyclerView) view.findViewById(R.id.rv_program_load_state);
//        list.setLayoutManager(new LinearLayoutManager(mActivity));
//        ProgramStateDialogAdapter adapter = new ProgramStateDialogAdapter(mItems, mActivity);
//        list.setAdapter(adapter);
//        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDialog().dismiss();
//            }
//        });
//        builder.setView(view);

        final Dialog stateDialog = new Dialog(mActivity, R.style.CustomDialog);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.program_load_state_list, null);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.rv_program_load_state);
        list.setLayoutManager(new LinearLayoutManager(mActivity));
        ProgramStateDialogAdapter adapter = new ProgramStateDialogAdapter(mItems, mActivity);
        list.setAdapter(adapter);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateDialog.dismiss();
            }
        });
//        mSetNameDialog.setCanceledOnTouchOutside();
//        stateDialog.setCancelable(false);
        stateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题栏
        stateDialog.setContentView(view);
        return stateDialog;
    }
}
