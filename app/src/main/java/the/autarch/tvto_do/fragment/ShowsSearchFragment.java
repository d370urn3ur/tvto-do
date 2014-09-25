package the.autarch.tvto_do.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.adapter.SearchResultAdapter;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.gson.SearchResultGson;
import the.autarch.tvto_do.network.SearchRequest;

public class ShowsSearchFragment extends BaseSpiceFragment implements ActionMode.Callback {

	private SearchResultAdapter _searchAdapter;
	private ActionMode _actionMode;

    @InjectView(android.R.id.list) ListView _listView;
    @InjectView(android.R.id.empty) View _emptyView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shows_search, container, false);
        ButterKnife.inject(this, v);
		return v;
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _searchAdapter = new SearchResultAdapter(getActivity(), R.layout.search_cell);
        _searchAdapter.setNotifyOnChange(true);

        _listView.setEmptyView(_emptyView);
        _listView.setAdapter(_searchAdapter);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @OnItemClick(android.R.id.list)
    void onItemSelected(int position) {

        _searchAdapter.toggleExpandedCell(position);
        updateVisibleCells();

        if (_actionMode != null) {
            int selectedPosition = (Integer) _actionMode.getTag();
            if (selectedPosition == position) {
                _actionMode.finish();
                _actionMode = null;
            } else {
                _actionMode.setTag(position);
            }
            return;
        }

        // Start the CAB using the ActionMode.Callback defined above
        _actionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(ShowsSearchFragment.this);
        _actionMode.setTag(position);
    }
	
	public void searchForText(String searchText) {

        if(searchText.length() == 0) {
            _searchAdapter.clear();
            return;
        }

        SearchRequest req = new SearchRequest(searchText);
        String cacheKey = req.createCacheKey();

        getTraktManager().cancel(SearchResultGson.class, cacheKey);

        getTraktManager().execute(req, cacheKey, DurationInMillis.ONE_MINUTE, new ListSearchRequestListener());
	}

    private void updateVisibleCells() {
        final int first = _listView.getFirstVisiblePosition();
        final int last = _listView.getLastVisiblePosition();
        for(int i = first; i <= last; ++i) {
            View cell = _listView.getChildAt(i - first);
            _searchAdapter.getView(i, cell, _listView);
        }
    }

	private void addSearchResultToList(SearchResultGson searchResult) {
        Show show = searchResult.toShow();
        TVTDApplication.model().getShowDao().createInBackground(show);
	}

    public final class ListSearchRequestListener implements RequestListener<SearchResultGson.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            _searchAdapter.clear();
        }

        @Override
        public void onRequestSuccess(SearchResultGson.List searchResultJsons) {
            _searchAdapter.swapData(searchResultJsons);
        }
    }

    /**
     * Action Item methods
     */

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                int selectedPosition = (Integer)_actionMode.getTag();
                SearchResultGson searchResult = _searchAdapter.getItem(selectedPosition);
                addSearchResultToList(searchResult);
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.search_cell_context, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        _actionMode = null;
        _searchAdapter.toggleExpandedCell(-1);
        updateVisibleCells();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // do something here if necessary
        // return false if nothing done?
        return false;
    }
}
