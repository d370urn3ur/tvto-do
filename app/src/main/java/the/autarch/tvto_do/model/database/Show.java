package the.autarch.tvto_do.model.database;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import the.autarch.tvto_do.model.gson.ExtendedInfoGson;

@DatabaseTable(tableName = Show.TABLE_NAME, daoClass = ShowDaoImpl.class)
public class Show {

    /**
     * Shows table Constants
     */
    public static final String TABLE_NAME = "shows";

    public static final class ShowColumns implements BaseColumns {

        // This class cannot be instantiated
        private ShowColumns() {}

        public static final String TITLE = "title";
        public static final String YEAR = "year";
        public static final String URL = "url";
        public static final String COUNTRY = "country";
        public static final String OVERVIEW = "overview";
        public static final String IMDB_ID = "imdb_id";
        public static final String TVDB_ID = "tvdb_id";
        public static final String TVRAGE_ID = "tvrage_id";
        public static final String POSTER_138_URL = "poster_138_url";
        public static final String POSTER_300_URL = "poster_300_url";
        public static final String POSTER_138_FILEPATH = "poster_138_filepath";
        public static final String POSTER_300_FILEPATH = "poster_300_filepath";
        public static final String EXTENDED_INFO_STATUS = "extended_info_status";
        public static final String NEXT_EPISODE_TITLE = "next_episode_title";
        public static final String NEXT_EPISODE_TIME = "next_episode_time";
        public static final String EXTENDED_INFO_LAST_UPDATE = "extended_info_last_update";

        public static final String DEFAULT_SORT_ORDER = TITLE;
        public static final boolean DEFAULT_SORT_ASCENDING = true;
    }

    public enum ExtendedInfoStatus {

        EXTENDED_INFO_REFRESHING(0),    // updated extended info
        EXTENDED_INFO_VALID(1),         // extended info is valid and up to date
        EXTENDED_INFO_OUT_OF_DATE(2),   //  the extended info is out of date (wait for user to manually refresh)
        EXTENDED_INFO_UNKNOWN(3),       // the extended info status is unknown (attempt auto-refresh after elapsed time)
        EXTENDED_INFO_ENDED(4);         // show has ended, don't attempt update

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

    @DatabaseField(columnName = ShowColumns._ID, generatedId = true) private int _id;
    @DatabaseField(columnName = ShowColumns.TITLE) private String title;
    @DatabaseField(columnName = ShowColumns.YEAR) private String year;
    @DatabaseField(columnName = ShowColumns.URL) private String url;
    @DatabaseField(columnName = ShowColumns.COUNTRY) private String country;
    @DatabaseField(columnName = ShowColumns.OVERVIEW) private String overview;
    @DatabaseField(columnName = ShowColumns.IMDB_ID) private String imdbId;
    @DatabaseField(columnName = ShowColumns.TVDB_ID) private String tvdbId;
    @DatabaseField(columnName = ShowColumns.TVRAGE_ID) private String tvrageId;
    @DatabaseField(columnName = ShowColumns.POSTER_138_URL) private String poster138Url;
    @DatabaseField(columnName= ShowColumns.POSTER_300_URL) private String poster300Url;
    @DatabaseField(columnName= ShowColumns.POSTER_138_FILEPATH) private String poster138filepath;
    @DatabaseField(columnName= ShowColumns.POSTER_300_FILEPATH) private String poster300filepath;

	// TVRage extended info
    @DatabaseField(columnName= ShowColumns.EXTENDED_INFO_STATUS, persisterClass = ExtendedInfoStatusPersister.class) private ExtendedInfoStatus extendedInfoStatus;
    @DatabaseField(columnName= ShowColumns.NEXT_EPISODE_TITLE) private String nextEpisodeTitle;
    @DatabaseField(columnName= ShowColumns.NEXT_EPISODE_TIME, persisterClass = TimePersister.class) private Time nextEpisodeTime;
    @DatabaseField(columnName=ShowColumns.EXTENDED_INFO_LAST_UPDATE, persisterClass=DatePersister.class) private Date extendedInfoLastUpdate;

    private GradientDrawable gradientBackground;
    private int titleColor;
    private int bodyColor;

    public boolean hasColorInfo() {
        return gradientBackground == null;
    }

    public void setColorInfo(GradientDrawable bg, int titleColor, int bodyColor) {
        this.gradientBackground = bg;
        this.titleColor = titleColor;
        this.bodyColor = bodyColor;
    }

    public GradientDrawable getGradientBackground() {
        if(gradientBackground == null) {
            return new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {Color.BLACK, Color.BLACK});
        }
        return gradientBackground;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getBodyColor() {
        return bodyColor;
    }

    public Show() {
        // required no-args constructor for ORMLite
    }

	// derived properties
    public boolean hasEnded() {
        return extendedInfoStatus == ExtendedInfoStatus.EXTENDED_INFO_ENDED;
    }

	public boolean isOutOfDate() {
		
		if(hasEnded()) {
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
		if(extendedInfoStatus == ExtendedInfoStatus.EXTENDED_INFO_ENDED) {
			return "Ended";
		}
		
		if(TextUtils.isEmpty(nextEpisodeTitle)) {
			return "TBA";
		}
		
		return nextEpisodeTitle;
	}

	public String prettyNextDate(Context context, int flags) {
		if(extendedInfoStatus == ExtendedInfoStatus.EXTENDED_INFO_ENDED) {
			return "N/A";
		}
		
		if(nextEpisodeTime == null) {
			return "TBA";
		}
		
		String prettyDate = DateUtils.formatDateTime(context, nextEpisodeTime.toMillis(false), flags);
		
		return prettyDate;
	}
	
	public void updateWithExtendedInfo(ExtendedInfoGson extendedInfo) {
		if(extendedInfo.hasInfo()) {
            if(extendedInfo.hasEnded()) {
                extendedInfoStatus = ExtendedInfoStatus.EXTENDED_INFO_ENDED;
                nextEpisodeTime = null;
                nextEpisodeTitle = null;
            } else {
                extendedInfoStatus = ExtendedInfoStatus.EXTENDED_INFO_VALID;
                nextEpisodeTitle = extendedInfo.nextEpisodeTitle;
                nextEpisodeTime = extendedInfo.nextEpisodeTime;
            }
        } else {
            extendedInfoStatus = ExtendedInfoStatus.EXTENDED_INFO_UNKNOWN;
            nextEpisodeTitle = null;
            nextEpisodeTime = null;
        }

        extendedInfoLastUpdate = new Date();
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

    public String getNextEpisodeTitle() {
        return nextEpisodeTitle;
    }

    public Time getNextEpisodeTime() {
        return nextEpisodeTime;
    }

    public ExtendedInfoStatus getExtendedInfoStatus() {
        return extendedInfoStatus;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setTvdbId(String tvdbId) {
        this.tvdbId = tvdbId;
    }

    public void setTvrageId(String tvrageId) {
        this.tvrageId = tvrageId;
    }

    public void setPoster138Url(String poster138Url) {
        this.poster138Url = poster138Url;
    }

    public void setPoster300Url(String poster300Url) {
        this.poster300Url = poster300Url;
    }

    public void setExtendedInfoStatus(ExtendedInfoStatus extendedInfoStatus) {
        this.extendedInfoStatus = extendedInfoStatus;
    }
}