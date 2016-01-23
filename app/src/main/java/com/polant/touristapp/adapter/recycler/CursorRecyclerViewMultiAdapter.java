package com.polant.touristapp.adapter.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для множественного выбора элементов RecyclerView.
 */
public abstract class CursorRecyclerViewMultiAdapter<VH extends RecyclerView.ViewHolder> extends CursorRecyclerViewAdapter<VH> {

    private SparseBooleanArray selectedItems;

    public CursorRecyclerViewMultiAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        selectedItems = new SparseBooleanArray();
    }

    public boolean isSelectedPosition(int position) {
        return getSelectedItemsPositions().contains(position);
    }

    public boolean isSelectedId(long id) {
        return getSelectedItemsIds().contains(id);
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selectedPositions = getSelectedItemsPositions();
        selectedItems.clear();
        for (Integer i : selectedPositions) {
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItemsPositions() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public List<Long> getSelectedItemsIds(){
        List<Long> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(getItemId(selectedItems.keyAt(i)));
        }
        return items;
    }
}
