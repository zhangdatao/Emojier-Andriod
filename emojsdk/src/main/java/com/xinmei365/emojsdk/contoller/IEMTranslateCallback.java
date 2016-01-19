package com.xinmei365.emojsdk.contoller;

import com.xinmei365.emojsdk.domain.EMTranslatEntity;

/**
 * Created by xinmei on 15/12/28.
 */
public interface IEMTranslateCallback {

    void onEmojTransferSuccess(EMTranslatEntity translatEntity);

    void onEmojTransferError();

    void onEmptyMsgTranslate();
}
