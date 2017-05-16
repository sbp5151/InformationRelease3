package com.jld.InformationRelease.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/10 17:15
 */
class MyHintDialog {

    private int layoutId;
    private String title;
    private String content;
    private String confirm;
    private String cancel;
    private Context mContext;
    private ClickListenOne mClickListenOne;
    private ClickListenTwo mClickListenTwo;

    public MyHintDialog(int layoutId, String title, String content, String confirm, String cancel, Context context, ClickListenTwo clickListen) {
        this.layoutId = layoutId;
        this.title = title;
        this.content = content;
        this.confirm = confirm;
        this.cancel = cancel;
        this.mContext = context;
        this.mClickListenTwo = clickListen;
    }

    public MyHintDialog(int layoutId, String title, String content, String confirm, Context context, ClickListenOne clickListen) {
        this.layoutId = layoutId;
        this.title = title;
        this.content = content;
        this.confirm = confirm;
        this.mContext = context;
        this.mClickListenOne = clickListen;
    }

    public void show() {

        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        if(view!=null){


        }else{
             throw new IllegalArgumentException();
        }

    }

    public interface ClickListenOne {

        void clickOne();
    }

    interface ClickListenTwo {
        void clickOne();

        void clickTwo();
    }

}
