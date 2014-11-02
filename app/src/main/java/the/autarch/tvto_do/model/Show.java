package the.autarch.tvto_do.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by joshua.pierce on 28/10/14.
 */
public class Show extends CBLObject {

    public enum ShowStatus {
        ENDED,
        OUT_OF_DATE,
        UP_TO_DATE,
        UNKNOWN
    }

    public static final String KEY_TITLE = "title";
    public static final String KEY_YEAR = "year";
    public static final String KEY_URL = "url";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_IMDB_ID = "imdb_id";
    public static final String KEY_TVDB_ID = "tvdb_id";
    public static final String KEY_TVRAGE_ID = "tvrage_id";
    public static final String KEY_ENDED = "ended";
    public static final String KEY_IMAGES = "images";

    public static final String KEY_NEXT_EPISODE_TITLE = "next_episode_title";
    public static final String KEY_NEXT_EPISODE_DATE = "next_episode_date";

    public static final String KEY_POSTER = "poster";

    @CBLPropertyName(KEY_TITLE)
    public String title;

    @CBLPropertyName(KEY_YEAR)
    public String year;

    @CBLPropertyName(KEY_URL)
    public String url;

    @CBLPropertyName(KEY_COUNTRY)
    public String country;

    @CBLPropertyName(KEY_OVERVIEW)
    public String overview;

    @CBLPropertyName(KEY_IMDB_ID)
    public String imdbId;

    @CBLPropertyName(KEY_TVDB_ID)
    public String tvdbId;

    @CBLPropertyName(KEY_TVRAGE_ID)
    public String tvrageId;

    @CBLPropertyName(KEY_ENDED)
    public Boolean ended;

    @CBLPropertyName(KEY_POSTER)
    public String posterUrl;

    @CBLPropertyName(KEY_NEXT_EPISODE_DATE)
    public Number nextEpisodeDate;

    @CBLPropertyName(KEY_NEXT_EPISODE_TITLE)
    public String nextEpisodeTitle;

    public Show(Map<String, Object> data) {
        super(data);
    }

    public Show(SearchResultGson searchResultGson) {
        super(searchResultGson.toMap());
    }

    public void updateExtendedInfo(ExtendedInfo extendedInfo) {
        for(Map.Entry<String, Object> entry : extendedInfo.toMap().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        annotate();
    }

    public String getId() {
        return (String)get("_id");
    }

    public boolean hasEnded() {
        return ended != null && ended;
    }

    private ShowStatus getStatus() {
        if(hasEnded()) {
            return ShowStatus.ENDED;
        }
        if(nextEpisodeDate == null) {
            return ShowStatus.UNKNOWN;
        }

        if(nextEpisodeDate.longValue() < new Date().getTime()) {
            return ShowStatus.OUT_OF_DATE;
        } else {
            return ShowStatus.UP_TO_DATE;
        }
    }

    public String prettyStatus() {
        switch(getStatus()) {
            case ENDED:
                return "Ended";
            case UNKNOWN:
                return "Unknown";
            case OUT_OF_DATE:
            case UP_TO_DATE:
            default:
                return getPrettyDate();
        }
    }

    public boolean isOutOfDate() {
        return getStatus() == ShowStatus.OUT_OF_DATE;
    }

    public String getPrettyDate() {
        if(nextEpisodeDate == null) {
            return "Unknown";
        }

        Date d = new Date(nextEpisodeDate.longValue());
        DateFormat df = DateFormat.getDateInstance();
        return df.format(d);
    }

    public String getPoster138Url() {

        if(posterUrl == null) {
            return null;
        }

        if(posterUrl.contains("poster-dark")) {
            return url;
        }

        if(posterUrl.contains(".")) {
            int idx = posterUrl.lastIndexOf(".");
            StringBuilder result = new StringBuilder(posterUrl);
            result.insert(idx, "-138");
            return result.toString();
        }

        return null;
    }

    public String getPoster300Url() {

        if(posterUrl == null) {
            return null;
        }

        if(posterUrl.contains("poster-dark")) {
            return posterUrl;
        }

        if(posterUrl.contains(".")) {
            int idx = posterUrl.lastIndexOf(".");
            StringBuilder result = new StringBuilder(posterUrl);
            result.insert(idx, "-300");
            return result.toString();
        }

        return null;
    }

    public String prettyNextEpisode() {
        if(hasEnded()) {
            return "";
        }
        if(nextEpisodeTitle == null) {
            return "Unknown";
        }
        return nextEpisodeTitle;
    }
}
