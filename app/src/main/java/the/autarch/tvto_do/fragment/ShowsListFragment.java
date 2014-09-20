package the.autarch.tvto_do.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.activity.ShowsListActivity;
import the.autarch.tvto_do.adapter.ShowAdapter;
import the.autarch.tvto_do.event.UpdateExtendedInfoEvent;
import the.autarch.tvto_do.model.ExtendedInfoWrapper;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.network.ExtendedInfoRequest;
import the.autarch.tvto_do.provider.ShowContract;

public class ShowsListFragment extends BaseSpiceFragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback {
	
	private ShowAdapter _showAdapter;
	private ActionMode _actionMode;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shows_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
		
		_showAdapter = new ShowAdapter(getActivity());
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		lv.setAdapter(_showAdapter);
		lv.setEmptyView(getView().findViewById(android.R.id.empty));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View cell, int position, long id) {
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
		});

		getLoaderManager().initLoader(ShowsListActivity.LOADER_ID_SHOW, null, this);
	}

    @Override
    public void onResume() {
        super.onResume();
        updateVisibleCells();
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
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        return new CursorLoader(getActivity(),
                ShowContract.ShowColumns.CONTENT_URI,
                ShowContract.allShowsProjection,
                null,
                null,
                ShowContract.ShowColumns.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		_showAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		_showAdapter.swapCursor(null);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int selectedPos = (Integer)_actionMode.getTag();
        Cursor c = (Cursor)_showAdapter.getItem(selectedPos);
		Show show = new Show(c);
		
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
        ContentResolver cr = getActivity().getContentResolver();
        cr.delete(ShowContract.ShowColumns.contentItemUriFor(show.getId()), null, null);
        FileManager.getInstance().deleteBitmapForFilename(show.getPoster138filepath());
	}

    public void onEvent(UpdateExtendedInfoEvent ev) {
        Show s = ev.getShow();
        refreshShowExtendedInfo(s);
    }
	
	private void refreshShowExtendedInfo(Show show) {
        ExtendedInfoRequest req = new ExtendedInfoRequest(show.getTvrageId());
        String cacheKey = req.createCacheKey();
        getRageManager().execute(req, cacheKey, DurationInMillis.ONE_MINUTE, new ExtendedInfoRequestListener());
	}

    class ExtendedInfoRequestListener implements RequestListener<ExtendedInfoWrapper> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(getClass().getSimpleName(), "got error in extended info request: " + spiceException);
        }

        @Override
        public void onRequestSuccess(ExtendedInfoWrapper extendedInfoWrapper) {

            ContentValues cvs = new ContentValues();

            if(extendedInfoWrapper.hasInfo()) {
                cvs.put(ShowContract.ShowColumns.NEXT_EPISODE_TITLE, extendedInfoWrapper.nextEpisodeTitle);
                cvs.put(ShowContract.ShowColumns.NEXT_EPISODE_TIME, extendedInfoWrapper.nextEpisodeTime.toMillis(false));
            }

            cvs.put(ShowContract.ShowColumns.EXTENDED_INFO_UPDATED, true);

            ContentResolver cr = getActivity().getContentResolver();
            cr.update(
                    ShowContract.ShowColumns.CONTENT_URI,
                    cvs,
                    ShowContract.ShowColumns.TVRAGE_ID + "= ?",
                    new String[] { extendedInfoWrapper.tvRageId }
            );
        }
    }

}
