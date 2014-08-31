package the.autarch.tvto_do.adapter;

import java.util.ArrayList;
import java.util.Collection;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.SearchResultWrapper;
import the.autarch.tvto_do.network.NetworkManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class SearchResultAdapter extends ArrayAdapter<SearchResultWrapper> {

	private int _layoutRes;
	private ArrayList<Integer> _expandedCells = new ArrayList<Integer>();
	
	public SearchResultAdapter(Context context, int resource) {
		super(context, resource);
		_layoutRes = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if(v == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(_layoutRes, parent, false);
			SearchResultCellHolder holder = new SearchResultCellHolder(v);
			v.setTag(holder);
		}
		
		SearchResultWrapper searchResult = getItem(position);
		
		// set background color
		int mod = position % 2;
		int colorRef = (mod == 0) ? R.color.holo_gray_bright : R.color.holo_blue_light;
		v.setBackgroundColor(getContext().getResources().getColor(colorRef));
		
		SearchResultCellHolder holder = (SearchResultCellHolder)v.getTag();
		holder.loadSearchResult(searchResult);
		return v;
	}
	
	public void supportAddAll(Collection<SearchResultWrapper> data) {
		for(SearchResultWrapper s : data) {
			add(s);
		}
	}
	
	public void empty() {
		ArrayList<SearchResultWrapper> items = new ArrayList<SearchResultWrapper>();
		for(int i=0; i < getCount(); ++i) {
			items.add(getItem(i));
		}
		for(SearchResultWrapper s : items) {
			remove(s);
		}
		_expandedCells.clear();
	}
	
	public void toggleExpandedCell(Integer position) {
		if(_expandedCells.contains(position)) {
			_expandedCells.remove(position);
		} else {
			_expandedCells.add(position);
		}
	}
	
	class SearchResultCellHolder {
		private NetworkImageView _iv;
		private TextView _title;
		private TextView _status;
		private TextView _year;
		private TextView _overview;
		
		SearchResultCellHolder(View root) {
			_iv = (NetworkImageView)root.findViewById(R.id.search_cell_image);
			_iv.setDefaultImageResId(R.drawable.poster_dark);
			_iv.setErrorImageResId(R.drawable.poster_dark);
			
			_title = (TextView)root.findViewById(R.id.search_cell_title);
			_status = (TextView)root.findViewById(R.id.search_cell_status);
			_year = (TextView)root.findViewById(R.id.search_cell_year);
			_overview = (TextView)root.findViewById(R.id.search_cell_overview);
		}
		
		void loadSearchResult(final SearchResultWrapper searchResult) {
			
			if(searchResult.hasPoster()) {
				ImageLoader il = NetworkManager.getInstance().getImageLoader();
				_iv.setImageUrl(searchResult.getPoster138Url(), il);
			}
			
			_title.setText(searchResult.title);
			_status.setText(searchResult.prettyStatus());
			if(searchResult.hasEnded()) {
				_status.setVisibility(View.VISIBLE);
			} else {
				_status.setVisibility(View.GONE);
			}
			_year.setText(searchResult.year);
			_overview.setText(searchResult.overview);
			if(_expandedCells.contains(Integer.valueOf(getPosition(searchResult)))) {
				_overview.setVisibility(View.VISIBLE);
			} else {
				_overview.setVisibility(View.GONE);
			}
		}
	}
}
