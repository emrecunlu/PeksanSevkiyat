package com.replik.peksansevkiyat.Interface;

import androidx.annotation.Nullable;

import com.replik.peksansevkiyat.DataClass.ModelDto.ApkVersion;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.Customer;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrder;
import com.replik.peksansevkiyat.DataClass.ModelDto.Customer.CustomerOrderDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Dtos.dtoPalletDetailAndSeritra_data;
import com.replik.peksansevkiyat.DataClass.ModelDto.Dtos.getStandartLong;
import com.replik.peksansevkiyat.DataClass.ModelDto.Label.ZarfLabelResult;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.Order.OrderList;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderDetail.OrderDetailList;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingList;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.OrderShippingTransport;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateOrderShippingTransportDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.CreatePalletDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletContentDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletContentResponse;
import com.replik.peksansevkiyat.DataClass.ModelDto.OrderShipping.UpdateShipmentVehicleStatusDto;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletDetail;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletLabelResponse;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.PalletSingle;
import com.replik.peksansevkiyat.DataClass.ModelDto.Pallet.dtoPalletPrint;
import com.replik.peksansevkiyat.DataClass.ModelDto.PalletDetail.PalletDetailDtos;
import com.replik.peksansevkiyat.DataClass.ModelDto.PalletDetail.PalletDetailList;
import com.replik.peksansevkiyat.DataClass.ModelDto.Personel.PersonelList;
import com.replik.peksansevkiyat.DataClass.ModelDto.Result;
import com.replik.peksansevkiyat.DataClass.ModelDto.Seritra.spSeritraSingle;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.LotItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.StockItem;
import com.replik.peksansevkiyat.DataClass.ModelDto.Stock.StockLot;
import com.replik.peksansevkiyat.DataClass.ModelDto.Transfer.TransferRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getApkVersion")
    Call<ApkVersion> getApkVersion();

    // --------------- GET
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getPersonels")
    Call<PersonelList> getUserList();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getSeriControl/{staffId}/{seri}")
    Call<spSeritraSingle> getSeriControl(@Path("staffId") Number staffId, @Path("seri") String barcode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getSeriControlFromPallet/{staffId}/{seri}")
    Call<dtoPalletDetailAndSeritra_data> getSeriControlFromPallet(@Path("staffId") Number staffId, @Path("seri") String barcode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getPalletControlByID/{staffId}/{id}")
    Call<PalletSingle> getPalletControl(@Path("staffId") Number staffId, @Path("id") Integer Id);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getPalletControl/{staffId}/{barcode}")
    Call<PalletSingle> getPalletControl(@Path("staffId") Number staffId, @Path("barcode") String barcode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getPalletDetail/{staffId}/{palletId}")
    Call<PalletDetailList> getPalletDetail(@Path("staffId") Number staffId, @Path("palletId") Number palletId);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getOrderList/{staffId}")
    Call<OrderList> getOrderList(@Path("staffId") Number staffId, @Query("search") String search);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getOrderDetailList/{staffId}/{sipNo}")
    Call<OrderDetailList> getOrderDetailList(@Path("staffId") Number staffId, @Path("sipNo") String sipNo);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getPalletPrint/{staffId}")
    Call<dtoPalletPrint.PalletPrintList> getPalletPrint(@Path("staffId") Number staffId);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/getOrderShippingList/{staffId}")
    Call<OrderShippingList> getOrderShippingList(@Path("staffId") Number staffId, @Query("search") String search);

    // --------------- SET
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/setPalletDetail")
    Call<PalletDetailList> setPalletDetail(@Body PalletDetailDtos.setPalletDetailColumn palletDetailColumn);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/setPalletPrint")
    Call<Result> setPalletPrint(@Body getStandartLong getStandartLong);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/setOrderCollectedByBarcode")
    Call<Result> setOrderCollectedByBarcode(@Body OrderDtos.setPickingItem pickingData);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/setOrderStatus")
    Call<Result> setOrderStatus(@Body OrderDtos.setOrderStatus data);

    // --------------- DELETE
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @HTTP(method = "POST", path = "api/delPalletDetail", hasBody = true)
    Call<PalletDetailList> delPalletDetail(@Body PalletDetailDtos.delPalletDetailColumn delPalletDetailColumn);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @HTTP(method = "POST", path = "api/delPallet", hasBody = true)
    Call<Result> delPallet(@Body getStandartLong delStandartColumn);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @HTTP(method = "POST", path = "api/delOrderPicking", hasBody = true)
    Call<Result> delOrderPicking(@Body OrderDtos.delPickingItem delPickingItem);


    // --------------- NETSIS PROP
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/setNetsisEIrsPacked/{belgeNo}")
    Call<Result> setNetsisEIrsPacked(@Path("belgeNo") String belgeNo);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/setNetsisEIrsSend/{belgeNo}/{cariKod}")
    Call<Result> setNetsisEIrsSend(@Path("belgeNo") String belgeNo, @Path("cariKod") String cariKod);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/setNetsisPrint/{gibBelgeNo}")
    Call<Result> setNetsisPrint(@Path("gibBelgeNo") String gibBelgeNo);

    // --------------- NETSIS PROP
    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Shipment/CariList")
    Call<List<Customer>> getCustomers();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Shipment/SevkList")
    Call<List<CustomerOrder>> getCustomerOrders(@Query("cariKod") String customerCode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Shipment/OrderList")
    Call<List<CustomerOrderDetail>> getOrderDetail(@Query("sevkNo") String sevkNo);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Shipment/PalletDetailList")
    Call<List<PalletDetail>> getPalletDetail(@Query("barkod") String barcode);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Shipment/CreateOrderProducts")
    Call<Result> createOrderByProducts(@Body OrderDtos.createOrderByProductsDto createOrderByProductsDto);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Shipment/CreateNetsisShipment")
    Call<ZarfLabelResult> createNetsisShipment(@Body OrderDtos.setNetsisShipment setNetsisShipmentDto);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/Shipment/TransportList")
    Call<List<OrderShippingTransport>> getTransportList();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Shipment/UpdateTransportType")
    Call<Result> updateTransportType(@Body UpdateOrderShippingTransportDto updateOrderShippingTransportDto);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Pallet/PalletDetailList")
    Call<PalletContentResponse> PalletDetailList(@Body PalletContentDto palletContentDto);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/Pallet/CreatePallet")
    Call<PalletLabelResponse> PalletCreate(@Body CreatePalletDto createPalletDto);
    @POST("api/Shipment/VehicleControl")
    Call<Result> updateVehicleStatus(@Body UpdateShipmentVehicleStatusDto updateShipmentVehicleStatusDto);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/MaterialTransfer/AllStocks")
    Call<List<StockItem>> getStockList();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/MaterialTransfer/RequestStocks")
    Call<List<StockItem>> getRequstedStockList();

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/MaterialTransfer/StockMovements/{stockCode}")
    Call<List<StockLot>> getStockLotList(@Path("stockCode") String stockCode, @Query("search") String search);

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @GET("api/MaterialTransfer/StockMovements/{stockCode}")
    Call<List<LotItem>> getLotList(
        @Path("stockCode") String stockCode,
        @Query("search") String search
    );

    @Headers({"CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"})
    @POST("api/MaterialTransfer/TransferMaterial")
    Call<Result> transferMaterial(@Body List<TransferRequest> request);
}

