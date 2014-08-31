package the.autarch.tvto_do.activity;

import java.util.List;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.adapter.ShowAdapter;
import the.autarch.tvto_do.loader.SQLiteShowDataLoader;
import the.autarch.tvto_do.model.DataManager;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.model.ShowDataSource;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
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

public class ShowsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Show>>, ActionMode.Callback {
	
	private ShowAdapter _showAdapter;
	private ActionMode _actionMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shows_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		_showAdapter = new ShowAdapter(getActivity(), R.layout.show_cell);
		_showAdapter.setNotifyOnChange(true);
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		lv.setAdapter(_showAdapter);
		lv.setEmptyView(getView().findViewById(android.R.id.empty));
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View cell, int position, long id) {
				
				if(_actionMode != null) {
					return false;
				}
				
				// Start the CAB using the ActionMode.Callback defined above
				_actionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(ShowsListFragment.this);
				Show item = _showAdapter.getItem(position);
				_actionMode.setTag(item);
		        return true;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View cell, int position, long id) {
				_showAdapter.toggleExpandedCell(Integer.valueOf(position));
				updateItemAtPosition(position);
			}
		});
		
		getLoaderManager().initLoader(ShowsListActivity.LOADER_ID_SHOW, null, this);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	private void updateItemAtPosition(int position) {
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
	    int visiblePosition = lv.getFirstVisiblePosition();
	    View view = lv.getChildAt(position - visiblePosition);
	    lv.getAdapter().getView(position, view, lv);
	}
	
	private void updateVisibleCells() {
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		int first = lv.getFirstVisiblePosition();
		int last = lv.getLastVisiblePosition();
		for(int i = first, j = last; i <= j; ++i) {
			View cell = lv.getChildAt(i - first);
			_showAdapter.getView(i, cell, lv);
		}
	}

	@Override
	public Loader<List<Show>> onCreateLoader(int loaderId, Bundle args) {
		ShowDataSource dataSource = DataManager.getInstance().getShowDataSource();
		SQLiteShowDataLoader loader = new SQLiteShowDataLoader(getActivity(), dataSource, null, null, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<Show>> loader, List<Show> data) {
		_showAdapter.supportAddAll(data);
		updateVisibleCells();
	}

	@Override
	public void onLoaderReset(Loader<List<Show>> loader) {
		_showAdapter.empty();
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		
		Show show = (Show)_actionMode.getTag();
		
		switch(item.getItemId()) {
			case R.id.action_remove:
				removeShowFromList(show);
				mode.finish();
				return true;
			
			case R.id.action_refresh:
				refreshShowExtendedInfo(show);
				mode.finish();
				return true;
		}
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.show_cell_context, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		_actionMode = null;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}
	
	private void removeShowFromList(Show show) {
		ShowDataSource showDataSource = DataManager.getInstance().getShowDataSource();
		showDataSource.delete(show);
	}
	
	private void refreshShowExtendedInfo(Show show) {
		_showAdapter.getExtendedInfoForShow(show);
	}
	
//	updateShowWithImages();

}
