package com.jld.InformationRelease.view.my_program.program;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;

public class BaseProgramCompileActivity extends BaseActivity {
    ProgramBean mProgramBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_program_compile);
    }

    /**
     * 设置节目标签dialog
     */
    public void setTabDialog() {
        final EditText editText = new EditText(this);
        editText.setHint(getString(R.string.program_hint));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.input_program_tab)).setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mProgramBean.setTab(editText.getText().toString());
            }
        }).show();
    }
}
