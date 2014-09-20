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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;
import the.autarch.tvto_do.model.SearchResultJson;
import the.autarch.tvto_do.model.Show;

public class ShowsListActivity extends BaseSpiceActivity {

	public static final int LOADER_ID_SHOW = 1;
	private static final String KEY_STATE_CURRENT_FRAGMENT = "ShowsListActivity.key_state_current_fragment";
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
		
		if(savedInstanceState == null) {
			hideSearch();
		} else {
			_isSearching = savedInstanceState.getBoolean(KEY_STATE_CURRENT_FRAGMENT);
			if(!_isSearching) {
				hideSearch();
			}
		}
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
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_STATE_CURRENT_FRAGMENT, _isSearching);
		super.onSaveInstanceState(outState);
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
		Fragment searchFrag = getSupportFragmentManager().findFragmentById(R.id.shows_search_fragment);
		getSupportFragmentManager().beginTransaction().hide(searchFrag).commit();
	}
	
	private void showSearch() {
		_isSearching = true;
		Fragment searchFrag = getSupportFragmentManager().findFragmentById(R.id.shows_search_fragment);
		getSupportFragmentManager().beginTransaction().show(searchFrag).commit();
	}

    private void searchForText(String query) {
        Log.e(getClass().getSimpleName(), "searching for query: " + query);
        ShowsSearchFragment searchFrag = (ShowsSearchFragment)getSupportFragmentManager().findFragmentById(R.id.shows_search_fragment);
        searchFrag.searchForText(query);
    }
}
