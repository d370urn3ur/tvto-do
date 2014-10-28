package the.autarch.tvto_do.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by joshua.pierce on 28/10/14.
 */
public class Show extends CBLObject {

    @CBLProperty(name=ShowSchema.KEY_TITLE)
    public String title;

    @CBLProperty(name=ShowSchema.KEY_YEAR)
    public String year;

    @CBLProperty(name=ShowSchema.KEY_URL)
    public String url;

    @CBLProperty(name=ShowSchema.KEY_COUNTRY)
    public String country;

    @CBLProperty(name=ShowSchema.KEY_OVERVIEW)
    public String overview;

    @CBLProperty(name=ShowSchema.KEY_IMDB_ID)
    public String imdbId;

    @CBLProperty(name=ShowSchema.KEY_TVDB_ID)
    public String tvdbId;

    @CBLProperty(name=ShowSchema.KEY_TVRAGE_ID)
    public String tvrageId;

    @CBLProperty(name=ShowSchema.KEY_ENDED)
    public Boolean ended;

    @CBLProperty(name=ShowSchema.KEY_POSTER)
    public String posterUrl;

    @CBLProperty(name=ShowSchema.KEY_NEXT_EPISODE_DATE)
    public Number nextEpisodeDate;

    @CBLProperty(name=ShowSchema.KEY_NEXT_EPISODE_TITLE)
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

    public boolean hasEnded() {
        return ended != null && ended;
    }

    private ShowSchema.ShowStatus getStatus() {
        if(hasEnded()) {
            return ShowSchema.ShowStatus.ENDED;
        }
        if(nextEpisodeDate == null) {
            return ShowSchema.ShowStatus.UNKNOWN;
        }

        if(nextEpisodeDate.longValue() < new Date().getTime()) {
            return ShowSchema.ShowStatus.OUT_OF_DATE;
        } else {
            return ShowSchema.ShowStatus.UP_TO_DATE;
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
