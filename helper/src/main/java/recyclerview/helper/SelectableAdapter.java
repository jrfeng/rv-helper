package recyclerview.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

    public void setSelectMode(int mode) {
        mSelectableHelper.setSelectMode(mode);
    }

    public boolean isSelected(int position) {
        return mSelectableHelper.isSelected(position);
    }

    public void clearSelected() {
        mSelectableHelper.clearSelected();
    }

    public void setSelect(int position, boolean select) {
        mSelectableHelper.setSelect(position, select);
    }

    public List<Integer> getSelectedPositions() {
        return mSelectableHelper.getSelectedPositions();
    }

    public void setOnItemClickListener(ItemClickHelper.OnItemClickListener listener) {
        mSelectableHelper.setOnItemClickListener(listener);
    }
}
