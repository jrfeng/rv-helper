package recyclerview.helper.debug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import recyclerview.helper.ItemClickHelper;

public class ItemClickHelperActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_click_helper);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemClickHelperAdapter(50, (TextView) findViewById(R.id.tvMessage)));
    }

    public static class ItemClickHelperAdapter extends RecyclerView.Adapter<ItemClickHelperAdapter.ViewHolder> {
        private int mSize;
        private ItemClickHelper mItemClickHelper;
        private TextView tvMessage;

        ItemClickHelperAdapter(int size, TextView messageView) {
            mSize = size;
            mItemClickHelper = new ItemClickHelper();
            tvMessage = messageView;

            mItemClickHelper.setOnItemClickListener(new ItemClickHelper.OnItemClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onItemClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                    switch (viewId) {
                        case R.id.item:
                            tvMessage.setText("Item clicked, position: " + position);
                            break;
                        case R.id.button:
                            tvMessage.setText("Button clicked, position: " + position);
                            break;
                    }
                }
            });

            mItemClickHelper.setOnItemLongClickListener(new ItemClickHelper.OnItemLongClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public boolean onItemLongClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                    switch (viewId) {
                        case R.id.item:
                            tvMessage.setText("Item long clicked, position: " + position);
                            return true;
                        case R.id.button:
                            tvMessage.setText("Button long clicked, position: " + position);
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

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvText;
            Button button;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvText = itemView.findViewById(R.id.tvText);
                button = itemView.findViewById(R.id.button);
            }
        }
    }
}
