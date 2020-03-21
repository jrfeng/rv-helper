package recyclerview.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于实现监听列表项 “点击/长按点击” 事件的功能。
 * <p>
 * 如果你需要监听列表项视图中任意一个或多个 View 的 “点击/长按点击” 事件，可以使用 {@link ItemClickHelper} 类。
 *
 * @param <VH> RecyclerView.ViewHolder
 */
public abstract class ItemClickableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private ItemClickHelper mItemClickHelper = new ItemClickHelper();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mItemClickHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mItemClickHelper.detach();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        mItemClickHelper.bindClickListener(holder.itemView);
        mItemClickHelper.bindLongClickListener(holder.itemView);
    }

    /**
     * 设置列表项 “点击” 事件监听器。
     *
     * @param listener 列表项 “点击” 事件监听器。
     */
    public void setOnItemClickListener(ItemClickHelper.OnItemClickListener listener) {
        mItemClickHelper.setOnItemClickListener(listener);
    }

    /**
     * 设置列表项 “长按点击” 事件监听器。
     *
     * @param listener 列表项 “长按点击” 事件监听器。
     */
    public void setOnItemLongClickListener(ItemClickHelper.OnItemLongClickListener listener) {
        mItemClickHelper.setOnItemLongClickListener(listener);
    }
}
