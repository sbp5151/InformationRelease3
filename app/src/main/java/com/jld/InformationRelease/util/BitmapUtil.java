package com.jld.InformationRelease.util;

import android.graphics.Bitmap;

import net.bither.util.NativeUtil;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/8 16:19
 */
public class BitmapUtil {



    /**
     * @param bit           需要压缩的bitmap
     * @param fileNameBytes 压缩后保存文件的地址
     * @param optimize      是否采用哈弗曼表数据计算，品质相差5-10倍，但对设备性能有所要求
     * @return
     */
    public static String compressBitmap(Bitmap bit,int quality, String fileNameBytes,
                                        boolean optimize) {
        return NativeUtil.compressBitmap(bit,quality, fileNameBytes, optimize);
    }
}
