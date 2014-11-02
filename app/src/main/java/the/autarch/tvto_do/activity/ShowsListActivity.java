package the.autarch.tvto_do.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.View;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.rx.TVTDViewObservable;

public class ShowsListActivity extends BaseEventActivity {

    private static final String STATE_KEY_QUERY = "ShowsSearchFragment.state_key_query";

    private String _lastQuery;

    private CompositeSubscription _actionBarSubscriptions = new CompositeSubscription();

    @InjectView(R.id.toolbar) Toolbar _toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shows_list);

        ButterKnife.inject(this);

        setSupportActionBar(_toolbar);

        if(savedInstanceState != null) {
            _lastQuery = savedInstanceState.getString(STATE_KEY_QUERY);
        }
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _actionBarSubscriptions.clear();
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
	    SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);

        _actionBarSubscriptions.add(
                AndroidObservable.bindActivity(this, TVTDViewObservable.searchText(sv)
                .debounce(800, TimeUnit.MILLISECONDS)
                .map(new Func1<SearchView, String>() {
                    @Override
                    public String call(SearchView searchView) {
                        return searchView.getQuery().toString();
                    }
                }))
                .subscribe(new Action1<String>() {
                               @Override
                               public void call(String queryText) {
                                   searchForText(queryText);
                               }
                           })
        );

        boolean initiallyCollapsed = TextUtils.isEmpty(_lastQuery);

        _actionBarSubscriptions.add(
                AndroidObservable.bindActivity(this, TVTDViewObservable.collapsed(searchItem, initiallyCollapsed))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean collapsed) {
                        if(collapsed) {
                            hideSearch();
                        } else {
                            showSearch();
                        }
                    }
                })
        );

        if(!TextUtils.isEmpty(_lastQuery)) {
            sv.setQuery(_lastQuery, false);
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
}
