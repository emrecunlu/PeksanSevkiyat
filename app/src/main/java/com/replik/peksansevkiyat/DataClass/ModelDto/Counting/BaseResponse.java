package com.replik.peksansevkiyat.DataClass.ModelDto.Counting;

public class BaseResponse<T> {
    private T data;
    private boolean success;
    private String message;

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 