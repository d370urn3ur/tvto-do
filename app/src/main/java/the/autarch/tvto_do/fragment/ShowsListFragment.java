package the.autarch.tvto_do.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.adapter.ShowAdapter;
import the.autarch.tvto_do.model.ExtendedInfo;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.network.ApiManager;
import the.autarch.tvto_do.util.CBLLiveQueryChangeEventObservable;

public class ShowsListFragment extends BaseInjectableFragment implements ActionMode.Callback {
	
	private ShowAdapter _showAdapter;
	private ActionMode _actionMode;

    private LiveQuery _showsQuery;

    @Inject Database _database;

    @InjectView(R.id.shows_recycler_view) RecyclerView _recyclerView;

    private CompositeSubscription _extendedInfoSubscriptions = new CompositeSubscription();
    private CompositeSubscription _uiSubscriptions = new CompositeSubscription();

    @Override
    public void onDestroy() {
        super.onDestroy();

        _extendedInfoSubscriptions.clear();
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
		
		_showAdapter = new ShowAdapter(getActivity(), _selector);

        _recyclerView.setHasFixedSize(true);
        _recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        _recyclerView.setAdapter(_showAdapter);
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inject();
    }

    @Override
    public void onResume() {
        super.onResume();

        resumeUi();
    }

    @Override
    public void onPause() {
        super.onPause();

        pauseUi();
    }

    private void resumeUi() {
        if(_showsQuery == null) {
            _showsQuery = _database.getView("shows").createQuery().toLiveQuery();
        }

        _uiSubscriptions.add(AndroidObservable.bindFragment(this, Observable.create(new CBLLiveQueryChangeEventObservable(_showsQuery))
                        .map(new Func1<LiveQuery.ChangeEvent, List<Show>>() {
                            @Override
                            public List<Show> call(LiveQuery.ChangeEvent changeEvent) {
                                List<Show> results = new ArrayList<Show>();
                                for (Iterator<QueryRow> it = changeEvent.getRows(); it.hasNext();) {
                                    QueryRow next = it.next();
                                    Map<String, Object> properties = (Map<String, Object>)next.getValue();
                                    results.add(new Show(properties));
                                }
                                return results;
                            }
                        })
                )
                .subscribe(new Action1<List<Show>>() {
                    @Override
                    public void call(List<Show> shows) {
                        _showAdapter.swapData(shows);
                    }
                })
        );

        _showsQuery.addChangeListener(new LiveQuery.ChangeListener() {
            @Override
            public void changed(final LiveQuery.ChangeEvent changeEvent) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        _showsQuery.start();
    }

    private void pauseUi() {
        _uiSubscriptions.clear();
    }

    public interface ShowSelector {
        public void onShowSelected(int position);
    }

    private ShowSelector _selector = new ShowSelector() {
        @Override
        public void onShowSelected(int position) {
            _showAdapter.expandPosition(position);
            _showAdapter.notifyDataSetChanged();

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
    };

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int selectedPos = (Integer)_actionMode.getTag();
        Show show = _showAdapter.getItem(selectedPos);
		
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
        _showAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}
	
	private void removeShowFromList(Show show) {
        Document document = _database.getDocument(show.getId());
        try {
            document.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

	private void refreshShowExtendedInfo(final Show show) {

        _extendedInfoSubscriptions.add(AndroidObservable.bindFragment(this, ApiManager.getExtendedInfo(show.tvrageId))
                .subscribe(new Action1<ExtendedInfo>() {
                    @Override
                    public void call(ExtendedInfo extendedInfo) {
                        show.updateExtendedInfo(extendedInfo);
                        Document document = _database.getDocument(show.getId());
                        try {
                            document.putProperties(show);
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
	}
}
