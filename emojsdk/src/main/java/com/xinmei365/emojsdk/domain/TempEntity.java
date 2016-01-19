package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/12/11.
 */
public class TempEntity {

    public int start;
    public int end;
    public boolean isEmoj;

    public String content;


    public TempEntity(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public TempEntity(int start, String content, boolean isEmoj) {
        this.start = start;
        this.content = content;
        this.isEmoj = isEmoj;
    }

    public TempEntity(int start, int end, boolean isEmoj) {
        this.start = start;
        this.end = end;
        this.isEmoj = isEmoj;
    }
}
