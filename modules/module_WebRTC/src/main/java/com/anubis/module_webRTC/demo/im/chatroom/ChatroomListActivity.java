package com.anubis.module_webRTC.demo.im.chatroom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.serverAPI.InterfaceUrls;
import com.anubis.module_webRTC.ui.CircularCoverView;
import com.anubis.module_webRTC.utils.AEvent;
import com.anubis.module_webRTC.utils.ColorUtils;
import com.anubis.module_webRTC.utils.DensityUtils;
import com.anubis.module_webRTC.utils.StarListUtil;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatroomListActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<ChatroomInfo> mDatas;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_list);
        ((TextView)findViewById(R.id.title_text)).setText("聊天室列表");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatroomListActivity.this,ChatroomCreateActivity.class));
            }
        });

        AEvent.addListener(AEvent.AEVENT_GOT_LIST,this);

        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatroomListActivity.this,ChatroomCreateActivity.class));
            }
        });


        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);

        mDatas = new ArrayList<>();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myListAdapter = new MyListAdapter();
        vList = (ListView) findViewById(R.id.list);
        vList.setAdapter(myListAdapter);
        vList.setOnItemClickListener(this);
        vList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case SCROLL_STATE_IDLE:
                        if(StarListUtil.isListViewReachTopEdge(absListView)){
                            refreshLayout.setEnabled(true);
                        }else{
                            refreshLayout.setEnabled(false);
                        }
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        AEvent.addListener(AEvent.AEVENT_GOT_LIST,this);
    }

    @Override
    public void onResume(){
        super.onResume();
        queryAllList();
    }

    private void queryAllList(){
        if(MLOC.INSTANCE.getAEventCenterEnable()){
            InterfaceUrls.demoQueryList(""+ MLOC.INSTANCE.getLIST_TYPE_CHATROOM());
        }else{
            XHClient.getInstance().getChatroomManager().queryList("",""+ MLOC.INSTANCE.getLIST_TYPE_CHATROOM(),new IXHResultCallback() {
                @Override
                public void success(final Object data) {
                    String[] res = (String[]) data;
                    JSONArray array = new JSONArray();
                    for (int i=0;i<res.length;i++){
                        String info = res[i];
                        try {
                            info = URLDecoder.decode(info,"utf-8");
                            JSONObject jsonObject = new JSONObject(info);
                            array.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }

                    refreshLayout.setRefreshing(false);
                    mDatas.clear();
                    try {
//                    JSONArray array = (JSONArray) data;
                        for(int i = array.length()-1;i>=0;i--){
                            ChatroomInfo info = new ChatroomInfo();
                            JSONObject obj = array.getJSONObject(i);
                            info.createrId = obj.getString("creator");
                            info.roomId = obj.getString("id");
                            info.roomName = obj.getString("name");
                            mDatas.add(info);
                        }
                        myListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(String errMsg) {
                    refreshLayout.setRefreshing(false);
                    mDatas.clear();
                    myListAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_GOT_LIST,this);
        super.onStop();
    }

    @Override
    public void dispatchEvent(String aEventID, final boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_GOT_LIST:
                refreshLayout.setRefreshing(false);
                mDatas.clear();

                if(success){
                    JSONArray datas = (JSONArray) eventObj;
                    for(int i = 0;i<datas.length();i++){
                        try {
                            JSONObject json = datas.getJSONObject(i);
                            String tmp = json.getString("data");
                            JSONObject tmpObj = new JSONObject(URLDecoder.decode(tmp,"utf-8"));
                            ChatroomInfo item = new ChatroomInfo();
                            item.createrId = tmpObj.getString("creator");
                            item.roomId = tmpObj.getString("id");
                            item.roomName = tmpObj.getString("name");
                            mDatas.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    myListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatroomInfo clickInfo = mDatas.get(position);
        Intent intent = new Intent(ChatroomListActivity.this, ChatroomActivity.class);
        intent.putExtra(ChatroomActivity.TYPE,ChatroomActivity.CHATROOM_ID);
        intent.putExtra(ChatroomActivity.CHATROOM_ID,clickInfo.roomId);
        intent.putExtra(ChatroomActivity.CHATROOM_NAME,clickInfo.roomName);
        intent.putExtra(ChatroomActivity.CREATER_ID,clickInfo.createrId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        queryAllList();
    }

    class MyListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewIconImg;
            if(convertView == null){
                viewIconImg = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_all_list,null);
                viewIconImg.vRoomName = (TextView)convertView.findViewById(R.id.item_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                viewIconImg.vHeadBg =  convertView.findViewById(R.id.head_bg);
                viewIconImg.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                viewIconImg.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (ViewHolder)convertView.getTag();
            }
            viewIconImg.vRoomName.setText(mDatas.get(position).roomName);
            viewIconImg.vCreaterId.setText(mDatas.get(position).createrId);
            viewIconImg.vHeadBg.setBackgroundColor(ColorUtils.getColor(ChatroomListActivity.this,mDatas.get(position).roomName));
            viewIconImg.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(ChatroomListActivity.this,28);
            viewIconImg.vHeadCover.setRadians(cint, cint, cint, cint,0);
            viewIconImg.vHeadImage.setImageResource(R.drawable.icon_im_chatroom_item);
            return convertView;
        }

        class  ViewHolder{
            private TextView vRoomName;
            private TextView vCreaterId;
            public View vHeadBg;
            public CircularCoverView vHeadCover;
            public ImageView vHeadImage;
        }
    }


}
