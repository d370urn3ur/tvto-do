package the.autarch.tvto_do.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jpierce on 9/13/14.
 */
public final class ShowContract {

    public static final String TABLE_NAME = "shows";

    private ShowContract() {
        // This class cannot be instantiated
    }

    /**
     * Shows table
     */
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

        public static final String DEFAULT_SORT_ORDER = TITLE + " asc";
    }

    public static final String[] allShowsProjection = new String[] {
            ShowColumns._ID,
            ShowColumns.TITLE,
            ShowColumns.YEAR,
            ShowColumns.URL,
            ShowColumns.COUNTRY,
            ShowColumns.OVERVIEW,
            ShowColumns.IMDB_ID,
            ShowColumns.TVDB_ID,
            ShowColumns.TVRAGE_ID,
            ShowColumns.POSTER_138_URL,
            ShowColumns.POSTER_300_URL,
            ShowColumns.POSTER_138_FILEPATH,
            ShowColumns.POSTER_300_FILEPATH,
            ShowColumns.EXTENDED_INFO_STATUS,
            ShowColumns.NEXT_EPISODE_TITLE,
            ShowColumns.NEXT_EPISODE_TIME
    };

}
