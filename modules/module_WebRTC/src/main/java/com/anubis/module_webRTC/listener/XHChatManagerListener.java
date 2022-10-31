package com.anubis.module_webRTC.listener;

import com.anubis.kt_extends.EExtendsKt;
import com.anubis.module_webRTC.database.CoreDB;
import com.anubis.module_webRTC.database.HistoryBean;
import com.anubis.module_webRTC.database.MessageBean;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHChatManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import java.text.SimpleDateFormat;

public class XHChatManagerListener implements IXHChatManagerListener {
    @Override
    public void onReceivedMessage(XHIMMessage message) {
        EExtendsKt.eLog(this,"XHChatManager-onReceivedMessage\n"+message.toString(),"TAG");
        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_C2C);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setLastMsg(message.contentData);
        historyBean.setConversationId(message.fromId);
        historyBean.setNewMsgCount(1);
        MLOC.INSTANCE.addHistory(historyBean,false);

        MessageBean messageBean = new MessageBean();
        messageBean.setConversationId(message.fromId);
        messageBean.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        messageBean.setMsg(message.contentData);
        messageBean.setFromId(message.fromId);
        MLOC.INSTANCE.saveMessage(messageBean);

        AEvent.notifyListener(AEvent.AEVENT_C2C_REV_MSG,true,message);

    }

    @Override
    public void onReceivedSystemMessage(XHIMMessage message) {
        EExtendsKt.eLog(this,"XHChatManager-onReceivedSystemMessage\n"+message.toString(),"TAG");
        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_C2C);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setLastMsg(message.contentData);
        historyBean.setConversationId(message.fromId);
        historyBean.setNewMsgCount(1);
        MLOC.INSTANCE.addHistory(historyBean,false);

        MessageBean messageBean = new MessageBean();
        messageBean.setConversationId(message.fromId);
        messageBean.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        messageBean.setMsg(message.contentData);
        messageBean.setFromId(message.fromId);
        MLOC.INSTANCE.saveMessage(messageBean);

        AEvent.notifyListener(AEvent.AEVENT_REV_SYSTEM_MSG,true,message);
    }
}
