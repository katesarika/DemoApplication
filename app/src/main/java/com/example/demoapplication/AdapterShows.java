package com.example.demoapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterShows extends RecyclerView.Adapter<AdapterShows.ItemHolder> {
    private List<Episode> episodeArrayList;
    private Context mContext;
    String i;
    public AdapterShows()
    {}
    public AdapterShows(Context mContext, ArrayList<Episode> episodeArrayList) {
        this.mContext = mContext;
        this.episodeArrayList = episodeArrayList;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);
        ItemHolder itemHolder = new ItemHolder(view);
        return itemHolder;
    }
    public void removeItem(int position) {
        episodeArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Episode item, int position) {
        episodeArrayList.add(position, item);
        notifyItemInserted(position);
    }
    public List<Episode> getData() {
        return episodeArrayList;
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        // set the data
        Episode modal = episodeArrayList.get(position);

        Picasso.get().load(episodeArrayList.get(position).getUrl()).placeholder(R.drawable.previe_no).into(holder.ivEpisode);
        holder.tv_name.setText(""+episodeArrayList.get(position).getName());
        holder.tv_type.setText("Type : " + episodeArrayList.get(position).getType());
        holder.tv_season.setText("season : " + episodeArrayList.get(position).getSeason());
    }

    @Override
    public int getItemCount() {
        return  episodeArrayList==null ? 0 :episodeArrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_type, tv_season;
        AppCompatImageView ivEpisode;
        ItemHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_type = view.findViewById(R.id.tv_type);
            tv_season = view.findViewById(R.id.tv_season);
            ivEpisode=view.findViewById(R.id.iv_epi);
        }
    }
}

