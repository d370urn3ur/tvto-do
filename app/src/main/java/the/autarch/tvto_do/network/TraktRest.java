package the.autarch.tvto_do.network;

import retrofit.http.GET;
import retrofit.http.Query;
import the.autarch.tvto_do.model.gson.SearchResultGson;

/**
 * Created by jpierce on 9/10/14.
 */
public interface TraktRest {

    //	http://api.trakt.tv/search/shows.format/apikey?query=query&limit=limit&seasons=seasons
    @GET("/search/shows.json/{apiKey}")
    public SearchResultGson.List searchForShow(@Query("query") String query);

}
