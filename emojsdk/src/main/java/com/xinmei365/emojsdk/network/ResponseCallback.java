package com.xinmei365.emojsdk.network;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResponseCallback<T> {
    public Type mType;

    public ResponseCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public boolean onBefore(HttpReuqest request) {
        return false;
    }

    public void onAfter() {
    }

    public void inProgress(float progress) {

    }

    public abstract void onError(HttpReuqest request, Exception e);

    public abstract void onSucess(T response);


    public static final ResponseCallback<String> DEFAULT_RESPONSE_CALLBACK = new ResponseCallback<String>() {
        @Override
        public void onError(HttpReuqest request, Exception e) {

        }

        @Override
        public void onSucess(String response) {

        }
    };
}