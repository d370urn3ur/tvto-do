package the.autarch.tvto_do.model;

public class SearchResultWrapper {
	
	public String title;
	public String year;
	public String url;
	public String country;
	public String overview;
	public String imdb_id;
	public String tvdb_id;
	public String tvrage_id;
	public String ended;
	public ImagesWrapper images;
	
	public static class ImagesWrapper {
		public String poster;
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
}
