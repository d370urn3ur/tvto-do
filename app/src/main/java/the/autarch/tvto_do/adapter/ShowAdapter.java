package the.autarch.tvto_do.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import the.autarch.tvto_do.R;
import the.autarch.tvto_do.fragment.ShowsListFragment;
import the.autarch.tvto_do.model.Show;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowCellHolder> {

    private List<Show> _data = new ArrayList<Show>();

	private int _expandedPosition = -1;

    private Context _context;
    private ShowsListFragment.ShowSelector _clickListener;
    private LayoutInflater _inflater;
	
	// colors
	private int _endedColor;
	private int _outOfDateColor;
	
	public ShowAdapter(Context context, ShowsListFragment.ShowSelector clickListener) {
        super();

		_context = context;
        _clickListener = clickListener;
        _inflater = LayoutInflater.from(context);
		Resources r = context.getResources();
		_endedColor = r.getColor(R.color.show_cell_ended_bg);
		_outOfDateColor = r.getColor(R.color.show_cell_out_of_date_bg);
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
        return 0;
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

            _nextTitle.setText(show.prettyNextEpisode());
            _nextDate.setText(show.prettyStatus());
            _overview.setText(show.overview);

            if(_expandedPosition == position) {
                _overview.setVisibility(View.VISIBLE);
            } else {
                _overview.setVisibility(View.GONE);
            }

            setGradientBackground();

            Picasso.with(_context)
                    .load(show.getPoster300Url())
                    .placeholder(R.drawable.poster_dark)
                    .error(R.drawable.poster_dark)
                    .into(_iv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _clickListener.onShowSelected(position);
                }
            });
		}

        private void setGradientBackground() {

            int[] gradColors = new int[] {_context.getResources().getColor(R.color.material_teal_700), _context.getResources().getColor(R.color.material_teal_200) - 0x20000000};

            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, gradColors);

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                _textContainer.setBackgroundDrawable(bg);
            } else {
                _textContainer.setBackground(bg);
            }
            _nextTitle.setTextColor(Color.WHITE);
            _nextDate.setTextColor(Color.WHITE);
            _overview.setTextColor(Color.WHITE);
        }
	}
}
