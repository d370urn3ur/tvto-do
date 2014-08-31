package the.autarch.tvto_do.activity;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.DataManager;
import the.autarch.tvto_do.model.SearchResultWrapper;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.model.ShowDataSource;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;

public class ShowsListActivity extends ActionBarActivity implements ShowsSearchFragment.Callback {

	public static final int LOADER_ID_SHOW = 1;
	private static final String KEY_STATE_CURRENT_FRAGMENT = "ShowsListActivity.key_state_current_fragment";
	private boolean _isSearching = false;
	
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
			public boolean onQueryTextChange(String searchText) {
				ShowsSearchFragment searchFrag = (ShowsSearchFragment)getSupportFragmentManager().findFragmentById(R.id.shows_search_fragment);
				searchFrag.searchForText(searchText);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String searchText) {
				ShowsSearchFragment searchFrag = (ShowsSearchFragment)getSupportFragmentManager().findFragmentById(R.id.shows_search_fragment);
				searchFrag.searchForText(searchText);
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

	@Override
	public void userSelectedSearchResult(SearchResultWrapper searchResult) {
		Show newShow = new Show();
		newShow.hydrateFromSearchResult(searchResult);
		ShowDataSource showDataSource = DataManager.getInstance().getShowDataSource();
		showDataSource.insert(newShow);
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
}
