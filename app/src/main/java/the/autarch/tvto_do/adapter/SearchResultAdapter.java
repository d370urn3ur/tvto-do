package the.autarch.tvto_do.adapter;

import java.util.ArrayList;
import java.util.Collection;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.SearchResultJson;
import the.autarch.tvto_do.network.NetworkManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class SearchResultAdapter extends ArrayAdapter<SearchResultJson> {

	private int _layoutRes;
    private int _expandedPosition = -1;
	
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
		
		SearchResultJson searchResult = getItem(position);

        boolean expanded = _expandedPosition == position;
		
		// set background color
        if(expanded) {
            v.setBackgroundColor(getContext().getResources().getColor(R.color.holo_blue_bright));
        } else {
            int mod = position % 2;
            int colorRef = (mod == 0) ? R.color.holo_gray_bright : R.color.holo_gray_light;
            v.setBackgroundColor(getContext().getResources().getColor(colorRef));
        }
		
		SearchResultCellHolder holder = (SearchResultCellHolder)v.getTag();
		holder.loadSearchResult(searchResult, expanded);
		return v;
	}
	
	public void supportAddAll(Collection<SearchResultJson> data) {
		for(SearchResultJson s : data) {
			add(s);
		}
	}
	
	public void empty() {
		ArrayList<SearchResultJson> items = new ArrayList<SearchResultJson>();
		for(int i=0; i < getCount(); ++i) {
			items.add(getItem(i));
		}
		for(SearchResultJson s : items) {
			remove(s);
		}
		_expandedPosition = -1;
	}
	
	public void toggleExpandedCell(int position) {
        if(_expandedPosition == position) {
            _expandedPosition = -1;
        } else {
            _expandedPosition = position;
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
		
		void loadSearchResult(final SearchResultJson searchResult, boolean expanded) {
			
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
			if(expanded) {
				_overview.setVisibility(View.VISIBLE);
			} else {
				_overview.setVisibility(View.GONE);
			}
		}
	}
}
