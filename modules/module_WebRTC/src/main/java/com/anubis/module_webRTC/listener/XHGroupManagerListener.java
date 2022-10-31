package com.anubis.module_webRTC.listener;

import com.anubis.module_webRTC.database.CoreDB;
import com.anubis.module_webRTC.database.HistoryBean;
import com.anubis.module_webRTC.database.MessageBean;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.utils.AEvent;
import com.starrtc.starrtcsdk.apiInterface.IXHGroupManagerListener;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import java.text.SimpleDateFormat;

public class XHGroupManagerListener implements IXHGroupManagerListener {
    @Override
    public void onMembersUpdeted(String groupID, int number) {

    }

    @Override
    public void onSelfKicked(String groupID) {

    }

    @Override
    public void onGroupDeleted(String groupID) {

    }

    @Override
    public void onReceivedMessage(XHIMMessage message) {
        //msg.fromId 消息来源
        //msg.targetId 目标ID
        //msg.contentData 消息体
        //msg.time 消息发送的时间
        //msg.atList 群内@某个用户 多人用逗号“,”分隔


        HistoryBean historyBean = new HistoryBean();
        historyBean.setType(CoreDB.HISTORY_TYPE_GROUP);
        historyBean.setLastTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        historyBean.setLastMsg(message.contentData);
        historyBean.setConversationId(message.targetId);
        historyBean.setNewMsgCount(1);
        MLOC.INSTANCE.addHistory(historyBean,false);

        MessageBean messageBean = new MessageBean();
        messageBean.setConversationId(message.targetId);
        messageBean.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date()));
        messageBean.setMsg(message.contentData);
        messageBean.setFromId(message.fromId);
        MLOC.INSTANCE.saveMessage(messageBean);

        AEvent.notifyListener(AEvent.AEVENT_GROUP_REV_MSG,true,message);
    }
}
