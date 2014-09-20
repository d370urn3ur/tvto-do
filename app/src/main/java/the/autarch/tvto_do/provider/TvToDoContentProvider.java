package the.autarch.tvto_do.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import the.autarch.tvto_do.model.DatabaseHelper;

/**
 * Created by jpierce on 9/13/14.
 */
public class TvToDoContentProvider extends ContentProvider {

    private DatabaseHelper _dbHelper;

    private static HashMap<String, String> sShowsProjectionMap;

    private static final int SHOWS = 1;
    private static final int SHOW_ID = 2;

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ShowContract.AUTHORITY, "shows", SHOWS);
        sUriMatcher.addURI(ShowContract.AUTHORITY, "shows/#", SHOW_ID);

        sShowsProjectionMap = new HashMap<String, String>();
        sShowsProjectionMap.put(ShowContract.ShowColumns._ID, ShowContract.ShowColumns._ID);
        sShowsProjectionMap.put(ShowContract.ShowColumns.TITLE, ShowContract.ShowColumns.TITLE);
        sShowsProjectionMap.put(ShowContract.ShowColumns.URL, ShowContract.ShowColumns.URL);
        sShowsProjectionMap.put(ShowContract.ShowColumns.YEAR, ShowContract.ShowColumns.YEAR);
        sShowsProjectionMap.put(ShowContract.ShowColumns.COUNTRY, ShowContract.ShowColumns.COUNTRY);
        sShowsProjectionMap.put(ShowContract.ShowColumns.OVERVIEW, ShowContract.ShowColumns.OVERVIEW);
        sShowsProjectionMap.put(ShowContract.ShowColumns.IMDB_ID, ShowContract.ShowColumns.IMDB_ID);
        sShowsProjectionMap.put(ShowContract.ShowColumns.TVDB_ID, ShowContract.ShowColumns.TVDB_ID);
        sShowsProjectionMap.put(ShowContract.ShowColumns.TVRAGE_ID, ShowContract.ShowColumns.TVRAGE_ID);
        sShowsProjectionMap.put(ShowContract.ShowColumns.ENDED, ShowContract.ShowColumns.ENDED);
        sShowsProjectionMap.put(ShowContract.ShowColumns.POSTER_138_URL, ShowContract.ShowColumns.POSTER_138_URL);
        sShowsProjectionMap.put(ShowContract.ShowColumns.POSTER_300_URL, ShowContract.ShowColumns.POSTER_300_URL);
        sShowsProjectionMap.put(ShowContract.ShowColumns.POSTER_138_FILEPATH, ShowContract.ShowColumns.POSTER_138_FILEPATH);
        sShowsProjectionMap.put(ShowContract.ShowColumns.POSTER_300_FILEPATH, ShowContract.ShowColumns.POSTER_300_FILEPATH);
        sShowsProjectionMap.put(ShowContract.ShowColumns.EXTENDED_INFO_UPDATED, ShowContract.ShowColumns.EXTENDED_INFO_UPDATED);
        sShowsProjectionMap.put(ShowContract.ShowColumns.NEXT_EPISODE_TITLE, ShowContract.ShowColumns.NEXT_EPISODE_TITLE);
        sShowsProjectionMap.put(ShowContract.ShowColumns.NEXT_EPISODE_TIME, ShowContract.ShowColumns.NEXT_EPISODE_TIME);
    }

    public boolean onCreate() {
        _dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ShowContract.TABLE_NAME);
        qb.setProjectionMap(sShowsProjectionMap);

        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                break;
            case SHOW_ID:
                qb.appendWhere(ShowContract.ShowColumns._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = ShowContract.ShowColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case SHOWS:
                return ShowContract.ShowColumns.CONTENT_TYPE;
            case SHOW_ID:
                return ShowContract.ShowColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        // Validate the requested uri
        if (sUriMatcher.match(uri) != SHOWS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        long rowId = db.insert(ShowContract.TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(ShowContract.ShowColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return noteUri;
        }

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                count = db.delete(ShowContract.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case SHOW_ID:
                String noteId = uri.getPathSegments().get(1);
                String whereId = String.format("%s = %s", ShowContract.ShowColumns._ID, noteId);
                String where = !TextUtils.isEmpty(selection)
                        ? String.format(" AND (%s)", selection)
                        : "";
                count = db.delete(ShowContract.TABLE_NAME, whereId + where, selectionArgs);
                getContext().getContentResolver().notifyChange(ShowContract.ShowColumns.CONTENT_URI, null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SHOWS:
                count = db.update(ShowContract.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            case SHOW_ID:
                String noteId = uri.getPathSegments().get(1);
                String whereId = String.format("%s = %s", ShowContract.ShowColumns._ID, noteId);
                String where = !TextUtils.isEmpty(selection)
                        ? String.format(" AND (%s)", selection)
                        : "";
                count = db.update(ShowContract.TABLE_NAME, values, whereId + where, selectionArgs);
                getContext().getContentResolver().notifyChange(ShowContract.ShowColumns.CONTENT_URI, null);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }
}
