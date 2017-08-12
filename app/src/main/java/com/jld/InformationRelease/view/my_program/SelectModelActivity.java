package com.jld.InformationRelease.view.my_program;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.GetModelResponse;
import com.jld.InformationRelease.interfaces.IViewListen;
import com.jld.InformationRelease.presenter.GetModelPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.my_program.adapter.ModelAdapter;
import com.jld.InformationRelease.view.my_program.program_create.ProgramImageActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramTextActivity;
import com.jld.InformationRelease.view.my_program.program_create.ProgramVideoActivity;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * 二：模板选择
 */
public class SelectModelActivity extends BaseActivity implements IViewListen<GetModelResponse> {

    public static final String TAG = "SelectModelActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<GetModelResponse.Model> models = new ArrayList<>();
    private static final int LOAD_MODEL = 0x21;
    private ModelAdapter mModelAdapter;
    private ProgressDialog mDialog;
    private String[] intros = {"幻灯片", "奶茶店价格表", "广告集"};
    private String[] modleIds = {Constant.IMAGE_MODEL, Constant.NAICHA_MODEL_1, Constant.VIDEO_MODEL};
    private String[] imgs = {"https://imgsa.baidu.com/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=f9767158b0de9c82b268f1dd0de8eb6f/f9198618367adab421c38e2288d4b31c8701e444.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497875392095&di=ed08e02021f32a3bd518dbbbf216799b&imgtype=0&src=http%3A%2F%2Fpic2.ooopic.com%2F03%2F35%2F48%2F63b1OOOPIC16.jpg",
            "http://pic.qiantucdn.com/58pic/16/66/50/89t58PICaGb_1024.jpg!/fw/780/watermark/url/L3dhdGVybWFyay12MS4zLnBuZw==/align/center"};

    private static final int PROGRAM_REQUEST = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate:");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_model);
        initView();
//        initData();
        mDialog = new ProgressDialog(this);
    }

    public void initView() {
        //title
        View view = findViewById(R.id.title_select_model);
        View title = view.findViewById(R.id.title_back);
        View right = view.findViewById(R.id.title_right);
        right.setVisibility(View.GONE);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView center = (TextView) view.findViewById(R.id.title_center);
        center.setText(getResources().getString(R.string.select_model));

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_select_model);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        GetModelResponse model = new GetModelResponse();
        for (int i = 0; i < intros.length; i++) {//假数据
            GetModelResponse.Model item = model.new Model();
            item.setIntro(intros[i]);
            item.setModelimg(imgs[i]);
            item.setModelid(modleIds[i]);
            model.getModels().add(item);
        }
        models = model.getModels();
        mModelAdapter = new ModelAdapter(models, this);
        mRecyclerView.setAdapter(mModelAdapter);
        mModelAdapter.setModeItemClickListener(mClickItemListener);
    }

    ModelAdapter.ModeItemClickListener mClickItemListener = new ModelAdapter.ModeItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            GetModelResponse.Model model = mModelAdapter.getModel(position);
            String modelid = model.getModelid();
            Intent intent = null;
            switch (modelid) {
                case Constant.NAICHA_MODEL_1://奶茶
                    intent = new Intent(SelectModelActivity.this, ProgramTextActivity.class);
                    intent.putExtra("model_img",imgs[1]);
                    break;
                case Constant.IMAGE_MODEL://幻灯片
                    intent = new Intent(SelectModelActivity.this, ProgramImageActivity.class);
                    intent.putExtra("model_img",imgs[0]);
                    break;
                case Constant.VIDEO_MODEL://视频广告
                    intent = new Intent(SelectModelActivity.this, ProgramVideoActivity.class);
                    intent.putExtra("model_img",imgs[2]);
                    break;
            }
            if (intent != null) {
                SelectModelActivity.this.startActivityForResult(intent, PROGRAM_REQUEST);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult:" + requestCode);
        if (requestCode == PROGRAM_REQUEST && data != null) {
            ProgramBean body = (ProgramBean) data.getParcelableExtra("body");
            LogUtil.d(TAG, "ProgramBean:" + body);
            Intent intent = new Intent();
            intent.putExtra("body", body);
            setResult(mProgramResultCode, intent);//编辑结果返回
        }
        finish();
    }

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
