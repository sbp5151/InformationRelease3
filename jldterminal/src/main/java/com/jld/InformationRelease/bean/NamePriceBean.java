package com.jld.InformationRelease.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/20 17:30
 */
public class NamePriceBean implements Parcelable {

    private String name;
    private String price;

    public NamePriceBean() {
    }

    public NamePriceBean(String name, String price) {
        this.name = name;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NamePriceBean{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(price);
    }
    protected NamePriceBean(Parcel in){
        name = in.readString();
        price = in.readString();
    }
    public static final Creator<NamePriceBean> CREATOR = new Creator<NamePriceBean>() {
        @Override
        public NamePriceBean createFromParcel(Parcel in) {
            return new NamePriceBean(in);
        }
        @Override
        public NamePriceBean[] newArray(int size) {
            return new NamePriceBean[size];
        }
    };
}
