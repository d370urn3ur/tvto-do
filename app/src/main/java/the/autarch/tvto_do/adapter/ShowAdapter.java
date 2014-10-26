package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import the.autarch.tvto_do.BuildConfig;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.fragment.ShowsListFragment;
import the.autarch.tvto_do.model.database.Show;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowCellHolder> {

    private List<Show> _data = new ArrayList<Show>();

	private int _expandedPosition = -1;

    private Context _context;
    private ShowsListFragment.ShowSelector _clickListener;
    private LayoutInflater _inflater;
	
	// colors
	private int _endedColor;
	private int _outOfDateColor;
	private int _evenColor;
	private int _oddColor;
	
	public ShowAdapter(Context context, ShowsListFragment.ShowSelector clickListener) {
        super();

		_context = context;
        _clickListener = clickListener;
        _inflater = LayoutInflater.from(context);
		Resources r = context.getResources();
		_endedColor = r.getColor(R.color.show_cell_ended_bg);
		_outOfDateColor = r.getColor(R.color.show_cell_out_of_date_bg);
		_evenColor = r.getColor(R.color.show_cell_even_bg);
		_oddColor = r.getColor(R.color.show_cell_odd_bg);
	}

    public Show getItem(int i) {
        return _data.get(i);
    }

    @Override
    public ShowCellHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = _inflater.inflate(R.layout.show_cell, viewGroup, false);
        return new ShowCellHolder(view);
    }

    @Override
    public void onBindViewHolder(ShowCellHolder showCellHolder, int i) {
        Show show = _data.get(i);
        showCellHolder.populateWithShowAtPosition(show, i);
    }

    @Override
    public long getItemId(int position) {
        return _data.get(position).getId();
    }

    @Override
    public int getItemCount() {
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

    public class ShowCellHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.show_cell_image) ImageView _iv;
        @InjectView(R.id.show_cell_text_container) View _textContainer;
		@InjectView(R.id.show_cell_next_title) TextView _nextTitle;
		@InjectView(R.id.show_cell_next_date) TextView _nextDate;
		@InjectView(R.id.show_cell_overview) TextView _overview;

        private int dateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE;
		
		public ShowCellHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
		}
		
		void populateWithShowAtPosition(final Show show, final int position) {

            int bgColor;
            if(show.hasEnded()) {
                bgColor = _endedColor;
            } else if(show.isOutOfDate()) {
                bgColor = _outOfDateColor;
            } else {
                bgColor = (position % 2 == 0) ? _evenColor : _oddColor;
            }
//            itemView.setBackgroundColor(bgColor);

            _nextTitle.setText(show.prettyNextEpisode());
            _nextDate.setText(show.prettyNextDate(_context, dateFormatFlags));
            _overview.setText(show.getOverview());

            if(_expandedPosition == position) {
                _overview.setVisibility(View.VISIBLE);
            } else {
                _overview.setVisibility(View.GONE);
            }

            Picasso p = Picasso.with(_context);
            p.setIndicatorsEnabled(BuildConfig.DEBUG);
            p.load(show.getPoster138Url())
                    .placeholder(R.drawable.poster_dark)
                    .error(R.drawable.poster_dark)
                    .transform(new PaletteGrabberTransformation(show))
                    .into(_iv, new Callback() {
                        @Override
                        public void onSuccess() {
                            if(show.palette != null) {

                                Palette.Swatch swatch = show.palette.getLightVibrantSwatch();
                                if(swatch != null) {
                                    _textContainer.setBackgroundColor(swatch.getRgb());
                                    _nextTitle.setTextColor(swatch.getTitleTextColor());
                                    _nextDate.setTextColor(swatch.getTitleTextColor());
                                    _overview.setTextColor(swatch.getBodyTextColor());
                                }
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _clickListener.onShowSelected(position);
                }
            });
		}
	}

    class PaletteGrabberTransformation implements Transformation {

        private Show show;

        PaletteGrabberTransformation(Show show) {
            this.show = show;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            show.palette = Palette.generate(source);
            return source;
        }

        @Override
        public String key() {
            return "paletteGrabber()";
        }
    }
}
