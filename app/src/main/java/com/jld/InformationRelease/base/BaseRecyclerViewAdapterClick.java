package com.jld.InformationRelease.base;

import android.view.View;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/26 13:50
 */
public interface BaseRecyclerViewAdapterClick {

    public interface MyItemClick {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    /**
     * 添加点击监听
     *
     * @param onItemSelectClick
     */
    public void setMyItemSelectClick(MyItemClick onItemSelectClick);
}
