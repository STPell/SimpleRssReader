package com.example.hellodroid;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class RssItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final RecyclerItemViewClickListener mListener;
    private TextView simpleTitleView;
    private TextView simpleDescriptionView;
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
        simpleTitleView = (TextView) itemView.findViewById(R.id.simple_title);
        simpleDescriptionView = (TextView) itemView.findViewById(R.id.simple_description);

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
        simpleTitleView.setText(viewModel.getTitleText());
        simpleTitleView.setTextColor(Color.rgb(0,0,0));
        simpleDescriptionView.setText(Html.fromHtml(viewModel.getShortDescription(), Html.FROM_HTML_MODE_COMPACT, null, null));
        //simpleDescriptionView.setText(viewModel.getShortDescription());
        model = viewModel;
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition(), model);
    }
}
