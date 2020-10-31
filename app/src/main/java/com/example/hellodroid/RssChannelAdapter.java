package com.example.hellodroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RssChannelAdapter extends RecyclerView.Adapter {
    private List<RssChannelViewModel> models = new ArrayList<>();
    private RecyclerChannelViewClickListener mListener;

    /**
     * Adapter constructor
     *
     * @param viewModels
     *         A collection of viewmodels which will contain the data that will be used in each ViewHolder
     */
    public RssChannelAdapter(final List<RssChannelViewModel> viewModels, RecyclerChannelViewClickListener listener) {
        if (viewModels != null) {
            this.models.addAll(viewModels);
        }

        mListener = listener;
    }

    /**
     * This is where the ViewHolder(s) are created. Since the framework handles the initialization and recycling
     * we only need to use the viewtype passed in here to inflate our View
     *
     * @param parent
     *         The ViewGroup into which the new View will be added after it is bound to
     *         an adapter position.
     * @param viewType
     *         The view type of the new View.
     *
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RssChannelViewHolder(view, mListener);
    }

    /**
     * This is where the data is bound to each ViewHolder. This method is called at least once and will be
     * called each time the adapter is notified that the data set has changed
     *
     * @param holder
     *         The ViewHolder
     * @param position
     *         The position in our collection of data
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((RssChannelViewHolder) holder).bindData(models.get(position));
    }

    /**
     * Gets the size of the collection of items in our list
     *
     * @return An Integer representing the size of the collection that will be displayed
     */
    @Override
    public int getItemCount() {
        return models.size();
    }

    /**
     * Gets the item view type. We can return the static constant that the Android framework
     * creates for us.
     *
     * @param position
     *         The position in the collection
     *
     * @return The item layout id
     */
    @Override
    public int getItemViewType(final int position) {
        return R.layout.channel_simple_itemview;
    }

    public void addItem(RssChannelViewModel item) {
        models.add(item);
        this.notifyItemInserted(models.size());
    }
}
