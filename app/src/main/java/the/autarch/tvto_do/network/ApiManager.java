package the.autarch.tvto_do.network;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import the.autarch.tvto_do.model.gson.SearchResultGson;

/**
 * Created by joshua.pierce on 23/10/14.
 */
public class ApiManager {

    private static final String BASE_URL = "http://api.trakt.tv";
    private static final String TRAKT_API_KEY = "3be333da6a72543854309bf4a1894679";

    private static RequestInterceptor _requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addPathParam("apiKey", TRAKT_API_KEY);
        }
    };

    private static final RestAdapter _restAdapter =
            new RestAdapter
                    .Builder()
                    .setEndpoint(BASE_URL)
                    .setRequestInterceptor(_requestInterceptor)
                    .build();

    private static final TraktRetroService _traktService = _restAdapter.create(TraktRetroService.class);

    private interface TraktRetroService {

        //	http://api.trakt.tv/search/shows.format/apikey?query=query&limit=limit&seasons=seasons
        @GET("/search/shows.json/{apiKey}")
        public Observable<SearchResultGson.List> searchForShow(@Query("query") String query);
    }

    public static Observable<SearchResultGson.List> searchForShow(String query) {
        return _traktService.searchForShow(query);
    }

}
