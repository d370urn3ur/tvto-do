package the.autarch.tvto_do.activity;

import java.util.Collection;
import java.util.HashMap;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.adapter.SearchResultAdapter;
import the.autarch.tvto_do.model.SearchResultWrapper;
import the.autarch.tvto_do.network.NetworkManager;
import the.autarch.tvto_do.network.SearchRequest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ShowsSearchFragment extends Fragment implements ActionMode.Callback {
	
	private SearchResultAdapter _searchAdapter;
	
	public interface Callback {
		public void userSelectedSearchResult(SearchResultWrapper searchResult);
	}
	
	private Callback _callback;
	private ActionMode _actionMode;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
            _callback = (Callback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ShowsSearchFragment.Callback");
        }
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		_callback = null;
	}
	
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
	public void onDestroyView() {
		super.onDestroyView();
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
				_searchAdapter.toggleExpandedCell(Integer.valueOf(position));
				updateItemAtPosition(position);
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View cell, int position, long id) {
				
				if(_actionMode != null) {
					return false;
				}
				
				// Start the CAB using the ActionMode.Callback defined above
				_actionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(ShowsSearchFragment.this);
				SearchResultWrapper item = _searchAdapter.getItem(position);
				_actionMode.setTag(item);
		        return true;
			}
		});
		
		super.onActivityCreated(savedInstanceState);
	}
	
	public void searchForText(String searchText) {
		
		final String searchRequestTag = "SearchTag";
		
		if(searchText.length() == 0) {
			_searchAdapter.empty();
			return;
		}
		
		RequestQueue queue = NetworkManager.getInstance().getRequestQueue();
		queue.cancelAll(searchRequestTag);
		
		SearchRequest request = SearchRequest.searchForText(searchText, new HashMap<String, String>(), new Listener<Collection<SearchResultWrapper>>() {
			@Override
			public void onResponse(Collection<SearchResultWrapper> response) {
				
				_searchAdapter.empty();
				_searchAdapter.supportAddAll(response);
			}
		},
		new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO: inform user (via empty view) that network is offline
				Log.d(this.getClass().getName(), "got error: " + error.toString());
			}
		});
		
		request.setTag(searchRequestTag);
		queue.add(request);
	}
	
	private void updateItemAtPosition(int position) {
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
	    int visiblePosition = lv.getFirstVisiblePosition();
	    View view = lv.getChildAt(position - visiblePosition);
	    lv.getAdapter().getView(position, view, lv);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_add:
				SearchResultWrapper searchResult = (SearchResultWrapper)_actionMode.getTag();
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
	
	private void addSearchResultToList(SearchResultWrapper searchResult) {
		_callback.userSelectedSearchResult(searchResult);
	}
}
