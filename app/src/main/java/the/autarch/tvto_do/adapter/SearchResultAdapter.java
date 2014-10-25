package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.gson.SearchResultGson;
import the.autarch.tvto_do.util.ViewHolder;

public class SearchResultAdapter extends ArrayAdapter<SearchResultGson> {

	private int _layoutRes;
    private int _expandedPosition = -1;
    LayoutInflater _inflater;
	
	public SearchResultAdapter(Context context, int resource) {
		super(context, resource);
		_layoutRes = resource;
        _inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        SearchResultGson searchResult = getItem(position);

		if(convertView == null) {
			convertView = _inflater.inflate(_layoutRes, parent, false);
//			SearchResultCellHolder holder = new SearchResultCellHolder(convertView);
//			convertView.setTag(holder);
		}

        ImageView iv = ViewHolder.get(convertView, R.id.search_cell_image);
        TextView title = ViewHolder.get(convertView, R.id.search_cell_title);
        TextView status = ViewHolder.get(convertView, R.id.search_cell_status);
        TextView year = ViewHolder.get(convertView, R.id.search_cell_year);
        TextView overview = ViewHolder.get(convertView, R.id.search_cell_overview);

        Picasso.with(getContext()).cancelRequest(iv);

        boolean expanded = _expandedPosition == position;

        if(searchResult.hasPoster()) {
            Picasso.with(getContext())
                    .load(searchResult.getPoster138Url())
                    .placeholder(R.drawable.poster_dark)
                    .error(R.drawable.poster_dark)
                    .into(iv);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.poster_dark)
                    .into(iv);
        }

        title.setText(searchResult.title);
        status.setText(searchResult.prettyStatus());
        if(searchResult.hasEnded()) {
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.GONE);
        }
        year.setText(searchResult.year);
        overview.setText(searchResult.overview);

        if(expanded) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.holo_blue_bright));
            overview.setVisibility(View.VISIBLE);
        } else {
            int mod = position % 2;
            int colorRef = (mod == 0) ? R.color.holo_gray_bright : R.color.holo_gray_light;
            convertView.setBackgroundColor(getContext().getResources().getColor(colorRef));
            overview.setVisibility(View.GONE);
        }

		return convertView;
	}
	
	public void swapData(List<SearchResultGson> data) {
        setNotifyOnChange(false);
        clear();
        for(SearchResultGson sr : data) {
            add(sr);
        }
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        _expandedPosition = -1;
        super.clear();
    }
	
	public void toggleExpandedCell(int position) {
        if(_expandedPosition == position) {
            _expandedPosition = -1;
        } else {
            _expandedPosition = position;
        }
	}
	
	class SearchResultCellHolder {

		@InjectView(R.id.search_cell_image) ImageView _iv;
		@InjectView(R.id.search_cell_title) TextView _title;
		@InjectView(R.id.search_cell_status) TextView _status;
		@InjectView(R.id.search_cell_year) TextView _year;
		@InjectView(R.id.search_cell_overview) TextView _overview;
		
		SearchResultCellHolder(View root) {

            ButterKnife.inject(this, root);

//			_iv.setDefaultImageResId(R.drawable.poster_dark);
//			_iv.setErrorImageResId(R.drawable.poster_dark);
		}
		
		void loadSearchResult(final SearchResultGson searchResult, boolean expanded) {
			
			if(searchResult.hasPoster()) {
//				ImageLoader il = NetworkManager.getInstance().getImageLoader();
//				_iv.setImageUrl(searchResult.getPoster138Url(), il);
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
