package recyclerview.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PositionHelper<T extends RecyclerView.ViewHolder & PositionHelper.OnPositionChangeListener> {
    private RecyclerView.Adapter<T> mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;

    public PositionHelper(@NonNull RecyclerView.Adapter<T> adapter) {
        NonNullUtil.requireNonNull(adapter);
        mAdapter = adapter;
        initAdapterDataObserver();
    }

    private void initAdapterDataObserver() {
        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                for (int i = positionStart; i < mAdapter.getItemCount(); i++) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                    if (!(viewHolder instanceof PositionHelper.OnPositionChangeListener)) {
                        mAdapter.notifyItemChanged(i);
                        continue;
                    }

                    OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                    listener.onPositionChanged(i, i + itemCount);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                for (int i = (positionStart + itemCount); i < mAdapter.getItemCount(); i++) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                    if (!(viewHolder instanceof PositionHelper.OnPositionChangeListener)) {
                        mAdapter.notifyItemChanged(i);
                        continue;
                    }

                    OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                    listener.onPositionChanged(i, i - itemCount);
                }
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                if (fromPosition > toPosition) {
                    for (int i = toPosition; i < fromPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (!(viewHolder instanceof OnPositionChangeListener)) {
                            mAdapter.notifyItemChanged(i);
                            continue;
                        }

                        OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                        listener.onPositionChanged(i, i + itemCount);
                    }

                    int interval = fromPosition - toPosition;
                    for (int i = fromPosition; i < (fromPosition + itemCount); i++) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (!(viewHolder instanceof OnPositionChangeListener)) {
                            mAdapter.notifyItemChanged(i);
                            continue;
                        }

                        OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                        listener.onPositionChanged(i, i - interval);
                    }
                    return;
                }

                if (fromPosition < toPosition) {
                    for (int i = (fromPosition + itemCount); i < (toPosition + itemCount); i++) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (!(viewHolder instanceof OnPositionChangeListener)) {
                            mAdapter.notifyItemChanged(i);
                            continue;
                        }

                        OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                        listener.onPositionChanged(i, i - itemCount);
                    }

                    int interval = toPosition - fromPosition;
                    for (int i = fromPosition; i < (fromPosition + itemCount); i++) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (!(viewHolder instanceof OnPositionChangeListener)) {
                            mAdapter.notifyItemChanged(i);
                            continue;
                        }

                        OnPositionChangeListener listener = (OnPositionChangeListener) viewHolder;
                        listener.onPositionChanged(i, i + interval);
                    }
                }
            }
        };
    }

    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        NonNullUtil.requireNonNull(recyclerView);
        mRecyclerView = recyclerView;
        mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    public void detach() {
        mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    public interface OnPositionChangeListener {
        void onPositionChanged(int oldPosition, int newPosition);
    }
}
