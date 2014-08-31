package the.autarch.tvto_do.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

public class ShowDataSource extends AbstractDataSource<Show> {
	
	private Context _context;
	
	public static final String TABLE_NAME = "show";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_COUNTRY = "country";
	public static final String COLUMN_OVERVIEW = "overview";
	public static final String COLUMN_IMDB_ID = "imdb_id";
	public static final String COLUMN_TVDB_ID = "tvdb_id";
	public static final String COLUMN_TVRAGE_ID = "tvrage_id";
	public static final String COLUMN_ENDED = "ended";
	public static final String COLUMN_POSTER_138_URL = "poster_138_url";
	public static final String COLUMN_POSTER_300_URL = "poster_300_url";
	public static final String COLUMN_POSTER_138_FILEPATH = "poster_138_filepath";
	public static final String COLUMN_POSTER_300_FILEPATH = "poster_300_filepath";
	public static final String COLUMN_LAST_EXTENDED_INFO_SYNC = "last_extended_info_sync";
	public static final String COLUMN_NEXT_EPISODE_TITLE = "next_episode_title";
	public static final String COLUMN_NEXT_EPISODE_DATE = "next_episode_date";
	public static final String COLUMN_EXTENDED_INFO_UPDATED = "extended_info_updated";
	
	// Database creation sql statement
	public static final String CREATE_COMMAND =
			"create table " + TABLE_NAME + "(" +
			COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_TITLE + " text not null, " +
			COLUMN_YEAR + " text, " +
			COLUMN_URL + " text, " +
			COLUMN_COUNTRY + " text, " +
			COLUMN_OVERVIEW + " text, " +
			COLUMN_IMDB_ID + " text, " +
			COLUMN_TVDB_ID + " text, " +
			COLUMN_TVRAGE_ID + " text, " +
			COLUMN_ENDED + " integer, " +
			COLUMN_POSTER_138_URL + " text, " +
			COLUMN_POSTER_300_URL + " text, " +
			COLUMN_POSTER_138_FILEPATH + " text, " +
			COLUMN_POSTER_300_FILEPATH + " text, " +
			COLUMN_LAST_EXTENDED_INFO_SYNC + " text, " +
			COLUMN_NEXT_EPISODE_TITLE + " text, " +
			COLUMN_NEXT_EPISODE_DATE + " integer, " +
			COLUMN_EXTENDED_INFO_UPDATED + " integer default 0" + 
			");";
	
	public ShowDataSource(SQLiteDatabase database, Context context) {
		super(database);
		_context = context;
	}

	@Override
	public void insert(Show entity) {
		if(entity == null) {
			return;
		}
		new InsertTask(this, _context).execute(entity);
	}

	@Override
	public void delete(Show entity) {
		if(entity == null) {
			return;
		}
		new DeleteTask(this, _context).execute(entity);
	}

	@Override
	public void update(Show entity) {
		if(entity == null) {
			return;
		}
		new UpdateTask(this, _context).execute(entity);
	}

	@Override
	public List<Show> read() {
		
		Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), null, null, null, null, null);
		List<Show> shows = new ArrayList<Show>();
		if (cursor != null && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				shows.add(generateObjectFromCursor(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return shows;
	}

	@Override
	public List<Show> read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		
		Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
		List<Show> shows = new ArrayList<Show>();
		if (cursor != null && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				shows.add(generateObjectFromCursor(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return shows;
	}
	
	public String[] getAllColumns() {
		return new String[] { TABLE_NAME + ".*" };
	}
	
	public Show generateObjectFromCursor(Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		Show show = new Show();
		show.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
		show.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
		show.year = cursor.getString(cursor.getColumnIndex(COLUMN_YEAR));
		show.url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
		show.country = cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY));
		show.overview = cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW));
		show.imdbId = cursor.getString(cursor.getColumnIndex(COLUMN_IMDB_ID));
		show.tvdbId = cursor.getString(cursor.getColumnIndex(COLUMN_TVDB_ID));
		show.tvrageId = cursor.getString(cursor.getColumnIndex(COLUMN_TVRAGE_ID));
		show.ended = cursor.getInt(cursor.getColumnIndex(COLUMN_ENDED)) != 0;
		show.poster138Url = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_138_URL));
		show.poster300Url = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_300_URL));
		show.poster138filepath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_138_FILEPATH));
		show.poster300filepath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_300_FILEPATH));
