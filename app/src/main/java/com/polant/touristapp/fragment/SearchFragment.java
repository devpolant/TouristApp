package com.polant.touristapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.adapter.base.RecyclerClickListener;
import com.polant.touristapp.adapter.recycler.SearchMultiTypesAdapter;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IRecyclerFragment;
import com.polant.touristapp.interfaces.ISearchableFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.model.search.SearchComplexItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент, содержащий RecyclerView, который служит для вывода результата поиска.
 * Текущий Адаптер позволяет выводить различные типы представлений одновременно в RecyclerView.
 */
public class SearchFragment extends Fragment implements IRecyclerFragment, ISearchableFragment {

    public interface SearchFragmentListener {
        void showPhotosByMark(Mark mark);
        void showSelectedPhoto(UserMedia photo);
    }

    private static int LAYOUT = R.layout.fragment_search;

    protected Activity mActivity;

    protected View view;

    protected SearchMultiTypesAdapter mAdapter;

    protected int mUserId;

    protected Handler handler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IWorkWithDatabaseActivity) || !(context instanceof SearchFragmentListener)) {
            throw new IllegalArgumentException("ACTIVITY MUST IMPLEMENT IWorkWithDatabaseActivity and " +
                    "SearchFragmentListener");
        }
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDataFromArguments(getArguments());
        initAdapter();
        initRecycledView();

        //Сначала вывожу все данные.
        search("");
    }

    protected Database getDatabase() {
        return ((IWorkWithDatabaseActivity) mActivity).getDatabase();
    }

    protected void getDataFromArguments(Bundle args) {
        if (args != null) {
            mUserId = args.getInt(Constants.USER_ID);
        }
    }

    protected void initAdapter(){
        mAdapter = new SearchMultiTypesAdapter(mActivity, null, new RecyclerClickListener() {
            @Override
            public void onItemClicked(int position) {
                SearchComplexItem clicked = mAdapter.getItem(position);
                if (clicked.isMark()) {
                    ((SearchFragment.SearchFragmentListener) mActivity).showPhotosByMark(clicked.getMark());
                }
                else if(clicked.isUserMedia()){
                    ((SearchFragment.SearchFragmentListener) mActivity).showSelectedPhoto(clicked.getMedia());
                }
            }

            @Override
            public boolean onItemLongClicked(int position) {
                return false;
            }
        });
    }

    protected void initRecycledView(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void notifyRecyclerView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void search(final String filter) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Database db = getDatabase();
                List<Mark> marks = db.searchMarks(mUserId, filter);
                List<UserMedia> medias = db.searchUserMedia(mUserId, filter);

                List<SearchComplexItem> result = merge(marks, medias);
                mAdapter.changeItems(result);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyRecyclerView();
                    }
                });
            }
        });
        t.start();
    }

    private List<SearchComplexItem> merge(List<Mark> marks, List<UserMedia> medias) {
        int resultSize = marks.size() + medias.size();
        List<SearchComplexItem> result = new ArrayList<>(resultSize);

        for (int i = 0; i < resultSize; i++) {
            if (i < marks.size()){
                result.add(new SearchComplexItem(i, marks.get(i)));
            }else {
                result.add(new SearchComplexItem(i, medias.get(i - marks.size())));
            }
        }
        return result;
    }

}
