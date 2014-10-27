package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import the.autarch.tvto_do.BuildConfig;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.gson.SearchResultGson;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultCellHolder> {

    private Context _context;
	private int _layoutRes;
    LayoutInflater _inflater;

    List<SearchResultGson> _data = Collections.EMPTY_LIST;
	
	public SearchResultAdapter(Context context, int resource) {
        _context = context;
		_layoutRes = resource;
        _inflater = LayoutInflater.from(context);
	}
	
	public void swapData(List<SearchResultGson> data) {
        _data.clear();
        _data.addAll(data);
        notifyDataSetChanged();
    }

    public SearchResultGson getItem(int position) {
        return _data.get(position);
    }

    @Override
    public SearchResultCellHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = _inflater.inflate(_layoutRes, viewGroup, false);
        return new SearchResultCellHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchResultCellHolder searchResultCellHolder, int i) {
        SearchResultGson item = _data.get(i);
        searchResultCellHolder.loadSearchResult(item, i);
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    class SearchResultCellHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.search_cell_image) ImageView _iv;
		@InjectView(R.id.search_cell_title) TextView _title;
		@InjectView(R.id.search_cell_status) TextView _status;
		@InjectView(R.id.search_cell_year) TextView _year;
		@InjectView(R.id.search_cell_overview) TextView _overview;

        public SearchResultCellHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
		
		void loadSearchResult(final SearchResultGson searchResult, int position) {
			
			_title.setText(searchResult.title);
			_status.setText(searchResult.prettyStatus());
			if(searchResult.hasEnded()) {
				_status.setVisibility(View.VISIBLE);
			} else {
				_status.setVisibility(View.GONE);
			}
			_year.setText(searchResult.year);
			_overview.setText(searchResult.overview);

            Picasso picasso = Picasso.with(_context);
            picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
            picasso.load(searchResult.getPoster138Url())
                    .placeholder(R.drawable.poster_dark)
                    .error(R.drawable.poster_dark)
                    .into(_iv);
		}
	}
}
