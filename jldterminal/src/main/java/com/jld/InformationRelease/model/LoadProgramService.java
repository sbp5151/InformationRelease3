package com.jld.InformationRelease.model;

import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.util.URLConstant;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 9:49
 */
public interface LoadProgramService {

    /**
     * 节目加载
     * @return
     */
    @POST(URLConstant.LOAD_PROGRAM_URL)
    Observable<ProgramResponseBean> loadProgram(
            @Body() LoadProgramBody body);


    class LoadProgramBody{

        private String sign;
        private String programId;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getProgramid() {
            return programId;
        }

        public void setProgramid(String programid) {
            this.programId = programid;
        }

        @Override
        public String toString() {
            return "LoadProgramBody{" +
                    "sign='" + sign + '\'' +
                    ", programid='" + programId + '\'' +
                    '}';
        }
    }

}
