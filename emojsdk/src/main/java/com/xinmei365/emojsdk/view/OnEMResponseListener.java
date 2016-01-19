package com.xinmei365.emojsdk.view;

import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;

/**
 * Created by xinmei on 15/11/20.
 */
public interface OnEMResponseListener {


    void onEMRespnSpanError(Exception exp);

    void onEMRespProperty(EMReceiveTxtEntity EMReceiveTxtEntity);

    void onEMRespSpanSb(EMCandiateEntity emojCandiateEntity);

}
