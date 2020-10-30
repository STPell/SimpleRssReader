package com.example.hellodroid;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RssItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final RecyclerItemViewClickListener mListener;
    private TextView simpleTextView;
    private RssItemViewModel model;

    /**
     * The ViewHolder that will be used to display the data in each item shown
     * in the RecyclerView
     *
     * @param itemView
     *         The layout view group used to display the data
     */
    public RssItemViewHolder(final View itemView, RecyclerItemViewClickListener listener) {
        super(itemView);
        simpleTextView = (TextView) itemView.findViewById(R.id.simple_text);

        mListener = listener;
        itemView.setOnClickListener(this);
    }

    /**
     * Method that is used to bind the data to the ViewHolder
     *
     * @param viewModel
     *         The viewmodel that contains the data
     */
    public void bindData(final RssItemViewModel viewModel) {
        simpleTextView.setText(viewModel.getSimpleText());
        model = viewModel;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition(), model);
    }
}
