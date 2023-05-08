package edu.uic.swethag.cs478.funclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ListViewHolder> {

    private ArrayList<ListItem> songsList;
    //    ArrayList<AudioModel> songsList;
    Context context;
    private OnSongListener mOnSongListener;

    public MusicListAdapter(ArrayList<ListItem> songsList, Context context, OnSongListener onSongListener) {
//  public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
        mOnSongListener = onSongListener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MusicListAdapter.ListViewHolder(view, mOnSongListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //        AudioModel songData = songsList.get(position);
        ListItem currentSong = songsList.get(position);
        holder.mImage.setImageBitmap(currentSong.getImage());
        holder.mTitle.setText(currentSong.getTitle());
        holder.mArtist.setText(currentSong.getArtist());
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImage;
        public TextView mTitle;
        public TextView mArtist;
        OnSongListener onSongListener;

        public ListViewHolder(View itemView, OnSongListener onSongListener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.imageView);
            mTitle = itemView.findViewById(R.id.title);
            mArtist = itemView.findViewById(R.id.artist);
            this.onSongListener = onSongListener;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            try {
                onSongListener.onSongClick(getAdapterPosition());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnSongListener {
        void onSongClick(int position) throws RemoteException;
    }
}

