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
 *
 * @param <Holder> 该参数必须继承 RecyclerView.ViewHolder 类并实现 {@link SelectableHelper.Selectable}
 *                 接口。
 */
public abstract class SelectableAdapter<Holder extends RecyclerView.ViewHolder & SelectableHelper.Selectable>
        extends RecyclerView.Adapter<Holder> {
    private SelectableHelper mSelectableHelper;

    public SelectableAdapter() {
        mSelectableHelper = new SelectableHelper(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mSelectableHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mSelectableHelper.detach();
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        mSelectableHelper.onBindViewHolder(holder, position);
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
     * 获取所有已被选中的列表项的索引值。
     *
     * @return 所有已被选中的列表项的索引值，如没有任何列表项被选中，则会返回一个空列表。
     */
    public List<Integer> getSelectedPositions() {
        return mSelectableHelper.getSelectedPositions();
    }
}
