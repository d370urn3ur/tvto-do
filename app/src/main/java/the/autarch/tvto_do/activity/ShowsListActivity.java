package the.autarch.tvto_do.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import roboguice.util.temp.Ln;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.event.NetworkEvent;
import the.autarch.tvto_do.event.UpdateExpiredExtendedInfoEvent;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.network.ExtendedInfoRequest;
import the.autarch.tvto_do.network.ExtendedInfoRequestListener;

public class ShowsListActivity extends BaseSpiceActivity {

	public static final int LOADER_ID_SHOW = 1;

    private static final String STATE_KEY_QUERY = "ShowsSearchFragment.state_key_query";

    private MenuItem _searchItem;
    private String _lastQuery;

    private Subscription _subscription;
    private PublishSubject<Observable<String>> _searchTextEmitterSubject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shows_list);

        if(savedInstanceState != null) {
            _lastQuery = savedInstanceState.getString(STATE_KEY_QUERY);
        }

        _searchTextEmitterSubject = PublishSubject.create();
        _subscription = AndroidObservable.bindActivity(this, Observable.switchOnNext(_searchTextEmitterSubject))
                            .debounce(800, TimeUnit.MILLISECONDS, Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(_getSearchObserver());
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_subscription != null) {
            _subscription.unsubscribe();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_KEY_QUERY, _lastQuery);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shows_list, menu);
		
		_searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(_searchItem);
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(final String searchText) {
                _searchTextEmitterSubject.onNext(getSearchObservableFor(searchText));
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String searchText) {
                _searchTextEmitterSubject.onNext(getSearchObservableFor(searchText));
				return true;
			}
	    });

	    MenuItemCompat.setOnActionExpandListener(_searchItem, new OnActionExpandListener() {

            // When using the support library, the setOnActionExpandListener() method is
            // static and accepts the MenuItem object as an argument

	        @Override
	        public boolean onMenuItemActionCollapse(MenuItem item) {
	        	hideSearch();
	            return true;  // Return true to collapse action view
	        }

	        @Override
	        public boolean onMenuItemActionExpand(MenuItem item) {
	        	showSearch();
	            return true;  // Return true to expand action view
	        }
	    });

        if(!TextUtils.isEmpty(_lastQuery)) {
            MenuItemCompat.expandActionView(_searchItem);
            searchView.setQuery(_lastQuery, false);
        }
	    
	    return super.onCreateOptionsMenu(menu);
	}

    private void hideSearch() {
        _lastQuery = null;
        Fragment searchFrag = getSupportFragmentManager().findFragmentByTag(ShowsSearchFragment.class.getName());
        if(searchFrag != null) {
            getSupportFragmentManager().popBackStack();
        }
	}
	
	private void showSearch() {
        Fragment searchFrag = getSupportFragmentManager().findFragmentByTag(ShowsSearchFragment.class.getName());
        if(searchFrag == null) {
            searchFrag = Fragment.instantiate(this, ShowsSearchFragment.class.getName());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, searchFrag, searchFrag.getClass().getName())
                    .addToBackStack(null)
                    .commit();
        }
	}

    private void searchForText(String query) {
        ShowsSearchFragment searchFrag = (ShowsSearchFragment)getSupportFragmentManager().findFragmentByTag(ShowsSearchFragment.class.getName());
        if(searchFrag != null) {
            _lastQuery = query;
            searchFrag.searchForText(query);
        }
    }

    public void onEventMainThread(UpdateExpiredExtendedInfoEvent ev) {
        List<Show> shows = ev.getExpiredShows();
        for(Show s : shows) {
            ExtendedInfoRequest req = new ExtendedInfoRequest(s.getTvrageId());
            String cacheKey = req.createCacheKey();
            getTvRageManager().execute(req, cacheKey, DurationInMillis.ONE_MINUTE, new ExtendedInfoRequestListener(s));
        }
    }

    public void onEventMainThread(NetworkEvent ev) {
        int length = ev.isSuccess() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(this, ev.getMessage(), length).show();
    }

    // -----------------------------------------------------------------------------------
    // Main Rx entities
    private Observer<String> _getSearchObserver() {
        return new Observer<String>() {
            @Override
            public void onCompleted() {
                Ln.d("--------- onComplete");
            }
            @Override
            public void onError(Throwable e) {
                Ln.e(e, "--------- Woops on error!");
            }
            @Override
            public void onNext(String searchText) {
                Ln.d(String.format("onNext You searched for %s", searchText));
                searchForText(searchText);
                onCompleted();
            }
        };
    }

    /**
     * @param searchText search text entered onTextChange
     * @return a new observable which searches for text searchText, explicitly say you want subscription to be done on a a non-UI thread, otherwise it'll default to the main thread.
     */
    private Observable<String> getSearchObservableFor(final String searchText) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Ln.d("----------- inside the search observable");
                subscriber.onNext(searchText);
                // subscriber.onCompleted(); This seems to have no effect.
            }
        }).subscribeOn(Schedulers.io());
    }
}
