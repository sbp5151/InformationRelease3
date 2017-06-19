package com.jld.InformationRelease.view.my_terminal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response_bean.GetModelResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.GetModelPresenter;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_program.ProgramCompileActivity;
import com.jld.InformationRelease.view.my_terminal.adapter.ModelAdapter;

import java.util.ArrayList;

/**
 * 二：模板选择
 */
public class SelectModelActivity extends AppCompatActivity implements IViewListen<GetModelResponse> {

    private RecyclerView mRecyclerView;
    private ArrayList<GetModelResponse.Model> models = new ArrayList<>();
    private static final int LOAD_MODEL = 0x21;
    private ModelAdapter mModelAdapter;
    private ProgressDialog mDialog;
    private ArrayList<String> mCheckMacs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_model);
        mCheckMacs = (ArrayList<String>) getIntent().getSerializableExtra("checkMacs");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_select_model);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mModelAdapter = new ModelAdapter(models, this);
        mRecyclerView.setAdapter(mModelAdapter);
        mModelAdapter.setModeItemClickListener(mClickListener);
        initData();
        mDialog = new ProgressDialog(this);
    }

    ModelAdapter.ModeItemClickListener mClickListener = new ModelAdapter.ModeItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            GetModelResponse.Model model = mModelAdapter.getModel(position);
            String modelid = model.getModelid();
            Intent intent = new Intent(SelectModelActivity.this, ProgramCompileActivity.class);
            intent.putExtra("checkMacs", mCheckMacs);
            intent.putExtra("modelid", modelid);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    public void initData() {
        GetModelPresenter presenter = new GetModelPresenter(this, this);
        presenter.getModel(LOAD_MODEL);
    }


    @Override
    public void showProgress(int requestTag) {
        mDialog.show();
    }

    @Override
    public void hideProgress(int requestTag) {
        mDialog.dismiss();
    }

    @Override
    public void loadDataSuccess(GetModelResponse data, int requestTag) {
        mModelAdapter.setDataChange(data.getModels());
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        mDialog.dismiss();
        ToastUtil.showToast(this, e.getMessage().toString(), 3000);
    }
}
