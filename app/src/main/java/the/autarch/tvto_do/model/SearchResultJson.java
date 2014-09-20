package the.autarch.tvto_do.model;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import the.autarch.tvto_do.provider.ShowContract;

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

    public ContentValues toContentValues() {
        ContentValues cvs = new ContentValues();
        cvs.put(ShowContract.ShowColumns.TITLE, title);
        cvs.put(ShowContract.ShowColumns.YEAR, year);
        cvs.put(ShowContract.ShowColumns.URL, url);
        cvs.put(ShowContract.ShowColumns.COUNTRY, country);
        cvs.put(ShowContract.ShowColumns.OVERVIEW, overview);
        cvs.put(ShowContract.ShowColumns.IMDB_ID, imdb_id);
        cvs.put(ShowContract.ShowColumns.TVDB_ID, tvdb_id);
        cvs.put(ShowContract.ShowColumns.TVRAGE_ID, tvrage_id);
        cvs.put(ShowContract.ShowColumns.ENDED, ended);
        cvs.put(ShowContract.ShowColumns.POSTER_138_URL, getPoster138Url());
        cvs.put(ShowContract.ShowColumns.POSTER_300_URL, getPoster300Url());
        return cvs;
    }
}