//		show.lastExtendedInfoSync = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_EXTENDED_INFO_SYNC));
		show.nextEpisodeTitle = cursor.getString(cursor.getColumnIndex(COLUMN_NEXT_EPISODE_TITLE));
		long millis = cursor.getLong(cursor.getColumnIndex(COLUMN_NEXT_EPISODE_DATE));
		if(millis > 0) {
			Time t = new Time();
			t.set(millis);
			show.nextEpisodeTime = t;
		}
		show.extendedInfoUpdated = cursor.getInt(cursor.getColumnIndex(COLUMN_EXTENDED_INFO_UPDATED)) != 0;
		return show;
	}

	public ContentValues generateContentValuesFromObject(Show entity) {
		if (entity == null) {
			return null;
		}
		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, entity.title);
		values.put(COLUMN_YEAR, entity.year);
		values.put(COLUMN_URL, entity.url);
		values.put(COLUMN_COUNTRY, entity.country);
		values.put(COLUMN_OVERVIEW, entity.overview);
		values.put(COLUMN_IMDB_ID, entity.imdbId);
		values.put(COLUMN_TVDB_ID, entity.tvdbId);
		values.put(COLUMN_TVRAGE_ID, entity.tvrageId);
		values.put(COLUMN_ENDED, entity.ended);
		values.put(COLUMN_POSTER_138_URL, entity.poster138Url);
		values.put(COLUMN_POSTER_300_URL, entity.poster300Url);
		values.put(COLUMN_POSTER_138_FILEPATH, entity.poster138filepath);
		values.put(COLUMN_POSTER_300_FILEPATH, entity.poster300filepath);
//		values.put(COLUMN_LAST_EXTENDED_INFO_SYNC, entity.lastExtendedInfoSync);
		if(entity.nextEpisodeTitle != null) {
			values.put(COLUMN_NEXT_EPISODE_TITLE, entity.nextEpisodeTitle);
		}
		if(entity.nextEpisodeTime != null) {
			values.put(COLUMN_NEXT_EPISODE_DATE, entity.nextEpisodeTime.toMillis(false));
		}
		values.put(COLUMN_EXTENDED_INFO_UPDATED, entity.extendedInfoUpdated);
		
		return values;
	}

	private class InsertTask extends ContentChangingTask<Show, Void, Boolean> {
		
		InsertTask(ShowDataSource dataSource, Context context) {
			super(dataSource, context);
		}

		@Override
		protected Boolean doInBackground(Show... params) {
			Show entity = params[0];
			_toastMessage = entity.title + " was added.";
			long result = mDatabase.insert(TABLE_NAME, null, generateContentValuesFromObject(entity));
			return result != -1;
		}
	}

	private class UpdateTask extends ContentChangingTask<Show, Void, Boolean> {
		
		UpdateTask(ShowDataSource dataSource, Context context) {
			super(dataSource, context);
		}

		@Override
		protected Boolean doInBackground(Show... params) {
			Show entity = params[0];
			_toastMessage = entity.title + " was updated.";
			int result = mDatabase.update(TABLE_NAME, generateContentValuesFromObject(entity), COLUMN_ID + "=?", new String[] { Integer.toString(entity.id)});
			return result != 0;
		}
	}

	private class DeleteTask extends ContentChangingTask<Show, Void, Boolean> {
		DeleteTask(ShowDataSource dataSource, Context context) {
			super(dataSource, context);
		}

		@Override
		protected Boolean doInBackground(Show... params) {
			Show entity = params[0];
			if(entity.poster138filepath != null) {
				FileManager.getInstance().deleteBitmapForFilename(entity.poster138filepath);
			}
			mDatabase.delete(TABLE_NAME, COLUMN_ID + " =?", new String[] {Integer.toString(entity.id)});
			_toastMessage = entity.title + " was removed";
			return Boolean.TRUE;
		}
	}
}
