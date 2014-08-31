package the.autarch.tvto_do.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.DataManager;
import the.autarch.tvto_do.model.ExtendedInfoWrapper;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.model.ShowDataSource;
import the.autarch.tvto_do.network.ExtendedInfoRequest;
import the.autarch.tvto_do.network.NetworkManager;
import the.autarch.tvto_do.util.TVTDImageCache;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ShowAdapter extends ArrayAdapter<Show> {
	
	private int _layoutRes;
	private ArrayList<Integer> _expandedCells = new ArrayList<Integer>();
	private HashMap<String, ExtendedInfoRequest> _requests = new HashMap<String, ExtendedInfoRequest>();
	private TVTDImageCache _imageCache = new TVTDImageCache();
	
	// colors
	private int _endedColor;
	private int _outOfDateColor;
	private int _evenColor;
	private int _oddColor;
	
	public ShowAdapter(Context context, int resource) {
		super(context, resource);
		_layoutRes = resource;
		
		Resources r = context.getResources();
		_endedColor = r.getColor(R.color.show_cell_ended_bg);
		_outOfDateColor = r.getColor(R.color.show_cell_out_of_date_bg);
		_evenColor = r.getColor(R.color.show_cell_even_bg);
		_oddColor = r.getColor(R.color.show_cell_odd_bg);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(position >= getCount()) {
			return null;
		}
		
		View v = convertView;
		if(v == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(_layoutRes, parent, false);
			ShowCellHolder holder = new ShowCellHolder(v);
			v.setTag(holder);
		}
		
		Show show = getItem(position);
		
		// update extended info if it is newly added
		if(!show.ended && !show.extendedInfoUpdated) {
			getExtendedInfoForShow(show);
			NetworkManager.getInstance().downloadAndSaveImageForShow(show);
		}
		
		// set cell background
		int bgColor;
		if(show.ended) {
			bgColor = _endedColor;
		} else if(show.isOutOfDate()) {
			bgColor = _outOfDateColor;
		} else {
			bgColor = (position % 2 == 0) ? _evenColor : _oddColor;
		}
		v.setBackgroundColor(bgColor);
		
		// populate cell with data
		ShowCellHolder holder = (ShowCellHolder)v.getTag();
		holder.populateWithShow(show);
		return v;
	}
	
	public void supportAddAll(List<Show> newData) {
		
		ArrayList<Show> showsNotInAdapter = new ArrayList<Show>();
		ArrayList<Show> showsNotInNewData = new ArrayList<Show>();
		
		for(Show s : newData) {
			boolean found = false;
			for(int i=0; i < getCount(); ++i) {
				Show show = getItem(i);
				if(s.id == show.id) {
					found = true;
					break;
				}
			}
			if(!found) {
				showsNotInAdapter.add(s);
			}
		}
		
		for(int i=0; i < getCount(); ++i) {
			Show show = getItem(i);
			boolean found = false;
			for(Show s : newData) {
				if(s.id == show.id) {
					found = true;
					break;
				}
			}
			if(!found) {
				showsNotInNewData.add(show);
			}
		}
		
		for(Show s : showsNotInAdapter) {
			add(s);
		}
		
		for(Show s : showsNotInNewData) {
			remove(s);
		}
	}
	
	public void empty() {
		ArrayList<Show> shows = new ArrayList<Show>();
		for(int i=0; i < getCount(); ++i) {
			shows.add(getItem(i));
		}
		for(Show s : shows) {
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
	
	public void getExtendedInfoForShow(final Show show) {
		
		if(_requests.containsKey(show.tvrageId)) {
			return;
		}
		
		ExtendedInfoRequest req = ExtendedInfoRequest.getExtendedInfoForTvRageId(show.tvrageId,
				new HashMap<String,String>(),
				new Listener<ExtendedInfoWrapper>() {
					@Override
					public void onResponse(ExtendedInfoWrapper response) {
						if(response != null) {
							show.updateWithExtendedInfo(response);
							ShowDataSource dataSource = DataManager.getInstance().getShowDataSource();
							dataSource.update(show);
						}
						
						_requests.remove(show.tvrageId);
					}
				},
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						_requests.remove(show.tvrageId);
						Log.d(getClass().getName(), error.toString());
					}
				});
		
		_requests.put(show.tvrageId, req);
		
		RequestQueue queue = NetworkManager.getInstance().getRequestQueue();
		queue.add(req);
	}
	
	class ShowCellHolder {
		
		private ImageView _iv;
		private TextView _title;
		private TextView _nextTitle;
		private TextView _nextDate;
		private TextView _overview;
		private int _dateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE;
		
		ShowCellHolder(View root) {
			_iv = (ImageView)root.findViewById(R.id.show_cell_image);
			_title = (TextView)root.findViewById(R.id.show_cell_title);
			_nextTitle = (TextView)root.findViewById(R.id.show_cell_next_title);
			_nextDate = (TextView)root.findViewById(R.id.show_cell_next_date);
			_overview = (TextView)root.findViewById(R.id.show_cell_overview);
		}
		
		void populateWithShow(Show show) {
			
			if(show.poster138filepath == null) {
				_iv.setImageResource(R.drawable.poster_dark);
			} else {
				Bitmap b = _imageCache.getBitmap(show.poster138filepath);
				if(b != null) {
					_iv.setImageBitmap(b);
				} else {
					b = FileManager.getInstance().getBitmapForFilename(show.poster138filepath);
					_imageCache.put(show.poster138filepath, b);
					_iv.setImageBitmap(b);
				}
			}
			
			_title.setText(show.title);
			_nextTitle.setText(show.prettyNextEpisode());
			_nextDate.setText(show.prettyNextDate(getContext(), _dateFormatFlags));
			
			_overview.setText(show.overview);
			if(_expandedCells.contains(Integer.valueOf(getPosition(show)))) {
				_overview.setVisibility(View.VISIBLE);
			} else {
				_overview.setVisibility(View.GONE);
			}
		}
	}
}
