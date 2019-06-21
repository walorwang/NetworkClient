package com.threathunter.networkclient.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.threathunter.networkclient.http.constants.NetworkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class StubGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    StubGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        InputStream inputStream = value.byteStream();
        byte[] bytes = readInputStream(inputStream);
        inputStream.read(bytes);
        String json = new String(bytes);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int status = jsonObject.optInt(NetworkConstants.Server.STATUS);
        String code = status + "";
        if (!code.startsWith("2")) {
            String message = jsonObject.optString(NetworkConstants.Server.MESSAGE);
            StubBaseResponse response = new StubBaseResponse();
            response.setStatus(status);
            response.setMessage(message);
            return (T) response;
        }

        try {
            T result = adapter.fromJson(json);
            return result;
        } finally {
            value.close();
        }
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
