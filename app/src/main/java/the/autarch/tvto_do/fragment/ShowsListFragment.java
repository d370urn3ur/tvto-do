package the.autarch.tvto_do.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.activity.ShowsListActivity;
import the.autarch.tvto_do.adapter.ShowAdapter;
import the.autarch.tvto_do.event.ShowCreatedEvent;
import the.autarch.tvto_do.loader.ShowLoader;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.gson.ExtendedInfoGson;
import the.autarch.tvto_do.network.ApiManager;

public class ShowsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Show>>, ActionMode.Callback {
	
	private ShowAdapter _showAdapter;
	private ActionMode _actionMode;

    @InjectView(android.R.id.list) ListView _listView;
    @InjectView(android.R.id.empty) View _emptyView;

    private Queue<Subscription> _extInfoSubscriptions = new LinkedList<Subscription>();

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        while(_extInfoSubscriptions.size() > 0) {
            Subscription s = _extInfoSubscriptions.remove();
            s.unsubscribe();
        }
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shows_list, container, false);
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
		
		_showAdapter = new ShowAdapter(getActivity());

		_listView.setAdapter(_showAdapter);
		_listView.setEmptyView(_emptyView);

        getLoaderManager().initLoader(ShowsListActivity.LOADER_ID_SHOW, null, this);
	}

    @Override
    public void onResume() {
        super.onResume();
        updateVisibleCells();
    }

    @OnItemClick(android.R.id.list)
    public void onItemSelected(int position) {
        _showAdapter.expandPosition(position);
        updateVisibleCells();

        if(_actionMode != null) {
            int selectedPos = (Integer)_actionMode.getTag();
            if(selectedPos == position) {
                _actionMode.finish();
                _actionMode = null;
            } else {
                _actionMode.setTag(position);
            }
            return;
        }

        // Start the CAB using the ActionMode.Callback defined above
        _actionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(ShowsListFragment.this);
        _actionMode.setTag(position);
    }
	
	private void updateVisibleCells() {
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		final int first = lv.getFirstVisiblePosition();
		final int last = lv.getLastVisiblePosition();
		for(int i = first; i <= last; ++i) {
			View cell = lv.getChildAt(i - first);
			_showAdapter.getView(i, cell, lv);
		}
	}

	@Override
	public Loader<List<Show>> onCreateLoader(int loaderId, Bundle args) {
        return new ShowLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Show>> loader, List<Show> data) {
		_showAdapter.swapData(data);
	}

	@Override
	public void onLoaderReset(Loader<List<Show>> loader) {
		_showAdapter.swapData(null);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int selectedPos = (Integer)_actionMode.getTag();
        Show show = (Show)_showAdapter.getItem(selectedPos);
		
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
        _showAdapter.expandPosition(-1);
        updateVisibleCells();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}
	
	private void removeShowFromList(Show show) {
        TVTDApplication.model().getShowDao().deleteInBackground(show);
	}

    public void onEventMainThread(ShowCreatedEvent event) {
        Show show = event.getShow();
        if(show.getExtendedInfoStatus() == Show.ExtendedInfoStatus.EXTENDED_INFO_UNKNOWN) {
            refreshShowExtendedInfo(event.getShow());
        }
    }

	private void refreshShowExtendedInfo(Show show) {

        Subscription s = AndroidObservable.bindFragment(this, ApiManager.getExtendedInfo(show.getTvrageId()))
                .subscribe(new Action1<ExtendedInfoGson>() {
                    @Override
                    public void call(ExtendedInfoGson extendedInfoGson) {
                        TVTDApplication.model().getShowDao().updateExtendedInfoInBackground(extendedInfoGson);
                    }
                });

        _extInfoSubscriptions.add(s);
	}
}
