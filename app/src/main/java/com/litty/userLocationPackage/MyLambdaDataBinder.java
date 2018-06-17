package com.litty.userLocationPackage;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaDataBinder;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

public class MyLambdaDataBinder implements LambdaDataBinder {

    private final Gson gson;
    Type mType;

    //CUSTOMIZATION: pass typetoken via class constructor
    public MyLambdaDataBinder(Type type) {
        this.gson = new Gson();
        mType = type;
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) {
        if (content == null) {
            return null;
        }
        Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));

        //CUSTOMIZATION: Original line of code: return gson.fromJson (reader, clazz);
        return gson.fromJson(reader, mType);
    }

    @Override
    public byte[] serialize(Object object) {
        return gson.toJson(object).getBytes(StringUtils.UTF8);
    }
}