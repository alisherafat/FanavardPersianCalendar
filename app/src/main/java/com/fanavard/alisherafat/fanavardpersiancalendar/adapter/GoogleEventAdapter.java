package com.fanavard.alisherafat.fanavardpersiancalendar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanavard.alisherafat.fanavardpersiancalendar.R;
import com.fanavard.alisherafat.fanavardpersiancalendar.models.GoogleEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * this adapter helps to render items of google calendar account
 */
public class GoogleEventAdapter extends RecyclerView.Adapter<GoogleEventAdapter.ViewHolder> {
    private List<GoogleEvent> items;
    private Context context;

    public GoogleEventAdapter(Context context, List<GoogleEvent> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_google_event, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GoogleEvent item = items.get(position);
        holder.txtDateTime.setText(item.getPersianTime());
        holder.txtTitle.setText(item.getTitle());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtDateTime)
        TextView txtDateTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

}