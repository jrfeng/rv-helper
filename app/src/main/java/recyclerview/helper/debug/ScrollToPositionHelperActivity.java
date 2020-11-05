package recyclerview.helper.debug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import recyclerview.helper.ScrollToPositionHelper;

public class ScrollToPositionHelperActivity extends AppCompatActivity {
    private ScrollToPositionHelper mScrollToPositionHelper;

    private static final int MAX_POSITION = 99;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_to_position_helper);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(MAX_POSITION + 1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mScrollToPositionHelper = new ScrollToPositionHelper(recyclerView);
        }
    }

    public void onClick(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(this, "Require API Level 16", Toast.LENGTH_SHORT).show();
            return;
        }

        mPosition += 5;

        if (mPosition >= MAX_POSITION) {
            mPosition = 0;
        }

        switch (view.getId()) {
            case R.id.btnScrollTo:
                mScrollToPositionHelper.scrollToPosition(mPosition);
                break;
            case R.id.btnSmoothScrollTo:
                mScrollToPositionHelper.smoothScrollToPosition(mPosition);
                break;
        }
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private int mSize;

        Adapter(int size) {
            mSize = size;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.text1.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mSize;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                text1 = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
