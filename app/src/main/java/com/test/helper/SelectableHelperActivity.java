package com.test.helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import recyclerview.helper.ItemClickHelper;
import recyclerview.helper.SelectableAdapter;
import recyclerview.helper.SelectableHelper;

public class SelectableHelperActivity extends AppCompatActivity {
    private List<String> mItems;
    private TestSelectableAdapter mAdapter;

    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectable_helper);

        mItems = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            mItems.add(String.valueOf(i));
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new TestSelectableAdapter(mItems);
        recyclerView.setAdapter(mAdapter);

        RadioGroup rgMode = findViewById(R.id.rgMode);
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.single:
                        mAdapter.setSelectMode(SelectableHelper.SelectMode.SINGLE);
                        break;
                    case R.id.multiple:
                        mAdapter.setSelectMode(SelectableHelper.SelectMode.MULTIPLE);
                        break;
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                // 在索引 2 处添加 5 个元素
                mItems.addAll(2, generateItems());
                mAdapter.setItems(mItems);
                break;
            case R.id.btnRemove:
                // 从索引 0 处移除 5 个元素
                for (int i = 0; i < 5 && i < mItems.size(); i++) {
                    mItems.remove(0);
                }
                mAdapter.setItems(mItems);
                break;
            case R.id.btnMove:
                // 交换索引为 0 和 5 的元素
                if (mItems.size() < 3) {
                    Toast.makeText(this, "ignore: item < 3", Toast.LENGTH_SHORT).show();
                    return;
                }
                String item = mItems.get(1);
                int targetIndex = Math.min(5, mItems.size() - 1);
                mItems.set(1, mItems.get(targetIndex));
                mItems.set(targetIndex, item);
                mAdapter.setItems(mItems);
                break;
        }
    }

    // *****************************private*********************************

    private List<String> generateItems() {
        List<String> items = new ArrayList<>(5);

        int end = mIndex + 5;
        for (; mIndex < end; mIndex++) {
            items.add("new item: " + mIndex);
        }

        return items;
    }

    public static class TestSelectableAdapter extends SelectableAdapter<TestSelectableAdapter.ViewHolder> {
        private List<String> mItems;

        public TestSelectableAdapter(List<String> items) {
            mItems = new ArrayList<>(items);

            setOnItemClickListener(new ItemClickHelper.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                    setSelect(position, !isSelected(position));
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selectable, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            holder.tvTitle.setText(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItems(List<String> items) {
            DiffCallback diffCallback = new DiffCallback(mItems, items);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback, true);
            result.dispatchUpdatesTo(this);
            mItems = new ArrayList<>(items);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements SelectableHelper.Selectable {
            public TextView tvTitle;

            private int mDefaultColor;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);

                mDefaultColor = tvTitle.getCurrentTextColor();
            }

            @Override
            public void onSelected() {
                tvTitle.setTextColor(Color.RED);
            }

            @Override
            public void onUnselected() {
                tvTitle.setTextColor(mDefaultColor);
            }
        }

        private static class DiffCallback extends DiffUtil.Callback {
            private List<String> mOldList;
            private List<String> mNewList;

            DiffCallback(List<String> oldList, List<String> newList) {
                mOldList = oldList;
                mNewList = newList;
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
                if (oldItemPosition > mOldList.size()
                        || newItemPosition > mNewList.size()) {
                    return false;
                }

                return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
            }
        }
    }
}
