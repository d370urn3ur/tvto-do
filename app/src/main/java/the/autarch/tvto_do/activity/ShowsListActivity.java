package the.autarch.tvto_do.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Timer;
import java.util.TimerTask;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.event.NetworkEvent;
import the.autarch.tvto_do.event.UpdateExpiredExtendedInfoEvent;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.network.ExtendedInfoRequest;
import the.autarch.tvto_do.network.ExtendedInfoRequestListener;

public class ShowsListActivity extends BaseSpiceActivity {

	public static final int LOADER_ID_SHOW = 1;
    private static final int SEARCH_QUERY_THRESHOLD_MILLIS = 2 * 1000;

    private static final String STATE_KEY_QUERY = "ShowsSearchFragment.state_key_query";

    private MenuItem _searchItem;
    private String _lastQuery;

    private Handler _searchDelayedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            _searchTimer.cancel();
            _searchTimer = null;
            searchForText((String)msg.obj);
        }
    };
    private Timer _searchTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shows_list);

        if(savedInstanceState != null) {
            _lastQuery = savedInstanceState.getString(STATE_KEY_QUERY);
//            if(!TextUtils.isEmpty(_lastQuery)) {
//                searchForText(_lastQuery);
//            }
        }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_KEY_QUERY, _lastQuery);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(_searchTimer != null) {
            _searchTimer.cancel();
            _searchTimer = null;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shows_list, menu);
		
		_searchItem = (MenuItem)menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(_searchItem);
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(final String searchText) {

                if(_searchTimer != null) {
                    _searchTimer.cancel();
                    _searchTimer = null;
                }

                if(!TextUtils.isEmpty(searchText)) {

                    _searchTimer = new Timer();
                    _searchTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message m = _searchDelayedHandler.obtainMessage(0, searchText);
                            _searchDelayedHandler.sendMessage(m);
                        }
                    }, SEARCH_QUERY_THRESHOLD_MILLIS);
                }

				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String searchText) {

                if(_searchTimer != null) {
                    _searchTimer.cancel();
                    _searchTimer = null;
                }

				searchForText(searchText);
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
}
