package com.xinmei365.emojsdk.notify;

/**
 * Created by xinmei on 15/11/19.
 */
public class NotifyEntity {

    private String cmd = "";
    private String subcmd = "";
    private String type = "";
    private String otherMarks = "";
    private String key = "";

    private Object object;

    /**
     * Creates a new instance of NotifyEntity.
     */
    public NotifyEntity(Object obj) {
        this.object = obj;
    }

    public NotifyEntity(String key, Object object) {
        this.key = key;
        this.object = object;
    }

    /**
     * Creates a new instance of NotifyEntity.
     *
     * @param cmd    cmd
     * @param subcmd subcmd
     */
    public NotifyEntity(String cmd, String subcmd) {
        setCmd(cmd);
        setSubcmd(subcmd);
    }

    /**
     * @return the cmd
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * @param cmd the cmd to set
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the subcmd
     */
    public String getSubcmd() {
        return subcmd;
    }

    /**
     * @param subcmd the subcmd to set
     */
    public void setSubcmd(String subcmd) {
        this.subcmd = subcmd;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the otherMarks
     */
    public String getOtherMarks() {
        return otherMarks;
    }

    /**
     * @param otherMarks the otherMarks to set
     */
    public void setOtherMarks(String otherMarks) {
        this.otherMarks = otherMarks;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Object object) {
        this.object = object;
    }
}
