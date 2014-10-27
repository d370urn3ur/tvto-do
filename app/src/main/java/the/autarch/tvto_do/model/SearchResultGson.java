package the.autarch.tvto_do.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchResultGson {

    public static class List extends ArrayList<SearchResultGson> {
    }

    public static final ArrayList<String> SHOW_CELL_PROJECTION = new ArrayList<String>() {{
        add("title");
        add("year");
        add("overview");
        add("tvrage_id");
        add("ended");
        add("images");
    }};
    public static final String KEY_POSTER_URL = "poster";

    @SerializedName("title") public String title;
    @SerializedName("year") public String year;
    @SerializedName("url") public String url;
    @SerializedName("country") public String country;
    @SerializedName("overview") public String overview;
    @SerializedName("imdb_id") public String imdb_id;
    @SerializedName("tvdb_id") public String tvdb_id;
    @SerializedName("tvrage_id") public String tvrage_id;
    @SerializedName("ended") private boolean ended;
    @SerializedName("images") private ImagesWrapper images;

    public Map<String, Object> getDocumentProperties() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, new TypeToken<HashMap<String, Object>>(){}.getType());
    }

    public static SearchResultGson fromDocumentProperties(Map<String, Object> properties) {
        Gson gson = new Gson();
        String json = gson.toJson(properties);
        return gson.fromJson(json, SearchResultGson.class);
    }
	
	public class ImagesWrapper {
        @SerializedName("poster") public String poster;
	}
	
	// derived properties
	public boolean hasPoster() {
		return images.poster != null;
	}
	
	public boolean hasEnded() {
		return ended;
	}
	
	public String prettyStatus() {
		return hasEnded() ? "Ended" : "";
	}
	
	public String getPoster138Url() {
		if(!hasPoster()) {
			return null;
		}

        if(images.poster.contains("poster-dark")) {
            return images.poster;
        }
		
		if(images.poster.contains(".")) {
			int idx = images.poster.lastIndexOf(".");
			StringBuilder result = new StringBuilder(images.poster);
			result.insert(idx, "-138");
			return result.toString();
		}
		
		return null;
	}
	
	public String getPoster300Url() {
		if(!hasPoster()) {
			return null;
		}

        if(images.poster.contains("poster-dark")) {
            return images.poster;
        }
		
		if(images.poster.contains(".")) {
			int idx = images.poster.lastIndexOf(".");
			StringBuilder result = new StringBuilder(images.poster);
			result.insert(idx, "-300");
			return result.toString();
		}
		
		return null;
	}
}
