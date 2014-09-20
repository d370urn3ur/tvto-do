package the.autarch.tvto_do.model;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResultJson {

    public static class List extends ArrayList<SearchResultJson> {
    }

    @SerializedName("title") public String title;
    @SerializedName("year") public String year;
    @SerializedName("url") public String url;
    @SerializedName("country") public String country;
    @SerializedName("overview") public String overview;
    @SerializedName("imdb_id") public String imdb_id;
    @SerializedName("tvdb_id") public String tvdb_id;
    @SerializedName("tvrage_id") public String tvrage_id;
    @SerializedName("ended") private String ended;
    @SerializedName("images") private ImagesWrapper images;
	
	public class ImagesWrapper {
        @SerializedName("poster") public String poster;
	}
	
	// derived properties
	public boolean hasPoster() {
		return images.poster != null;
	}
	
	public boolean hasEnded() {
		return Boolean.parseBoolean(ended);
	}
	
	public String prettyStatus() {
		return hasEnded() ? "Ended" : "";
	}
	
	public String getPoster138Url() {
		if(!hasPoster()) {
			return null;
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
		
		if(images.poster.contains(".")) {
			int idx = images.poster.lastIndexOf(".");
			StringBuilder result = new StringBuilder(images.poster);
			result.insert(idx, "-300");
			return result.toString();
		}
		
		return null;
	}

    public Show toShow() {

        Show show = new Show();
        show.setTitle(title);
        show.setYear(year);
        show.setUrl(url);
        show.setCountry(country);
        show.setOverview(overview);
        show.setImdbId(imdb_id);
        show.setTvdbId(tvdb_id);
        show.setTvrageId(tvrage_id);
        show.setPoster138Url(getPoster138Url());
        show.setPoster300Url(getPoster300Url());
        show.setExtendedInfoStatus(Show.ExtendedInfoStatus.EXTENDED_INFO_UNKNOWN);

        return show;
    }
}
