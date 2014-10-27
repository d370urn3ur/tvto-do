package the.autarch.tvto_do.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.adapter.SearchResultAdapter;
import the.autarch.tvto_do.model.SearchResultGson;
import the.autarch.tvto_do.network.ApiManager;

public class ShowsSearchFragment extends BaseInjectableFragment implements ActionMode.Callback {

	private SearchResultAdapter _searchAdapter;
	private ActionMode _actionMode;

    private Subscription _searchSubscription;

    @Inject Database _database;

    @InjectView(R.id.search_recycler_view) RecyclerView _recyclerView;

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

        _searchAdapter = new SearchResultAdapter(getActivity(), R.layout.search_cell, _searchSelector);

        _recyclerView.setHasFixedSize(true);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recyclerView.setAdapter(_searchAdapter);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        inject();
	}

    @Override
    public void onStop() {
        super.onStop();

        if(_searchSubscription != null && !_searchSubscription.isUnsubscribed()) {
            _searchSubscription.unsubscribe();
            _searchSubscription = null;
        }
    }

    public interface SearchSelector {
        public void onSearchResultSelected(int position);
    }

    private SearchSelector _searchSelector = new SearchSelector() {
        @Override
        public void onSearchResultSelected(int position) {

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
    };
	
	public void searchForText(String searchText) {

        if(searchText.length() == 0) {
            _searchAdapter.swapData(new ArrayList<SearchResultGson>());
            return;
        }

        if(_searchSubscription != null) {
            _searchSubscription.unsubscribe();
        }
        _searchSubscription = AndroidObservable.bindFragment(this, ApiManager.searchForShow(searchText))
                .subscribe(new Action1<SearchResultGson.List>() {
                    @Override
                    public void call(SearchResultGson.List searchResultGsons) {
                        _searchAdapter.swapData(searchResultGsons);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        _searchAdapter.swapData(new ArrayList<SearchResultGson>());
                        Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_LONG).show();
                    }
                });
	}

	private void addSearchResultToList(SearchResultGson searchResult) {
        Document document = _database.createDocument();
        Map<String, Object> props = searchResult.getDocumentProperties();
        try {
            document.putProperties(props);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
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
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }
}
