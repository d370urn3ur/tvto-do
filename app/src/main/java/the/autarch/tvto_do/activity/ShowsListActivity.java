package the.autarch.tvto_do.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.event.DatabaseInitializedEvent;
import the.autarch.tvto_do.event.UpdateExpiredExtendedInfoEvent;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.gson.SearchResultGson;
import the.autarch.tvto_do.network.ExtendedInfoRequest;
import the.autarch.tvto_do.network.ExtendedInfoRequestListener;
import the.autarch.tvto_do.network.SearchRequest;

public class ShowsListActivity extends BaseSpiceActivity {

	public static final int LOADER_ID_SHOW = 1;
	private boolean _isSearching = false;
    private static final int SEARCH_QUERY_THRESHOLD_MILLIS = 2 * 1000;

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
	}

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
		
		MenuItem searchItem = (MenuItem)menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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

	    // When using the support library, the setOnActionExpandListener() method is
	    // static and accepts the MenuItem object as an argument
	    MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
	    	
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
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	private void hideSearch() {
		_isSearching = false;
        getSupportFragmentManager().popBackStack();
	}
	
	private void showSearch() {
		_isSearching = true;
        Fragment searchFrag = Fragment.instantiate(this, ShowsSearchFragment.class.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, searchFrag, searchFrag.getClass().getName())
                .addToBackStack(null)
                .commit();
	}

    private void searchForText(String query) {
        ShowsSearchFragment searchFrag = (ShowsSearchFragment)getSupportFragmentManager().findFragmentByTag(ShowsSearchFragment.class.getName());
        if(searchFrag != null) {
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
}
