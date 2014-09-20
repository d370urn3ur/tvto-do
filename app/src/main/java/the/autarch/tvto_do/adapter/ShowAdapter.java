package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.event.UpdateExtendedInfoEvent;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.network.NetworkManager;
import the.autarch.tvto_do.util.TVTDImageCache;

public class ShowAdapter extends CursorAdapter {

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

        super(context, null, 0);

		_context = context;
        _inflater = LayoutInflater.from(context);
		Resources r = context.getResources();
		_endedColor = r.getColor(R.color.show_cell_ended_bg);
		_outOfDateColor = r.getColor(R.color.show_cell_out_of_date_bg);
		_evenColor = r.getColor(R.color.show_cell_even_bg);
		_oddColor = r.getColor(R.color.show_cell_odd_bg);
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = _inflater.inflate(R.layout.show_cell, parent, false);
        ShowCellHolder h = new ShowCellHolder(v);
        v.setTag(h);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Show show = new Show(cursor);

        // update extended info if it is newly added
        if(!show.isEnded() && !show.isExtendedInfoUpdated()) {
            UpdateExtendedInfoEvent event = new UpdateExtendedInfoEvent(show);
            EventBus.getDefault().post(event);
            NetworkManager.getInstance().downloadAndSaveImageForShow(show);
        }

        // set cell background
        int position = cursor.getPosition();
        int bgColor;
        if(show.isEnded()) {
            bgColor = _endedColor;
        } else if(show.isOutOfDate()) {
            bgColor = _outOfDateColor;
        } else {
            bgColor = (position % 2 == 0) ? _evenColor : _oddColor;
        }
        view.setBackgroundColor(bgColor);

        // populate cell with data
        ShowCellHolder holder = (ShowCellHolder)view.getTag();
        holder.populateWithShowAtPosition(show, position);
    }
	
	public void expandPosition(Integer position) {
		if(_expandedPosition == position) {
            _expandedPosition = -1;
        } else {
            _expandedPosition = position;
        }
	}

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        _expandedPosition = -1;
        return super.swapCursor(newCursor);
    }

    class ShowCellHolder {
		
		private ImageView _iv;
		private TextView _title;
		private TextView _nextTitle;
		private TextView _nextDate;
		private TextView _overview;

        private int dateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE;
		
		ShowCellHolder(View root) {
			_iv = (ImageView)root.findViewById(R.id.show_cell_image);
			_title = (TextView)root.findViewById(R.id.show_cell_title);
			_nextTitle = (TextView)root.findViewById(R.id.show_cell_next_title);
			_nextDate = (TextView)root.findViewById(R.id.show_cell_next_date);
			_overview = (TextView)root.findViewById(R.id.show_cell_overview);
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
