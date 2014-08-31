package the.autarch.tvto_do.loader;

import java.util.List;

import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.model.ShowDataSource;

import android.content.Context;

public class SQLiteShowDataLoader extends AbstractDataLoader<List<Show>> implements ShowDataSource.DataSourceObserver {
	
	private ShowDataSource mDataSource;
	private String mSelection;
	private String[] mSelectionArgs;
	private String mGroupBy;
	private String mHaving;
	private String mOrderBy;

	public SQLiteShowDataLoader(Context context, ShowDataSource dataSource, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		super(context);
		mDataSource = dataSource;
		mSelection = selection;
		mSelectionArgs = selectionArgs;
		mGroupBy = groupBy;
		mHaving = having;
		mOrderBy = orderBy;
	}

	@Override
	protected List<Show> buildList() {
		List<Show> shows = mDataSource.read(mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
		return shows;
	}
	
	@Override
	protected void onStartLoading() {
		mDataSource.addObserver(this);
		super.onStartLoading();
	}
	
	@Override
	protected void onReset() {
		mDataSource.removeObserver(this);
		super.onReset();
	}

	@Override
	public void dataSourceModified() {
		onContentChanged();
	}
}