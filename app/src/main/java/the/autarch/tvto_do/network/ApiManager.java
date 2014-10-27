package the.autarch.tvto_do.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import rx.Observable;
import the.autarch.tvto_do.model.ExtendedInfoGson;
import the.autarch.tvto_do.model.SearchResultGson;

/**
 * Created by joshua.pierce on 23/10/14.
 */
public class ApiManager {

    /*********** TRAKT SERVICE ************/

    private static final String BASE_TRAKT_URL = "http://api.trakt.tv";
    private static final String TRAKT_API_KEY = "3be333da6a72543854309bf4a1894679";

    private static RequestInterceptor _requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addPathParam("apiKey", TRAKT_API_KEY);
        }
    };

    private static final RestAdapter _traktRestAdapter =
            new RestAdapter
                    .Builder()
                    .setEndpoint(BASE_TRAKT_URL)
                    .setRequestInterceptor(_requestInterceptor)
                    .build();

    private interface TraktRetroService {

        //	http://api.trakt.tv/search/shows.format/apikey?query=query&limit=limit&seasons=seasons
        @GET("/search/shows.json/{apiKey}")
        public Observable<SearchResultGson.List> searchForShow(@Query("query") String query);
    }

    private static final TraktRetroService _traktService = _traktRestAdapter.create(TraktRetroService.class);

    public static Observable<SearchResultGson.List> searchForShow(String query) {
        return _traktService.searchForShow(query);
    }


    /*********** RAGE SERVICE *************/

    private static final String BASE_RAGE_URL = "http://services.tvrage.com";
//    private static final String TV_RAGE_API_KEY = "YDtBVboidHSwVGOvrLgK";

    private static final RestAdapter _rageRestAdapter =
            new RestAdapter
                    .Builder()
                    .setEndpoint(BASE_RAGE_URL)
                    .setConverter(new RageExtendedInfoConverter())
                    .build();

    private interface RageRetroService {

        @GET("/tools/quickinfo.php")
        public Observable<ExtendedInfoGson> getExtendedInfo(@Query("sid") String showId);
    }

    public static final RageRetroService _rageService = _rageRestAdapter.create(RageRetroService.class);

    public static Observable<ExtendedInfoGson> getExtendedInfo(String showId) {
        return _rageService.getExtendedInfo(showId);
    }

    static class RageExtendedInfoConverter implements Converter {

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {

            Reader r = null;
            try {

                r = new InputStreamReader(body.in(), "UTF-8");

                char[] buf = new char[2048];
                StringBuilder s = new StringBuilder();
                while (true) {
                    int n = r.read(buf);
                    if (n < 0)
                        break;
                    s.append(buf, 0, n);
                }

                HashMap<String, String> values = new HashMap<String,String>();
                String strippedString = s.toString().replace("<pre>", "");
                String[] items = strippedString.split("\n");
                String trimmed;
                for(String i : items) {
                    trimmed = i.trim();
                    String[] kv = trimmed.split("@");
                    if(kv.length > 1) {
                        values.put(kv[0], kv[1]);
                    }
                }
                ExtendedInfoGson extInfo = ExtendedInfoGson.parseValues(values);
                return extInfo;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    r.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        public TypedOutput toBody(Object object) {
            // TODO: i think this is only used for uploading
            return null;
        }
    };

}
