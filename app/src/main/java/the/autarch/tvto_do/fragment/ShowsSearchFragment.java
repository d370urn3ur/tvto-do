package the.autarch.tvto_do.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.adapter.SearchResultAdapter;
import the.autarch.tvto_do.model.SearchResultJson;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.network.SearchRequest;
import the.autarch.tvto_do.model.ShowContract;

public class ShowsSearchFragment extends BaseSpiceFragment implements ActionMode.Callback {

	private SearchResultAdapter _searchAdapter;
	private ActionMode _actionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shows_search, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		if(_searchAdapter == null) {
			_searchAdapter = new SearchResultAdapter(getActivity(), R.layout.search_cell);
			_searchAdapter.setNotifyOnChange(true);
		}
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		lv.setEmptyView(getView().findViewById(android.R.id.empty));
		lv.setAdapter(_searchAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View cell, int position, long id) {
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
        });
		
		super.onActivityCreated(savedInstanceState);
	}
	
	public void searchForText(String searchText) {

        if(searchText.length() == 0) {
            _searchAdapter.empty();
            return;
        }

        SearchRequest req = new SearchRequest(searchText);
        String cacheKey = req.createCacheKey();

        getTraktManager().cancel(SearchResultJson.class, cacheKey);

        getTraktManager().execute(req, cacheKey, DurationInMillis.ONE_MINUTE, new ListSearchRequestListener());
	}

    private void updateVisibleCells() {
        ListView lv = (ListView)getView().findViewById(android.R.id.list);
        final int first = lv.getFirstVisiblePosition();
        final int last = lv.getLastVisiblePosition();
        for(int i = first; i <= last; ++i) {
            View cell = lv.getChildAt(i - first);
            _searchAdapter.getView(i, cell, lv);
        }
    }

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_add:
                int selectedPosition = (Integer)_actionMode.getTag();
				SearchResultJson searchResult = _searchAdapter.getItem(selectedPosition);
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
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// do something here if necessary
		// return false if nothing done? 
		return false;
	}
	
	private void addSearchResultToList(SearchResultJson searchResult) {
        Show show = searchResult.toShow();
        TVTDApplication.model().getShowDao().createInBackground(show);
	}

    public final class ListSearchRequestListener implements RequestListener<SearchResultJson.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(getClass().getSimpleName(), "got request failure: " + spiceException);
            _searchAdapter.empty();
        }

        @Override
        public void onRequestSuccess(SearchResultJson.List searchResultJsons) {
            Log.e(getClass().getSimpleName(), "got request success");
            _searchAdapter.empty();
            _searchAdapter.supportAddAll(searchResultJsons);
        }
    }
}
