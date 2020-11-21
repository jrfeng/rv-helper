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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 继承该类即可让你的 RecyclerView.Adapter 具有单选和多选功能。
 * <p>
 * 该类还支持列表项的 “点击” 与 “长按点击” 功能。
 *
 * @param <Holder> 该参数必须继承 RecyclerView.ViewHolder 类并实现 {@link SelectableHelper.Selectable}
 *                 接口。
 */
public abstract class SelectableAdapter<Holder extends RecyclerView.ViewHolder & SelectableHelper.Selectable>
        extends RecyclerView.Adapter<Holder> {
    private SelectableHelper mSelectableHelper;
    private ItemClickHelper mItemClickHelper;

    public SelectableAdapter() {
        mSelectableHelper = new SelectableHelper(this);
        mItemClickHelper = new ItemClickHelper();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mSelectableHelper.attachToRecyclerView(recyclerView);
        mItemClickHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mSelectableHelper.detach();
        mItemClickHelper.detach();
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        mSelectableHelper.updateSelectState(holder, position);
        mItemClickHelper.bindClickListener(holder.itemView);
        mItemClickHelper.bindLongClickListener(holder.itemView);
    }

    /**
     * 设置选择模式。
     *
     * @param mode 选择模式
     */
    public void setSelectMode(SelectableHelper.SelectMode mode) {
        mSelectableHelper.setSelectMode(mode);
    }

    /**
     * 设置一个用于监听 “选中数量” 改变的监听器。
     *
     * @param listener 监听器，可为 null。为 null 时将清除上次设置的监听器。
     */
    public void setOnSelectCountChangeListener(SelectableHelper.OnSelectCountChangeListener listener) {
        mSelectableHelper.setOnSelectCountChangeListener(listener);
    }

    /**
     * 查询某个列表项是否已被选中。
     *
     * @param position 要查询的列表项的位置。
     * @return 如果 position 处的列表项已被选中，则返回 true，否则返回 false。
     */
    public boolean isSelected(int position) {
        return mSelectableHelper.isSelected(position);
    }

    /**
     * 清除所所列表项的选中状态。
     */
    public void clearSelected() {
        mSelectableHelper.clearSelected();
    }

    /**
     * 设置某个列表项的选中状态。
     *
     * @param position 要选中的列表项，如果小于 0，或者超出列表项的最大索引值，则会抛出 IllegalArgumentException 异常。
     * @param select   是否选中指定列表项。
     * @throws IllegalArgumentException 当 position 参数小于 0，或者超出列表项的最大索引值会抛出该异常。
     */
    public void setSelect(int position, boolean select) throws IllegalArgumentException {
        mSelectableHelper.setSelect(position, select);
    }

    /**
     * 设置 {@link ItemClickHelper.OnItemClickListener} 事件监听器，该监听器会在某个列表项被 “点击” 时调用。
     *
     * @param listener 要设置的 {@link ItemClickHelper.OnItemClickListener} 事件监听器，可为 null。为
     *                 null 时相当于清除上次设置的事件监听器。
     */
    public void setOnItemClickListener(ItemClickHelper.OnItemClickListener listener) {
        mItemClickHelper.setOnItemClickListener(listener);
    }

    /**
     * 设置 {@link ItemClickHelper.OnItemLongClickListener} 事件监听器，该监听器会在某个列表项被 “长按点击” 时调用。
     *
     * @param listener 要设置的 {@link ItemClickHelper.OnItemLongClickListener} 事件监听器，可为 null。
     *                 为 null 时相当于清除上次设置的事件监听器。
     */
    public void setOnItemLongClickListener(ItemClickHelper.OnItemLongClickListener listener) {
        mItemClickHelper.setOnItemLongClickListener(listener);
    }

    /**
     * 获取所有已被选中的列表项的索引值。
     *
     * @return 所有已被选中的列表项的索引值，如没有任何列表项被选中，则会返回一个空列表。
     */
    public List<Integer> getSelectedPositions() {
        return mSelectableHelper.getSelectedPositions();
    }
}
