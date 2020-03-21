package com.test.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import recyclerview.helper.ItemClickHelper;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private Context mContext;
    private int mSize;
    private ItemClickHelper mItemClickHelper;

    public TestAdapter(Context context, int size) {
        mContext = context;
        mSize = size;
        mItemClickHelper = new ItemClickHelper();

        mItemClickHelper.setOnItemClickListener(new ItemClickHelper.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                switch (viewId) {
                    case R.id.item:
                        Toast.makeText(mContext, "ItemClicked, position: " + position, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button:
                        Toast.makeText(mContext, "ButtonClicked, position: " + position, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mItemClickHelper.setOnItemLongClickListener(new ItemClickHelper.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                switch (viewId) {
                    case R.id.item:
                        Toast.makeText(mContext, "ItemLongClicked, position: " + position, Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.button:
                        Toast.makeText(mContext, "ButtonLongClicked, position: " + position, Toast.LENGTH_SHORT).show();
                        return true;
                }

                return false;
            }
        });
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mItemClickHelper.bindClickListener(holder.itemView, holder.button);
        mItemClickHelper.bindLongClickListener(holder.itemView, holder.button);

        holder.tvText.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return mSize;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvText = itemView.findViewById(R.id.tvText);
            button = itemView.findViewById(R.id.button);
        }
    }
}
