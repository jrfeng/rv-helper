package recyclerview.helper.debug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import recyclerview.helper.ItemClickHelper;
import recyclerview.helper.ItemClickableAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<TestListAdapter.Item> itemList = new ArrayList<>();

        itemList.add(new TestListAdapter.Item("ItemClickHelper", ItemClickHelperActivity.class));
        itemList.add(new TestListAdapter.Item("SelectableHelper", SelectableHelperActivity.class));
        itemList.add(new TestListAdapter.Item("ScrollToPositionHelper", ScrollToPositionHelperActivity.class));
        itemList.add(new TestListAdapter.Item("PositionHelper", PositionHelperActivity.class));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TestListAdapter(this, itemList));
    }

    private static class TestListAdapter extends ItemClickableAdapter<TestListAdapter.ViewHolder> {
        private Context mContext;
        private List<Item> mItemList;

        TestListAdapter(Context context, List<Item> itemList) {
            mContext = context;
            mItemList = new ArrayList<>(itemList);

            setOnItemClickListener(new ItemClickHelper.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, int viewId, View view, RecyclerView.ViewHolder holder) {
                    Intent intent = new Intent(mContext, mItemList.get(position).activity);
                    mContext.startActivity(intent);
                }
            });
        }

        @NonNull
        @Override
        public TestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_main_activity, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            holder.text.setText(mItemList.get(position).title);
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.tvText);
            }
        }

        private static class Item {
            String title;
            Class<? extends Activity> activity;

            Item(String title, Class<? extends Activity> activity) {
                this.title = title;
                this.activity = activity;
            }
        }
    }
}
