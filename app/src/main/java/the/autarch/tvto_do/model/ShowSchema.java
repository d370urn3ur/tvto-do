package the.autarch.tvto_do.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter interface for show data
 */
public final class ShowSchema extends HashMap<String, Object> {

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

    public ShowSchema() {
        super();
    }

    public ShowSchema(Map<String, Object> documentProperties) {
        super(documentProperties);
    }

    public ShowSchema(SearchResultGson searchResult) {
        super(searchResult.toMap());
    }



}
