package the.autarch.tvto_do.model;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import the.autarch.tvto_do.provider.ShowContract;

@DatabaseTable(tableName = ShowContract.TABLE_NAME)
public class Show {

    public enum ExtendedInfoStatus {
        EXTENDED_INFO_FIRST_FETCH(0), // fetching extended info for the first time
        EXTENDED_INFO_REFRESH(1),     // updating extended info
        EXTENDED_INFO_OUT_OF_DATE(2), //  the extended info status is out of date (wait for user to manually refresh)
        EXTENDED_INFO_UNKNOWN(3);      // the extended info status is unknown (attempt auto-refresh after elapsed time

        private int value;  // used for serialization

        ExtendedInfoStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ExtendedInfoStatus fromValue(int value) {
            for(ExtendedInfoStatus s : values()) {
                if(value == s.getValue()) {
                    return s;
                }
            }
            return EXTENDED_INFO_UNKNOWN;
        }
    }

    @DatabaseField(columnName = ShowContract.ShowColumns._ID, generatedId = true) private int _id;
    @DatabaseField(columnName = ShowContract.ShowColumns.TITLE) private String title;
    @DatabaseField(columnName = ShowContract.ShowColumns.YEAR) private String year;
    @DatabaseField(columnName = ShowContract.ShowColumns.URL) private String url;
    @DatabaseField(columnName = ShowContract.ShowColumns.COUNTRY) private String country;
    @DatabaseField(columnName = ShowContract.ShowColumns.OVERVIEW) private String overview;
    @DatabaseField(columnName = ShowContract.ShowColumns.IMDB_ID) private String imdbId;
    @DatabaseField(columnName = ShowContract.ShowColumns.TVDB_ID) private String tvdbId;
    @DatabaseField(columnName = ShowContract.ShowColumns.TVRAGE_ID) private String tvrageId;
    @DatabaseField(columnName = ShowContract.ShowColumns.ENDED) private boolean ended;
    @DatabaseField(columnName = ShowContract.ShowColumns.POSTER_138_URL) private String poster138Url;
    @DatabaseField(columnName= ShowContract.ShowColumns.POSTER_300_URL) private String poster300Url;
    @DatabaseField(columnName= ShowContract.ShowColumns.POSTER_138_FILEPATH) private String poster138filepath;
    @DatabaseField(columnName=ShowContract.ShowColumns.POSTER_300_FILEPATH) private String poster300filepath;

	// TVRage extended info
    @DatabaseField(columnName=ShowContract.ShowColumns.EXTENDED_INFO_STATUS, persisterClass = ExtendedInfoStatusPersister.class) private ExtendedInfoStatus extendedInfoStatus;
    @DatabaseField(columnName= ShowContract.ShowColumns.NEXT_EPISODE_TITLE) private String nextEpisodeTitle;
    @DatabaseField(columnName= ShowContract.ShowColumns.NEXT_EPISODE_TIME, persisterClass = TimePersister.class) private Time nextEpisodeTime;

    public Show() {
        // required no-args constructor for ORMLite
    }

    public Show(Cursor c) {
        try {
            _id = c.getInt(c.getColumnIndex(ShowContract.ShowColumns._ID));
            title = c.getString(c.getColumnIndex(ShowContract.ShowColumns.TITLE));
            year = c.getString(c.getColumnIndex(ShowContract.ShowColumns.YEAR));
            url = c.getString(c.getColumnIndex(ShowContract.ShowColumns.URL));
            country = c.getString(c.getColumnIndex(ShowContract.ShowColumns.COUNTRY));
            overview = c.getString(c.getColumnIndex(ShowContract.ShowColumns.OVERVIEW));
            imdbId = c.getString(c.getColumnIndex(ShowContract.ShowColumns.IMDB_ID));
            tvdbId = c.getString(c.getColumnIndex(ShowContract.ShowColumns.TVDB_ID));
            tvrageId = c.getString(c.getColumnIndex(ShowContract.ShowColumns.TVRAGE_ID));
            ended = c.getInt(c.getColumnIndex(ShowContract.ShowColumns.ENDED)) != 0;
            poster138Url = c.getString(c.getColumnIndex(ShowContract.ShowColumns.POSTER_138_URL));
            poster300Url = c.getString(c.getColumnIndex(ShowContract.ShowColumns.POSTER_300_URL));
            poster138filepath = c.getString(c.getColumnIndex(ShowContract.ShowColumns.POSTER_138_FILEPATH));
            poster300filepath = c.getString(c.getColumnIndex(ShowContract.ShowColumns.POSTER_300_FILEPATH));
            extendedInfoUpdated= c.getInt(c.getColumnIndex(ShowContract.ShowColumns.EXTENDED_INFO_UPDATED)) != 0;
            nextEpisodeTitle = c.getString(c.getColumnIndex(ShowContract.ShowColumns.NEXT_EPISODE_TITLE));

            long millis = c.getLong(c.getColumnIndex(ShowContract.ShowColumns.NEXT_EPISODE_TIME));
            if(millis > 0) {
                nextEpisodeTime = new Time();
                nextEpisodeTime.set(millis);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

	// derived properties
	public boolean isOutOfDate() {
		
		if(ended) {
			return true;
		}
		
		if(nextEpisodeTime == null) {
			return false;
		}
		
		Time now = new Time();
		now.setToNow();
		return now.after(nextEpisodeTime);
	}

	public String prettyNextEpisode() {
		if(ended) {
			return "Ended";
		}
		
		if(TextUtils.isEmpty(nextEpisodeTitle)) {
			return "TBA";
		}
		
		return nextEpisodeTitle;
	}

	public String prettyNextDate(Context context, int flags) {
		if(ended) {
			return "N/A";
		}
		
		if(nextEpisodeTime == null) {
			return "TBA";
		}
		
		String prettyDate = DateUtils.formatDateTime(context, nextEpisodeTime.toMillis(false), flags);
		
		return prettyDate;
	}
	
	public void updateWithExtendedInfo(ExtendedInfoWrapper extendedInfo) {
		if(extendedInfo == null) {
			return;
		}
		nextEpisodeTitle = extendedInfo.nextEpisodeTitle;
		nextEpisodeTime = extendedInfo.nextEpisodeTime;
		extendedInfoUpdated = true;
	}

    public int getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getUrl() {
        return url;
    }

    public String getCountry() {
        return country;
    }

    public String getOverview() {
        return overview;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTvdbId() {
        return tvdbId;
    }

    public String getTvrageId() {
        return tvrageId;
    }

    public boolean isEnded() {
        return ended;
    }

    public String getPoster138Url() {
        return poster138Url;
    }

    public String getPoster300Url() {
        return poster300Url;
    }

    public String getPoster138filepath() {
        return poster138filepath;
    }

    public void setPoster138filepath(String filepath) {
        this.poster138filepath = filepath;
    }

    public String getPoster300filepath() {
        return poster300filepath;
    }

    public boolean isExtendedInfoUpdated() {
        return extendedInfoUpdated;
    }

    public String getNextEpisodeTitle() {
        return nextEpisodeTitle;
    }

    public Time getNextEpisodeTime() {
        return nextEpisodeTime;
    }
}