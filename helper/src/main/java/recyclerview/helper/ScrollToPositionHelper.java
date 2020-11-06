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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 滚动到 RecyclerView 的某一 Item 位置时对该 Item 做背景闪动动画。
 * <p>
 * <b>注意！该类不支持低于 API Level 16 的版本。</b>
 * <p>
 * 默认情况下，背景闪动动画的颜色为黄色（Color.YELLOW）；持续时为 300ms；插值器为 LinearInterpolator。
 * <p>
 * 可以调用 {@link #setAnimDuration(int)} 方法和 {@link #setAnimInterpolator(TimeInterpolator)} 方
 * 法设置动画的持续时间与插值器。
 * <p>
 * <b>使用步骤：</b>
 * <p>
 * <ol>
 *     <li>创建一个 ScrollToPositionHelper 对象</li>
 *     <li>当要滚动 RecyclerView 时, 调用该对象的 {@link #scrollToPosition(int)} 或者 {@link #smoothScrollToPosition(int)} 方法即可</li>
 * </ol>
 * <p>
 * <b>例：</b>
 * <p>
 * <code>
 * <pre>
 * ScrollToPositionHelper helper = new ScrollToPositionHelper(recyclerView);
 * helper.scrollToPosition(40);
 * </pre>
 * </code>
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ScrollToPositionHelper {
    private static final int DEFAULT_DURATION = 300;
    private static final int NO_POSITION = -1;

    private RecyclerView mRecyclerView;
    private int mDuration;

    private int mPosition;

    private ColorDrawable mColorDrawable;

    private ValueAnimator mAnimator;

    private View mTargetView;
    private Drawable mTargetViewBackground;

    // 由于调用 scrollToPosition 方法时只会触发 RecyclerView.OnScrollListener 的 onScrolled 方法，而
    // 不会触发 onScrollStateChanged 方法，由于任何滚动事件都会触发 onScrolled 方法，因此需要在
    // onScrolled 方法中使用一个标志位来判断是否执行了 scrollToPosition 方法。
    private boolean mExecutedScrollToPosition;

    /**
     * 创建一个 ScrollToPositionHelper 对象。
     *
     * @param recyclerView RecyclerView 对象，不能为 null。
     */
    public ScrollToPositionHelper(@NonNull RecyclerView recyclerView) {
        this(recyclerView, Color.YELLOW, DEFAULT_DURATION);
    }

    /**
     * 创建一个 ScrollToPositionHelper 对象。
     *
     * @param recyclerView   RecyclerView 对象，不能为 null。
     * @param highlightColor 背景闪动颜色。
     */
    public ScrollToPositionHelper(@NonNull RecyclerView recyclerView, @ColorInt int highlightColor) {
        this(recyclerView, highlightColor, DEFAULT_DURATION);
    }

    /**
     * 创建一个 ScrollToPositionHelper 对象。
     *
     * @param recyclerView   RecyclerView 对象，不能为 null。
     * @param highlightColor 背景闪动颜色。
     * @param duration       背景闪动动画的持续时间，单位：毫秒。小于 0 时默认为 0。
     */
    public ScrollToPositionHelper(@NonNull RecyclerView recyclerView, @ColorInt int highlightColor, int duration) {
        NonNullUtil.requireNonNull(recyclerView);
        if (duration < 0) {
            duration = 0;
        }

        mRecyclerView = recyclerView;
        mDuration = duration;
        mColorDrawable = new ColorDrawable(highlightColor);
        mColorDrawable.setAlpha(0);
        mPosition = NO_POSITION;

        initAnimator();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (mPosition == NO_POSITION || newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }

                startBackgroundAnim();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (mExecutedScrollToPosition) {
                    mExecutedScrollToPosition = false;
                    startBackgroundAnim();
                }
            }
        });
    }

    /**
     * 设置背景闪动动画的持续时间。
     *
     * @param duration 背景闪动动画的持续时间，单位：毫秒（ms）。小于 0 时默认为 0。
     */
    public void setAnimDuration(int duration) {
        if (duration < 0) {
            duration = 0;
        }

        mDuration = duration;
        mAnimator.setDuration(mDuration);
    }

    /**
     * 设置背景闪动动画的插值器。
     *
     * @param interpolator 背景闪动动画的插值器，不能为 null。
     */
    public void setAnimInterpolator(@NonNull TimeInterpolator interpolator) {
        NonNullUtil.requireNonNull(interpolator);
        mAnimator.setInterpolator(interpolator);
    }

    public void scrollToPosition(int position) {
        mPosition = position;
        mExecutedScrollToPosition = true;

        if (isViewHolderVisible()) {
            mExecutedScrollToPosition = false;
            startBackgroundAnim();
        }

        mRecyclerView.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        mExecutedScrollToPosition = false;

        mPosition = position;
        if (isViewHolderVisible()) {
            startBackgroundAnim();
        }

        mRecyclerView.smoothScrollToPosition(position);
    }

    // ***************************************private************************************

    private void initAnimator() {
        mAnimator = ValueAnimator.ofInt(0, 255);
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(1);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();

                mColorDrawable.setAlpha(currentValue);
                mColorDrawable.invalidateSelf();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                restoreTargetViewBackground();
                mTargetView = null;
            }
        });
    }

    private void initTargetViewDrawable() {
        mTargetViewBackground = mTargetView.getBackground();


        if (mTargetViewBackground == null) {
            mTargetView.setBackground(mColorDrawable);
            return;
        }

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{mTargetViewBackground, mColorDrawable});
        mTargetView.setBackground(layerDrawable);
    }

    private void startBackgroundAnim() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }

        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mPosition);
        mPosition = NO_POSITION;

        if (holder == null) {
            return;
        }

        mTargetView = holder.itemView;

        initTargetViewDrawable();
        mAnimator.start();
    }

    private void restoreTargetViewBackground() {
        if (mTargetView == null) {
            return;
        }

        mTargetView.setBackground(mTargetViewBackground);
    }

    private boolean isViewHolderVisible() {
        return mRecyclerView.findViewHolderForAdapterPosition(mPosition) != null;
    }
}
