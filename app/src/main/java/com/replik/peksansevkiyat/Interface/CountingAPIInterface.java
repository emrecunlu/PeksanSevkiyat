package com.replik.peksansevkiyat.Interface;

import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.BaseResponse;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.Warehouse;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.StockItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Counting.CreateRecountRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface CountingAPIInterface {
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Recount/WarehouseList")
    Call<BaseResponse<List<Warehouse>>> getWarehouseList();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Recount/StockList")
    Call<BaseResponse<List<StockItem>>> getStockList(@Query("warehouseCode") int warehouseCode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Recount/CreateRecount")
    Call<BaseResponse<Void>> createRecount(@Body CreateRecountRequest request);
}
