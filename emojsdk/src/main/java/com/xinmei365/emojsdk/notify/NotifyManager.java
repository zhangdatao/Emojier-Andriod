package com.xinmei365.emojsdk.notify;

import android.util.Log;

import com.xinmei365.emojsdk.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinmei on 15/11/19.
 */
public class NotifyManager {

    private static NotifyManager mInstance = null;

    private final String TAG = NotifyManager.class.getSimpleName();
    private Map<String, ArrayList<INotifyCallback>> notifyMap;

    private NotifyManager(){
        notifyMap = new HashMap<String, ArrayList<INotifyCallback>>();

    }

    public static NotifyManager getInstance(){
        if (mInstance == null){
            synchronized (NotifyManager.class){
                if (mInstance == null) {
                    mInstance = new NotifyManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * create notification type according to parameter
     *
     * @param cmd    cmd
     * @param subcmd subcmd
     * @param type   additional fields
     * @return notification type
     */
    public String createFlag(String cmd, String subcmd, String type, String otherMarks) {
        StringBuilder flag = new StringBuilder(cmd + "_" + subcmd);

        if (!StringUtil.isNullOrEmpty(type)) {
            flag.append("_").append(type);
        }

        if (!StringUtil.isNullOrEmpty(otherMarks)) {
            flag.append("_").append(otherMarks);
        }

        return flag.toString();
    }


    /**
     * register message notification
     *
     * @param key      message type
     * @param callback
     */
    public void registerNotifyCallback(String key, INotifyCallback callback) {
        if (StringUtil.isNullOrEmpty(key) || callback == null) {
            return;
        }

        if (!notifyMap.containsKey(key)) {
            notifyMap.put(key, new ArrayList<INotifyCallback>());
        }

        ArrayList<INotifyCallback> notifies = notifyMap.get(key);
        if (notifies.indexOf(callback) < 0) {
            notifies.add(callback);
            Log.d(TAG, "listener name:" + callback.getClass().getSimpleName() + " type:" + key);
        }
    }



    /**
     * register notification message listener
     *
     * @param cmd        cmd
     * @param subcmd     subcmd
     * @param type       type useful in some protocol
     * @param otherMarks additional mark
     * @param callback
     */
    public void registerNotifyCallback(String cmd, String subcmd, String type, String otherMarks, INotifyCallback callback) {
        String notifyFlag = createFlag(cmd, subcmd, type, otherMarks);

        registerNotifyCallback(notifyFlag, callback);
    }

    /**
     * register notification message listener
     *
     * @param cmd      cmd
     * @param subcmd   subcmd
     * @param type     type some protocol need
     * @param callback
     */
    public void registerNotifyCallback(String cmd, String subcmd, String type, INotifyCallback callback) {
        registerNotifyCallback(cmd, subcmd, type, "", callback);
    }

    /**
     * register notification listener
     *
     * @param cmd      cmd
     * @param subcmd   subcmd
     * @param callback
     */
    public void registerNotifyCallback(String cmd, String subcmd, INotifyCallback callback) {
        registerNotifyCallback(cmd, subcmd, "", callback);
    }

    /**
     * delete all listener
     *
     * @param key      message type
     * @param callback
     */
    public void removeNotifyCallback(String key, INotifyCallback callback) {
        if (StringUtil.isNullOrEmpty(key) || callback == null) {
            return;
        }

        if (notifyMap.containsKey(key)) {
            ArrayList<INotifyCallback> callbacks = notifyMap.get(key);

            int index = callbacks.indexOf(callback);

            if (index >= 0) {
                callbacks.remove(callbacks.indexOf(callback));
                Log.d(TAG, "del listener name:" + callback.getClass().getSimpleName() + " type:" + key);
            }

            if (callbacks.size() == 0) {
                notifyMap.remove(key);
            }
        }
    }

    /**
     * delete all listener
     *
     * @param cmd        cmd
     * @param subcmd     subcmd
     * @param type       additional mark
     * @param callback
     * @param otherMarks other mark
     */
    public void removeNotifyCallback(String cmd, String subcmd, String type, String otherMarks, INotifyCallback callback) {
        String notifyFlag = createFlag(cmd, subcmd, type, otherMarks);

        removeNotifyCallback(notifyFlag, callback);
    }

    /**
     * delete listener
     *
     * @param cmd      cmd
     * @param subcmd   subcmd
     * @param type     additional mark
     * @param callback
     */
    public void removeNotifyCallback(String cmd, String subcmd, String type, INotifyCallback callback) {
        removeNotifyCallback(cmd, subcmd, type, "", callback);
    }

    /**
     * register listener
     *
     * @param cmd      cmd
     * @param subcmd   subcmd
     * @param callback type some protocol need
     */
    public void removeNotifyCallback(String cmd, String subcmd, INotifyCallback callback) {
        removeNotifyCallback(cmd, subcmd, "", callback);
    }

    /**
     * remove all listener
     *
     */
    public void removeAllNotifyCallback() {
        Log.d(TAG, "remove all listener");
        notifyMap.clear();
    }

    /**
     * send notification
     *
     * @param key    message type
     * @param entity content entity
     */
    public void sendNotifyCallback(String key, NotifyEntity entity) {
        if (StringUtil.isNullOrEmpty(key) || entity == null) {
            return;
        }

        entity.setKey(key);
        if (notifyMap.containsKey(key)) {
            List<INotifyCallback> callbacks = notifyMap.get(key);
            List<INotifyCallback> wellCalls = new ArrayList<INotifyCallback>();

            int size = callbacks.size();
            for (int i = 0; i < size; i++) {
                if (i >= callbacks.size()) {
                    break;
                }

                INotifyCallback notify = callbacks.get(i);
                if (notify != null) {
                    wellCalls.add(notify);
                }
            }

            for (INotifyCallback call : wellCalls) {
                call.notifyCallback(entity);
                Log.d(TAG, "dispatch message key:" + key + " " + call.getClass().getSimpleName() + "call");
            }
        }
    }

    /**
     * send notification
     *
     * @param entity content entity
     */
    public void sendNotifyCallback(NotifyEntity entity) {
        if (entity == null) {
            return;
        }

        String notifyFlag = createFlag(entity.getCmd(), entity.getSubcmd(), entity.getType(), entity.getOtherMarks());

        sendNotifyCallback(notifyFlag, entity);
    }



}
