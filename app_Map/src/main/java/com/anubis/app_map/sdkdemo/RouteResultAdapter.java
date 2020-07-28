package com.anubis.app_map.sdkdemo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anubis.app_map.R;
import com.baidu.navisdk.adapter.struct.BNRouteDetail;

import java.util.ArrayList;

public class RouteResultAdapter extends RecyclerView.Adapter<RouteResultAdapter.BNHolder> {

    private ArrayList<BNRouteDetail> mRouteDetails;

    public RouteResultAdapter(ArrayList<BNRouteDetail> routeList) {
        mRouteDetails = routeList;
    }

    @Override
    public BNHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BNHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_detail,
                parent, false));
    }

    @Override
    public void onBindViewHolder(BNHolder holder, int position) {
        holder.icon.setImageResource(mRouteDetails.get(position).getIconId());
        holder.title.setText(mRouteDetails.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mRouteDetails.size();
    }

    public static class BNHolder extends RecyclerView.ViewHolder {
        public final ImageView icon;
        public final TextView title;

        public BNHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.iv_icon);
            title = v.findViewById(R.id.tv_title);
        }
    }
}
