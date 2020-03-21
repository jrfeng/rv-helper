package recyclerview.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 功能: 用于帮助属性 RecyclerView 中列表项的单选与多选功能。
 * <p>
 * 使用方法:
 * <p>
 * <ul>
 *     <li>创建一个 {@link SelectableHelper} 对象；</li>
 *     <li>调用 {@link #attachToRecyclerView(RecyclerView)} 方法将当前 {@link SelectableHelper} 对象
 *     附加到一个 RecyclerView 对象上；
 *     <li>在 RecyclerView.Adapter 的 onBindViewHolder 方法中调用当前 SelectableHelper 对象的
 *     {@link #onBindViewHolder(RecyclerView.ViewHolder, int)} 方法；</li>
 * </ul>
 * <p>
 * 当不再需要一个 {@link SelectableHelper} 对象时，应该调用它的 {@link #detach()} 方法分离它。建议在
 * RecyclerView.Adapter 的 onDetachedFromRecyclerView 方法中调用该方法。
 * <p>
 * <b>注意！请在 Adapter 中的数据发生改变时调用相应的 notifyXxx 方法, 或者使用 DiffUtil（<b>推荐</b>），不
 * 然 SelectableHelper 可能无法正常工作。</b>
 * <p>
 * 具体做法请参考 {@link SelectableAdapter} 的源码。
 */
public class SelectableHelper {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTIPLE = 1;

    @Nullable
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private int mMode;
    private List<Integer> mSelectedPositions;

    private RecyclerView.AdapterDataObserver mAdapterDataObserver;

    private ItemClickHelper mItemClickHelper;

    public SelectableHelper(@NonNull RecyclerView.Adapter adapter) {
        NonNullHelper.requireNonNull(adapter);

        mAdapter = adapter;
        mRecyclerView = null;

        mMode = MODE_SINGLE;
        mSelectedPositions = new ArrayList<>();

        initListener();

        mItemClickHelper = new ItemClickHelper();
    }


    public <Holder extends RecyclerView.ViewHolder & Selectable> void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onUnselected();
        if (mSelectedPositions.contains(position)) {
            holder.onSelected();
        }

        mItemClickHelper.bindClickListener(holder.itemView);
    }

    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        NonNullHelper.requireNonNull(recyclerView);

        mRecyclerView = recyclerView;
        mItemClickHelper.attachToRecyclerView(mRecyclerView);
        registerAdapterDataObserver();
    }

    public void detach() {
        mRecyclerView = null;
        mItemClickHelper.detach();
        unregisterAdapterDataObserver();
    }

    public boolean isAttached() {
        return mRecyclerView != null;
    }

    public void setSelectMode(int mode) {
        switch (mode) {
            case MODE_SINGLE:
                mMode = mode;
                deselectOthers();
                break;
            case MODE_MULTIPLE:
                mMode = mode;
                break;
            default:
                break;
        }

    }

    public void clearSelected() {
        for (Integer selectedPosition : mSelectedPositions.subList(0, mSelectedPositions.size())) {
            deselect(selectedPosition);
        }
    }

    public void setSelect(int position, boolean select) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position < 0");
        }

        int count = mAdapter.getItemCount();
        if (position > count) {
            throw new IllegalArgumentException("position out of bound. position is " + position + ", size is: " + count);
        }

        if (select) {
            select(position);
        } else {
            deselect(position);
        }
    }

    public boolean isSelected(int position) {
        return mSelectedPositions.contains(position);
    }

    public List<Integer> getSelectedPositions() {
        return new ArrayList<>(mSelectedPositions);
    }

    public void setOnItemClickListener(ItemClickHelper.OnItemClickListener listener) {
        mItemClickHelper.setOnItemClickListener(listener);
    }

    // **************************************private**************************************

    private void initListener() {
        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                for (int i = 0; i < mSelectedPositions.size(); i++) {
                    Integer selectedItem = mSelectedPositions.get(i);
                    if (selectedItem >= positionStart) {
                        mSelectedPositions.set(i, selectedItem + itemCount);
                    }
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int end = positionStart + itemCount;

                for (Integer selectedItem : new ArrayList<>(mSelectedPositions)) {
                    if (selectedItem >= positionStart && selectedItem < end) {
                        mSelectedPositions.remove(selectedItem);
                    } else if (selectedItem >= end) {
                        mSelectedPositions.remove(selectedItem);
                        mSelectedPositions.add(selectedItem - itemCount);
                    }
                }
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                for (int i = 0; i < itemCount; i++) {
                    int from = fromPosition + i;
                    int to = toPosition + i;

                    if (from < to) {
                        // 修复 DiffUtil 的问题, 导致问题的原因: 例如在交换索引 1 和 5 的元素时, DiffUtil
                        // 会先交换 1 和 4, 再交换 5 和 1, 这个 if 语句的作用是忽略到 1 和 4 的交换。
                        continue;
                    }

                    if (mSelectedPositions.contains(from) && mSelectedPositions.contains(to)) {
                        continue;
                    }

                    if (mSelectedPositions.contains(from)) {
                        mSelectedPositions.remove(Integer.valueOf(from));
                        mSelectedPositions.add(to);
                    } else if (mSelectedPositions.contains(to)) {
                        mSelectedPositions.remove(Integer.valueOf(to));
                        mSelectedPositions.add(from);
                    }
                }
            }
        };
    }

    private void registerAdapterDataObserver() {
        mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    private void unregisterAdapterDataObserver() {
        mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    // 清除除第 1 个选中项外的其他选项的选中状态
    private void deselectOthers() {
        if (mSelectedPositions.size() <= 1) {
            return;
        }

        List<Integer> buf = new ArrayList<>(mSelectedPositions);
        buf.remove(Collections.min(buf));

        for (Integer i : buf) {
            deselect(i);
        }
    }

    private void select(int position) {
        if (isSelected(position)) {
            return;
        }

        if (mMode == MODE_SINGLE && mSelectedPositions.size() > 0) {
            clearSelected();
        }

        mSelectedPositions.add(position);

        if (mRecyclerView == null) {
            return;
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            Selectable selectable = (Selectable) holder;
            selectable.onSelected();
        }
    }

    private void deselect(int position) {
        if (!isSelected(position)) {
            return;
        }

        // 要手动装箱, 否则调用的是 remove(int):E 方法, 而不是 remove(Object):boolean 方法
        mSelectedPositions.remove(Integer.valueOf(position));

        if (mRecyclerView == null) {
            return;
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            Selectable selectable = (Selectable) holder;
            selectable.onUnselected();
        }
    }

    public interface Selectable {
        void onSelected();

        void onUnselected();
    }
}
