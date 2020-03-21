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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于帮助处理 RecyclerView 中列表项的 “点击/长按点击” 事件。
 * <p>
 * 可用于处理列表项视图中任意一个或多个 View 的  “点击/长按点击” 事件。
 * <p>
 * <b>使用步骤：</b>
 *
 * <ol>
 *     <li>创建一个 {@link ItemClickHelper} 对象；</li>
 *     <li>调用 {@link #attachToRecyclerView(RecyclerView)} 方法将 {@link ItemClickHelper}
 *     对象附件到一个 RecyclerView 对象上；</li>
 *     <li>（可选）调用 {@link #setOnItemClickListener(OnItemClickListener)} 设置 “点击” 事件
 *     监听器；</li>
 *     <li>（可选）调用 {@link #setOnItemLongClickListener(OnItemLongClickListener)} 设置
 *     “长按点击” 事件监听器；</li>
 *     <li>在 RecyclerView.Adapter 的 onBindViewHolder 方法中调用
 *     {@link #bindClickListener(View...)} 方法绑定某个或多个 View 的 “点击” 事件监听器；调用
 *     {@link #bindLongClickListener(View...)} 方法绑定某个或多个 View 的 “长按点击” 事件监听器。</li>
 * </ol>
 * <p>
 * 当不再需要 {@link ItemClickHelper} 对象时，应该调用 {@link #detach()} 方法进行分离。
 *
 * <p>
 * <b>例：</b>
 * <code>
 * <pre>
 * public abstract class ItemClickableAdapter&lt;VH extends RecyclerView.ViewHolder&gt; extends RecyclerView.Adapter&lt;VH&gt; {
 *     private ItemClickHelper mItemClickHelper = new ItemClickHelper();
 *
 *     &#64;Override
 *     public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
 *         super.onAttachedToRecyclerView(recyclerView);
 *
 *         // 附加到 RecyclerView 对象上
 *         mItemClickHelper.attachToRecyclerView(recyclerView);
 *     }
 *
 *     &#64;Override
 *     public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
 *         super.onDetachedFromRecyclerView(recyclerView);
 *
 *         // 分离 mItemClickHelper
 *         mItemClickHelper.detach();
 *     }
 *
 *     &#64;Override
 *     public void onBindViewHolder(@NonNull VH holder, int position) {
 *
 *         // 绑定列表项的 “点击” 事件
 *         mItemClickHelper.bindClickListener(holder.itemView);
 *
 *         // 绑定列表项的 “长按点击” 事件
 *         mItemClickHelper.bindLongClickListener(holder.itemView);
 *     }
 *
 *     public void setOnItemClickListener(ItemClickHelper.OnItemClickListener listener) {
 *         mItemClickHelper.setOnItemClickListener(listener);
 *     }
 *
 *     public void setOnItemLongClickListener(ItemClickHelper.OnItemLongClickListener listener) {
 *         mItemClickHelper.setOnItemLongClickListener(listener);
 *     }
 * }
 * </pre>
 * </code>
 * <p>
 * 如果你仅关心列表项的点击事件，那么继承 {@link ItemClickableAdapter} 类可能是更好的选择。
 */
public class ItemClickHelper {
    @Nullable
    private RecyclerView mRecyclerView;

    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    @Nullable
    private OnItemClickListener mItemClickListener;
    @Nullable
    private OnItemLongClickListener mItemLongClickListener;

    public ItemClickHelper() {
        initAllListener();
    }

    /**
     * 附加到一个 RecyclerView 对象。
     * <p>
     * 一个 {@link ItemClickHelper} 对象只有附加到 RecyclerView 对象时才有效。
     *
     * @param recyclerView 要附加到的 RecyclerView 对象，不能为 null。
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        NotNullHelper.requireNonNull(recyclerView);

        mRecyclerView = recyclerView;
    }

    /**
     * 分离当前 ItemClickHelper 对象。
     */
    public void detach() {
        mRecyclerView = null;
    }

    /**
     * 判断当前 ItemClickHelper 对象是否已附加到一个 RecyclerView 对象。
     *
     * @return 如果当前 ItemClickHelper 已附加到一个 RecyclerView 对象则返回 true，否则返回 false。
     */
    public boolean isAttached() {
        return mRecyclerView != null;
    }

    /**
     * 绑定 View 的 OnClickListener 事件监听器。
     * <p>
     * 可以绑定列表项视图中的任意一个或多个 View，当绑定的 View 被点击时，如果已设置
     * {@link OnItemClickListener}，则该事件监听器会被调用。
     * <p>
     * 可以在 RecyclerView.Adapter 的 onBindViewHolder 方法中调用该方法将 “点击” 事件监听器绑定到列表项视
     * 图中一个或多个 View 上。
     *
     * @param views 要绑定 OnClickListener 事件监听器的 View，可以是列表项视图中的任意一个或多个 View。
     * @see #setOnItemClickListener(OnItemClickListener)
     */
    public void bindClickListener(View... views) {
        View.OnClickListener clickListener = null;

        if (mItemClickListener != null) {
            clickListener = mClickListener;
        }

        for (View view : views) {
            view.setOnClickListener(clickListener);
        }
    }

    /**
     * 绑定 View 的 OnLongClickListener 事件监听器。
     * <p>
     * 可以绑定列表项视图中的任意一个或多个 View，当绑定的 View 被 “长按点击” 时，如果已设置
     * {@link OnItemLongClickListener}，则该事件监听器会被调用。
     * <p>
     * 可以在 RecyclerView.Adapter 的 onBindViewHolder 方法中调用该方法将 “长按点击” 事件监听器绑定到列表
     * 项视图中一个或多个 View 上。
     *
     * @param views 要绑定 OnLongClickListener 事件监听器的 View，可以是列表项视图中的任意一个或多个 View。
     * @see #setOnItemLongClickListener(OnItemLongClickListener)
     */
    public void bindLongClickListener(View... views) {
        View.OnLongClickListener longClickListener = null;

        if (mItemLongClickListener != null) {
            longClickListener = mLongClickListener;
        }

        for (View view : views) {
            view.setOnLongClickListener(longClickListener);
        }
    }

    /**
     * 设置 {@link OnItemClickListener} 事件监听器，该监听器会在某个以绑定的 View 被点击时调用。
     *
     * @param listener 要设置的 {@link OnItemClickListener} 事件监听器，可为 null。为 null 时相当于清除
     *                 上次设置的 {@link OnItemClickListener} 事件监听器。
     * @see #bindClickListener(View...)
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * 设置 {@link OnItemLongClickListener} 事件监听器，该监听器会在某个以绑定的 View 被点击时调用。
     *
     * @param listener 要设置的 {@link OnItemLongClickListener} 事件监听器，可为 null。为 null 时相当于
     *                 清除上次设置的 {@link OnItemLongClickListener} 事件监听器。
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    // *******************************private****************************

    private void initAllListener() {
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView == null) {
                    return;
                }

                RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(v);
                if (holder == null) {
                    return;
                }

                if (mItemClickListener == null) {
                    return;
                }

                mItemClickListener.onItemClicked(holder.getAdapterPosition(), v.getId(), v, holder);
            }
        };

        mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mRecyclerView == null) {
                    return false;
                }

                RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(v);
                if (holder == null) {
                    return false;
                }

                if (mItemLongClickListener == null) {
                    return false;
                }

                return mItemLongClickListener.onItemLongClicked(holder.getAdapterPosition(), v.getId(), v, holder);
            }
        };
    }

    // ****************************public interface**********************

    /**
     * 列表项 “点击” 事件监听器。
     */
    public interface OnItemClickListener {
        /**
         * 当列表项或者列表项中某个 View 被点击时，该方法会被调用。
         *
         * @param position 被点击的列表项在 RecyclerView 中的位置。
         * @param viewId   列表项中被点击的 View 的 ID。
         * @param view     列表项中被点击的 View。
         * @param holder   被点击的列表项的 ViewHolder。
         */
        void onItemClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder);
    }

    /**
     * 列表项 “长按点击” 事件监听器。
     */
    public interface OnItemLongClickListener {
        /**
         * 当列表项或者列表项中某个 View 被 “长按点击” 时，该方法会被调用。
         *
         * @param position 被 “长按点击” 的列表项在 RecyclerView 中的位置。
         * @param viewId   列表项中被 “长按点击” 的 View 的 ID。
         * @param view     列表项中被 “长按点击” 的 View。
         * @param holder   被 “长按点击” 的列表项的 ViewHolder。
         * @return 是否已处理 “长按点击” 事件，是的话则返回 true，否则返回 false。
         */
        boolean onItemLongClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder);
    }
}
