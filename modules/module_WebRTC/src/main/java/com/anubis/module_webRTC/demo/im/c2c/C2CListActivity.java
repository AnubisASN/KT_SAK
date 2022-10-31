package com.anubis.module_webRTC.demo.im.c2c;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anubis.module_webRTC.database.CoreDB;
import com.anubis.module_webRTC.database.HistoryBean;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.ui.CircularCoverView;
import com.anubis.module_webRTC.utils.ColorUtils;
import com.anubis.module_webRTC.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

public class C2CListActivity extends BaseActivity {
    private String mTargetId;
    private List<HistoryBean> mHistoryList;
    private ListView vHistoryList;
    private MyListAdapter listAdapter;

    private C2CActivity.MyChatroomListAdapter mAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_c2c_list);
        //StatusBarUtils.with(this).setColor(Color.parseColor("#FF6C00")).initSDK();
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("一对一会话列表");
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(C2CListActivity.this,C2CCreateActivity.class));
            }
        });

        mHistoryList = new ArrayList<>();
        listAdapter = new MyListAdapter();
        vHistoryList = (ListView) findViewById(com.anubis.module_webRTC.R.id.history_list);
        vHistoryList.setAdapter(listAdapter);
        vHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLOC.INSTANCE.addHistory(mHistoryList.get(position),true);
                mTargetId = (String) mHistoryList.get(position).getConversationId();
                Intent intent = new Intent(C2CListActivity.this,C2CActivity.class);
                intent.putExtra("targetId",mTargetId);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        MLOC.INSTANCE.setHasNewC2CMsg(false);
        mHistoryList.clear();
       List<HistoryBean> list = MLOC.INSTANCE.getHistoryList(CoreDB.HISTORY_TYPE_C2C);
       if(list!=null&&list.size()>0){
            mHistoryList.addAll(list);
       }
       listAdapter.notifyDataSetChanged();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        onResume();
    }



    public class MyListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(mHistoryList!=null)
                return mHistoryList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mHistoryList ==null)
                return null;
            return mHistoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mHistoryList ==null)
                return 0;
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder itemSelfHolder;
            if(convertView == null){
                itemSelfHolder = new ViewHolder();
                convertView = mInflater.inflate(com.anubis.module_webRTC.R.layout.item_c2c_history,null);
                itemSelfHolder.vUserId = (TextView) convertView.findViewById(com.anubis.module_webRTC.R.id.item_id);
                itemSelfHolder.vTime = (TextView) convertView.findViewById(com.anubis.module_webRTC.R.id.item_time);
                itemSelfHolder.vMessage = (TextView) convertView.findViewById(com.anubis.module_webRTC.R.id.item_msg);
                itemSelfHolder.vCount = (TextView) convertView.findViewById(com.anubis.module_webRTC.R.id.item_count);
                itemSelfHolder.vHeadBg =  convertView.findViewById(com.anubis.module_webRTC.R.id.head_bg);
                itemSelfHolder.vHeadImage = (ImageView) convertView.findViewById(com.anubis.module_webRTC.R.id.head_img);
                itemSelfHolder.vHeadCover = (CircularCoverView) convertView.findViewById(com.anubis.module_webRTC.R.id.head_cover);
                convertView.setTag(itemSelfHolder);
            }else{
                itemSelfHolder = (ViewHolder)convertView.getTag();
            }

            HistoryBean historyBean = mHistoryList.get(position);
            String userId = historyBean.getConversationId();
            itemSelfHolder.vUserId.setText(userId);
            itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(C2CListActivity.this,userId));
            itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(C2CListActivity.this,28);
            itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
            itemSelfHolder.vHeadImage.setImageResource(MLOC.INSTANCE.getHeadImage(C2CListActivity.this,userId));

            itemSelfHolder.vTime.setText(historyBean.getLastTime());
            itemSelfHolder.vMessage.setText(historyBean.getLastMsg());
            if(historyBean.getNewMsgCount()==0){
                itemSelfHolder.vCount.setVisibility(View.INVISIBLE);
            }else{
                itemSelfHolder.vCount.setText(""+historyBean.getNewMsgCount());
                itemSelfHolder.vCount.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vTime;
        public TextView vMessage;
        public TextView vCount;
        public View vHeadBg;
        public CircularCoverView vHeadCover;
        public ImageView vHeadImage;
    }

}
