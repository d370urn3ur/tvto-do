package the.autarch.tvto_do.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchResultGson {

    public static class List extends ArrayList<SearchResultGson> {}

    @SerializedName(Show.KEY_TITLE) public String title;
    @SerializedName(Show.KEY_YEAR) public String year;
    @SerializedName(Show.KEY_URL) public String url;
    @SerializedName(Show.KEY_COUNTRY) public String country;
    @SerializedName(Show.KEY_OVERVIEW) public String overview;
    @SerializedName(Show.KEY_IMDB_ID) public String imdb_id;
    @SerializedName(Show.KEY_TVDB_ID) public String tvdb_id;
    @SerializedName(Show.KEY_TVRAGE_ID) public String tvrage_id;
    @SerializedName(Show.KEY_ENDED) private boolean ended;
    @SerializedName(Show.KEY_IMAGES) private ImagesWrapper images;
	
	public class ImagesWrapper {
        @SerializedName(Show.KEY_POSTER) public String poster;
	}

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>() {{
            put(Show.KEY_TITLE, title);
            put(Show.KEY_YEAR, year);
            put(Show.KEY_URL, url);
            put(Show.KEY_COUNTRY, country);
            put(Show.KEY_OVERVIEW, overview);
            put(Show.KEY_IMDB_ID, imdb_id);
            put(Show.KEY_TVDB_ID, tvdb_id);
            put(Show.KEY_TVRAGE_ID, tvrage_id);
            put(Show.KEY_ENDED, ended);
            if(images != null && images.poster != null) {
                put(Show.KEY_POSTER, images.poster);
            }
        }};
    }
}
