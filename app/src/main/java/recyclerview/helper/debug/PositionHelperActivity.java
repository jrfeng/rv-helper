package recyclerview.helper.debug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import recyclerview.helper.PositionHelper;

public class PositionHelperActivity extends AppCompatActivity {
    private PositionAdapter mPositionAdapter;
    private List<String> mItems;

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_helper);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initItems();
        mPositionAdapter = new PositionAdapter(mItems);
        recyclerView.setAdapter(mPositionAdapter);
    }

    private void initItems() {
        mItems = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            mItems.add("Item" + i);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addItems();
                break;
            case R.id.btnRemove:
                removeItems();
                break;
            case R.id.btnMove:
                moveItems();
                break;
        }
    }

    private void addItems() {
        int end = i + 5;
        for (; i < end; i++) {
            mItems.add(2, "New item: " + i);
        }
        mPositionAdapter.setItems(mItems);
    }

    private void removeItems() {
        if (mItems.size() <= 5) {
            Toast.makeText(this, "Ignore, too few items", Toast.LENGTH_SHORT).show();
            return;
        }

        mItems.subList(2, 7).clear();
        mPositionAdapter.setItems(mItems);
    }

    private void moveItems() {
        String buf1 = mItems.remove(1);     // 1
        String buf2 = mItems.remove(1);     // 2
        mItems.add(7, buf1);
        mItems.add(8, buf2);
        mPositionAdapter.setItems(mItems);
    }

    private static class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.ViewHolder> {
        private List<String> mItems;
        private PositionHelper<ViewHolder> mPositionHelper;

        PositionAdapter(@NonNull List<String> items) {
            mItems = new ArrayList<>(items);
            mPositionHelper = new PositionHelper<>(this);
        }

        public void setItems(@NonNull final List<String> items) {
            DiffUtil.Callback callback = new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mItems.size();
                }

                @Override
                public int getNewListSize() {
                    return items.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mItems.get(oldItemPosition).equals(items.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return areItemsTheSame(oldItemPosition, newItemPosition);
                }
            };

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            result.dispatchUpdatesTo(this);
            mItems = new ArrayList<>(items);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mPositionHelper.attachToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mPositionHelper.detach();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_position_helper, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvPosition.setText(String.valueOf(position));
            holder.tvTitle.setText(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder
                implements PositionHelper.OnPositionChangeListener {
            TextView tvPosition;
            TextView tvTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvPosition = itemView.findViewById(R.id.tvPosition);
                tvTitle = itemView.findViewById(R.id.tvTitle);
            }

            @Override
            public void onPositionChanged(int oldPosition, int newPosition) {
                tvPosition.setText(String.valueOf(newPosition));
            }
        }
    }
}