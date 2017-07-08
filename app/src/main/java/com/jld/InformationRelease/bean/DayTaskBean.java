package com.jld.InformationRelease.bean;

import android.os.Parcel;

import com.jld.InformationRelease.base.BaseProgram;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/6 15:04
 */
public class DayTaskBean extends BaseProgram {

    public DayTaskBean(){}
    protected DayTaskBean(Parcel in) {
        super(in);
    }

    /**
     * 任务节目列表集合
     */
    private ArrayList<DayTaskItem> program_item = new ArrayList<>();

    public ArrayList<DayTaskItem> getProgram_item() {
        return program_item;
    }

    public void setProgram_item(ArrayList<DayTaskItem> program_item) {
        this.program_item = program_item;
    }

    @Override
    public String toString() {
        return "DayTaskBean{" +
                "program_item=" + program_item +
                '}';
    }

    public class DayTaskItem{
        private String programName;
        private String stateTime;
        private String stopTime;
        private String programLocalId;

        public DayTaskItem() {
        }

        public String getProgramLocalId() {
            return programLocalId;
        }

        public void setProgramLocalId(String programLocalId) {
            this.programLocalId = programLocalId;
        }

        public String getProgramName() {
            return programName;
        }

        public void setProgramName(String programName) {
            this.programName = programName;
        }

        public String getStateTime() {
            return stateTime;
        }

        public void setStateTime(String stateTime) {
            this.stateTime = stateTime;
        }

        @Override
        public String toString() {
            return "DayTaskBean{" +
                    "programName='" + programName + '\'' +
                    ", stateTime='" + stateTime + '\'' +
                    ", stopTime='" + stopTime + '\'' +
                    ", programLocalId='" + programLocalId + '\'' +
                    '}';
        }

        public String getStopTime() {
            return stopTime;
        }

        public void setStopTime(String stopTime) {
            this.stopTime = stopTime;
        }
    }
}
