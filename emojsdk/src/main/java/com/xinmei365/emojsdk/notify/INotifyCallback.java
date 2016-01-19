package com.xinmei365.emojsdk.notify;

/**
 * Created by xinmei on 15/11/19.
 */
public interface INotifyCallback {

    /**
     * notify callback interface
     *
     * @param entity
     *            Notify component callback to high level object
     */
    void notifyCallback(NotifyEntity entity);
}
