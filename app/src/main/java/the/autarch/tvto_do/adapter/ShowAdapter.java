package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.network.NetworkManager;
import the.autarch.tvto_do.util.TVTDImageCache;

public class ShowAdapter extends BaseAdapter {

    private List<Show> _data = new ArrayList<Show>();

	private int _expandedPosition = -1;
	private TVTDImageCache _imageCache = new TVTDImageCache();

    private Context _context;
    private LayoutInflater _inflater;
	
	// colors
	private int _endedColor;
	private int _outOfDateColor;
	private int _evenColor;
	private int _oddColor;
	
	public ShowAdapter(Context context) {
        super();

		_context = context;
        _inflater = LayoutInflater.from(context);
		Resources r = context.getResources();
		_endedColor = r.getColor(R.color.show_cell_ended_bg);
		_outOfDateColor = r.getColor(R.color.show_cell_out_of_date_bg);
		_evenColor = r.getColor(R.color.show_cell_even_bg);
		_oddColor = r.getColor(R.color.show_cell_odd_bg);
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = _inflater.inflate(R.layout.show_cell, parent, false);
            ShowCellHolder h = new ShowCellHolder(convertView);
            convertView.setTag(h);
        }

        Show show = (Show)getItem(position);

        // set cell background
        int bgColor;
        if(show.getExtendedInfoStatus() == Show.ExtendedInfoStatus.EXTENDED_INFO_ENDED) {
            bgColor = _endedColor;
        } else if(show.isOutOfDate()) {
            bgColor = _outOfDateColor;
        } else {
            bgColor = (position % 2 == 0) ? _evenColor : _oddColor;
        }
        convertView.setBackgroundColor(bgColor);

        // populate cell with data
        ShowCellHolder holder = (ShowCellHolder)convertView.getTag();
        holder.populateWithShowAtPosition(show, position);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Show)getItem(position)).getId();
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    public void expandPosition(Integer position) {
		if(_expandedPosition == position) {
            _expandedPosition = -1;
        } else {
            _expandedPosition = position;
        }
	}

    public void swapData(List<Show> data) {
        _expandedPosition = -1;
        _data.clear();
        if(data != null) {
            _data.addAll(data);
        }
        notifyDataSetChanged();
    }

    class ShowCellHolder {
		
		@InjectView(R.id.show_cell_image) ImageView _iv;
		@InjectView(R.id.show_cell_title) TextView _title;
		@InjectView(R.id.show_cell_next_title) TextView _nextTitle;
		@InjectView(R.id.show_cell_next_date) TextView _nextDate;
		@InjectView(R.id.show_cell_overview) TextView _overview;

        private int dateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE;
		
		ShowCellHolder(View root) {
            ButterKnife.inject(this, root);
		}
		
		void populateWithShowAtPosition(Show show, int position) {

            _title.setText(show.getTitle());
            _nextTitle.setText(show.prettyNextEpisode());
            _nextDate.setText(show.prettyNextDate(_context, dateFormatFlags));
            _overview.setText(show.getOverview());

            if(_expandedPosition == position) {
                _overview.setVisibility(View.VISIBLE);
            } else {
                _overview.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(show.getPoster138Url())) {
                _iv.setImageResource(R.drawable.poster_dark);
            } else if(TextUtils.isEmpty(show.getPoster138filepath())) {
				_iv.setImageResource(R.drawable.poster_dark);
                NetworkManager.getInstance().downloadAndSaveImageForShow(show);

			} else {
				Bitmap b = _imageCache.getBitmap(show.getPoster138filepath());
				if(b != null) {
					_iv.setImageBitmap(b);
				} else {
					b = FileManager.getInstance().getBitmapForFilename(show.getPoster138filepath());
					_imageCache.put(show.getPoster138filepath(), b);
					_iv.setImageBitmap(b);
				}
			}
		}
	}
}
