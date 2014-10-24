package the.autarch.tvto_do.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.event.NetworkEvent;
import the.autarch.tvto_do.event.UpdateExpiredExtendedInfoEvent;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.gson.ExtendedInfoGson;
import the.autarch.tvto_do.network.ApiManager;
import the.autarch.tvto_do.rx.TVTDViewObservable;

public class ShowsListActivity extends BaseEventActivity {

	public static final int LOADER_ID_SHOW = 1;

    private static final String STATE_KEY_QUERY = "ShowsSearchFragment.state_key_query";

    private String _lastQuery;

    private Subscription _subscription;
    private Subscription _collapseSubscription;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shows_list);

        if(savedInstanceState != null) {
            _lastQuery = savedInstanceState.getString(STATE_KEY_QUERY);
        }
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_subscription != null) {
            _subscription.unsubscribe();
            _subscription = null;
        }

        if(_collapseSubscription != null) {
            _collapseSubscription.unsubscribe();
            _collapseSubscription = null;
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
		
		MenuItem searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        Observable<String> searchObservable = TVTDViewObservable.searchText(searchView)
            .debounce(800, TimeUnit.MILLISECONDS)
            .map(new Func1<SearchView, String>() {
                @Override
                public String call(SearchView searchView) {
                    return searchView.getQuery().toString();
                }
            });

        _subscription = AndroidObservable.bindActivity(this, searchObservable)
            .subscribe(new Action1<String>() {
                @Override
                public void call(String queryText) {
                    searchForText(queryText);
                }
            });

        boolean initiallyCollapsed = TextUtils.isEmpty(_lastQuery);

        Observable<Boolean> collapsedEvent = TVTDViewObservable.collapsed(searchItem, initiallyCollapsed);
        _collapseSubscription = AndroidObservable.bindActivity(this, collapsedEvent)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean collapsed) {
                        if (collapsed) {
                            hideSearch();
                        } else {
                            showSearch();
                        }
                    }
                });

        if(!TextUtils.isEmpty(_lastQuery)) {
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

        AndroidObservable.bindActivity(this, Observable.from(ev.getExpiredShows())
                .flatMap(new Func1<Show, Observable<ExtendedInfoGson>>() {
                    @Override
                    public Observable<ExtendedInfoGson> call(Show show) {
                        return ApiManager.getExtendedInfo(show.getTvrageId());
                    }
                }))
                .forEach(new Action1<ExtendedInfoGson>() {
                    @Override
                    public void call(ExtendedInfoGson extendedInfoGson) {
                        TVTDApplication.model().getShowDao().updateExtendedInfoInBackground(extendedInfoGson);
                        EventBus.getDefault().post(new NetworkEvent(NetworkEvent.NetworkEventType.SUCCESS, "Finished updating show"));
                    }
                });
    }

    public void onEventMainThread(NetworkEvent ev) {
        int length = ev.isSuccess() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(this, ev.getMessage(), length).show();
    }
}
