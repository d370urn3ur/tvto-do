package the.autarch.tvto_do.model;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.activeandroid.annotation.Table;

@Table(name="shows", id="_id")
public class Show {

    public int id;
	public String title;
	public String year;
	public String url;
	public String country;
	public String overview;
	public String imdbId;
	public String tvdbId;
	public String tvrageId;
	public boolean ended;
	public String poster138Url;
	public String poster300Url;
	public String poster138filepath;
	public String poster300filepath;
	public boolean extendedInfoUpdated;

	// TVRage info
	public String nextEpisodeTitle;
	public Time nextEpisodeTime;

	// derived properties
	public boolean isOutOfDate() {
		
		if(ended) {
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
		if(ended) {
			return "Ended";
		}
		
		if(nextEpisodeTitle == null) {
			return "TBA";
		}
		
		return nextEpisodeTitle;
	}
	
	public String prettyNextDate(Context context, int flags) {
		if(ended) {
			return "N/A";
		}
		
		if(nextEpisodeTime == null) {
			return "TBA";
		}
		
		String prettyDate = DateUtils.formatDateTime(context,
				nextEpisodeTime.toMillis(false),
				flags);
		
		return prettyDate;
	}
	
	// data population
	public void hydrateFromSearchResult(SearchResultWrapper searchResult) {
		title = searchResult.title;
		year = searchResult.year;
		url = searchResult.url;
		country = searchResult.country;
		overview = searchResult.overview;
		imdbId = searchResult.imdb_id;
		tvdbId = searchResult.tvdb_id;
		tvrageId = searchResult.tvrage_id;
		ended = Boolean.parseBoolean(searchResult.ended);
		poster138Url = searchResult.getPoster138Url();
		poster300Url = searchResult.getPoster300Url();
	}
	
	public void updateWithExtendedInfo(ExtendedInfoWrapper extendedInfo) {
		if(extendedInfo == null) {
			return;
		}
		nextEpisodeTitle = extendedInfo.nextEpisodeTitle;
		nextEpisodeTime = extendedInfo.nextEpisodeTime;
		extendedInfoUpdated = true;
	}
}