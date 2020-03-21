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
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * 实现了 DiffUtil.Callback 的部分通用功能。
 *
 * @param <T> 元素的类型。
 */
public abstract class AbstractDiffCallback<T> extends DiffUtil.Callback {
    private List<T> mOldList;
    private List<T> mNewList;

    public AbstractDiffCallback(@NonNull List<T> oldList, @NonNull List<T> newList) {
        NonNullHelper.requireNonNull(oldList);
        NonNullHelper.requireNonNull(newList);

        mOldList = oldList;
        mNewList = newList;
    }

    public List<T> getOldList() {
        return mOldList;
    }

    public List<T> getNewList() {
        return mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldItemPosition >= mOldList.size()
                || newItemPosition >= mNewList.size()) {
            return false;
        }

        return areItemsTheSame2(oldItemPosition, newItemPosition);
    }

    public abstract boolean areItemsTheSame2(int oldItemPosition, int newItemPosition);
}
