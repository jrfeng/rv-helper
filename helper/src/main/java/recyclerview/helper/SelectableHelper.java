/*
 * MIT License
 *
 * Copyright (c) 2020 jrfeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package recyclerview.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 用于帮助实现 RecyclerView 中列表项的单选与多选功能。
 * <p>
 * 使用方法:
 * <p>
 * <ul>
 *     <li>创建一个 {@link SelectableHelper} 对象；</li>
 *     <li>调用 {@link #attachToRecyclerView(RecyclerView)} 方法将当前 {@link SelectableHelper} 对象
 *     附加到一个 RecyclerView 对象上；
 *     <li>在 RecyclerView.Adapter 的 onBindViewHolder 方法中调用当前 SelectableHelper 对象的
 *     {@link #updateSelectState(RecyclerView.ViewHolder, int)} 方法；</li>
 * </ul>
 * <p>
 * 当不再需要一个 {@link SelectableHelper} 对象时，应该调用它的 {@link #detach()} 方法分离它。建议在
 * RecyclerView.Adapter 的 onDetachedFromRecyclerView 方法中调用该方法。
 * <p>
 * <b>注意！请在 Adapter 中的数据发生改变时调用相应的 notifyXxx 方法, 或者使用 DiffUtil（<b>推荐</b>），不
 * 然 SelectableHelper 可能无法正常工作。</b>
 * <p>
 * 具体做法请参考 <a target="_blank" href="https://github.com/jrfeng/rv-helper/blob/master/helper/src/main/java/recyclerview/helper/SelectableAdapter.java">SelectableAdapter</a> 的源码。
 */
public class SelectableHelper {
    @Nullable
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private SelectMode mSelectMode;
    private List<Integer> mSelectedPositions;

    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private OnSelectCountChangeListener mOnSelectCountChangeListener;

    public SelectableHelper(@NonNull RecyclerView.Adapter adapter) {
        this(adapter, null);
    }

    public SelectableHelper(@NonNull RecyclerView.Adapter adapter, @Nullable List<Integer> selectedPositions) {
        NonNullUtil.requireNonNull(adapter);

        mAdapter = adapter;
        mRecyclerView = null;

        if (selectedPositions == null) {
            mSelectMode = SelectMode.SINGLE;
            mSelectedPositions = new ArrayList<>();
        } else {
            mSelectMode = SelectMode.MULTIPLE;
            mSelectedPositions = new ArrayList<>(selectedPositions);
        }

        initAdapterDataObserver();
    }

    /**
     * 需要在 RecyclerView.onBindViewHolder 方法中调用该方法。
     */
    public <Holder extends RecyclerView.ViewHolder & Selectable> void updateSelectState(@NonNull Holder holder, int position) {
        if (mSelectedPositions.contains(position)) {
            holder.onSelected();
        } else {
            holder.onUnselected();
        }
    }

    /**
     * 附加到一个 RecyclerView 对象上。
     *
     * @param recyclerView 要附加到的 RecyclerView 对象，不能为 null。
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        NonNullUtil.requireNonNull(recyclerView);

        mRecyclerView = recyclerView;
        registerAdapterDataObserver();
    }

    /**
     * 分离当前 {@link SelectableHelper} 对象。
     */
    public void detach() {
        mRecyclerView = null;
        unregisterAdapterDataObserver();
    }

    /**
     * 判断当前 {@link SelectableHelper} 对象是否已附加到一个 RecyclerView 对象上。
     *
     * @return 如果当前 {@link SelectableHelper} 对象已附加到一个 RecyclerView 对象上则返回 true，否则
     * 返回 false。
     */
    public boolean isAttached() {
        return mRecyclerView != null;
    }

    /**
     * 设置列表的选择模式。默认为 {@link SelectMode#SINGLE} 单选模式。
     *
     * @param mode 选择模式。
     */
    public void setSelectMode(SelectMode mode) {
        mSelectMode = mode;

        if (mSelectMode == SelectMode.SINGLE) {
            deselectOthers();
        }
    }

    /**
     * 清除所所列表项的选中状态。
     */
    @SuppressWarnings("WhileLoopReplaceableByForEach")
    public void clearSelected() {
        Iterator<Integer> iterator = mSelectedPositions.iterator();
        while (iterator.hasNext()) {
            // 不能使用 for 循环，因为该方法会会删除列表中的元素
            deselect(iterator.next());
        }
        notifySelectCountChanged();
    }

    /**
     * 设置一个用于监听 “选中数量” 改变的监听器。
     *
     * @param listener 监听器，可为 null。为 null 时将清除上次设置的监听器。
     */
    public void setOnSelectCountChangeListener(@Nullable OnSelectCountChangeListener listener) {
        mOnSelectCountChangeListener = listener;
    }

    /**
     * 设置某个列表项的选中状态。
     *
     * @param position 要选中的列表项，如果小于 0，或者超出列表项的最大索引值，则会抛出 IllegalArgumentException 异常。
     * @param select   是否选中指定列表项。
     * @throws IllegalArgumentException 当 position 参数小于 0，或者超出列表项的最大索引值会抛出该异常。
     */
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

    /**
     * 切换 position 处的选择状态。
     */
    public void toggle(int position) {
        setSelect(position, !isSelected(position));
    }

    /**
     * 查询某个列表项是否已被选中。
     *
     * @param position 要查询的列表项的位置。
     * @return 如果 position 处的列表项已被选中，则返回 true，否则返回 false。
     */
    public boolean isSelected(int position) {
        return mSelectedPositions.contains(position);
    }

    /**
     * 获取被选中项的数量。
     *
     * @return 被选中项的数量。
     */
    public int getSelectedCount() {
        return mSelectedPositions.size();
    }

    /**
     * 获取所有已被选中的列表项的索引值。
     *
     * @return 所有已被选中的列表项的索引值，如没有任何列表项被选中，则会返回一个空列表。
     */
    public List<Integer> getSelectedPositions() {
        return new ArrayList<>(mSelectedPositions);
    }

    // **************************************private**************************************

    private void initAdapterDataObserver() {
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

                notifySelectCountChanged();
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

        if (mSelectMode == SelectMode.SINGLE && mSelectedPositions.size() > 0) {
            clearSelected();
        }

        mSelectedPositions.add(position);
        notifySelectCountChanged();

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
        notifySelectCountChanged();

        if (mRecyclerView == null) {
            return;
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            Selectable selectable = (Selectable) holder;
            selectable.onUnselected();
        } else {
            mAdapter.notifyItemChanged(position);
        }
    }

    private void notifySelectCountChanged() {
        if (mOnSelectCountChangeListener != null) {
            mOnSelectCountChangeListener.onSelectCountChanged(mSelectedPositions.size());
        }
    }

    /**
     * 选择模式。
     */
    public enum SelectMode {
        /**
         * 单选模式。
         */
        SINGLE,
        /**
         * 多选模式。
         */
        MULTIPLE
    }

    public interface Selectable {
        void onSelected();

        void onUnselected();
    }

    /**
     * 监听选中数量改变的。
     */
    public interface OnSelectCountChangeListener {
        /**
         * 当选中的列表项的数量发生改变时会回调该方法。
         *
         * @param selectedCount 当前的选中数量
         */
        void onSelectCountChanged(int selectedCount);
    }
}
