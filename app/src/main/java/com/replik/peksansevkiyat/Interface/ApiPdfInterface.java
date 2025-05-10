package com.replik.peksansevkiyat.Interface;

import com.replik.peksansevkiyat.DataClass.ModelDto.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPdfInterface {
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Generate/Yukleme")
    Call<Result> generatePdf(@Query("sevkNo") String sevkNo, @Query("staffId") int staffId);
}
