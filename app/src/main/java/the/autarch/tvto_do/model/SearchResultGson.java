package the.autarch.tvto_do.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchResultGson {

    public static class List extends ArrayList<SearchResultGson> {}

    @SerializedName(ShowSchema.KEY_TITLE) public String title;
    @SerializedName(ShowSchema.KEY_YEAR) public String year;
    @SerializedName(ShowSchema.KEY_URL) public String url;
    @SerializedName(ShowSchema.KEY_COUNTRY) public String country;
    @SerializedName(ShowSchema.KEY_OVERVIEW) public String overview;
    @SerializedName(ShowSchema.KEY_IMDB_ID) public String imdb_id;
    @SerializedName(ShowSchema.KEY_TVDB_ID) public String tvdb_id;
    @SerializedName(ShowSchema.KEY_TVRAGE_ID) public String tvrage_id;
    @SerializedName(ShowSchema.KEY_ENDED) private boolean ended;
    @SerializedName(ShowSchema.KEY_IMAGES) private ImagesWrapper images;
	
	public class ImagesWrapper {
        @SerializedName(ShowSchema.KEY_POSTER) public String poster;
	}

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>() {{
            put(ShowSchema.KEY_TITLE, title);
            put(ShowSchema.KEY_YEAR, year);
            put(ShowSchema.KEY_URL, url);
            put(ShowSchema.KEY_COUNTRY, country);
            put(ShowSchema.KEY_OVERVIEW, overview);
            put(ShowSchema.KEY_IMDB_ID, imdb_id);
            put(ShowSchema.KEY_TVDB_ID, tvdb_id);
            put(ShowSchema.KEY_TVRAGE_ID, tvrage_id);
            put(ShowSchema.KEY_ENDED, ended);
            if(images != null && images.poster != null) {
                put(ShowSchema.KEY_POSTER, images.poster);
            }
        }};
    }
}
