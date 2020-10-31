package com.example.hellodroid;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RssChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final RecyclerChannelViewClickListener mListener;
    private TextView simpleTextView;
    private RssChannelViewModel model;

    /**
     * The ViewHolder that will be used to display the data in each item shown
     * in the RecyclerView
     *
     * @param itemView
     *         The layout view group used to display the data
     */
    public RssChannelViewHolder(final View itemView, RecyclerChannelViewClickListener listener) {
        super(itemView);
        simpleTextView = (TextView) itemView.findViewById(R.id.simple_title);

        mListener = listener;
        itemView.setOnClickListener(this);
    }

    /**
     * Method that is used to bind the data to the ViewHolder
     *
     * @param viewModel
     *         The viewmodel that contains the data
     */
    public void bindData(final RssChannelViewModel viewModel) {
        simpleTextView.setText(viewModel.getSimpleText());
        simpleTextView.setTextColor(Color.rgb(0,0,0));
        model = viewModel;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition(), model);
    }
}
