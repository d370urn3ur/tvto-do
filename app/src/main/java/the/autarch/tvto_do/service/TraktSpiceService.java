package the.autarch.tvto_do.service;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import the.autarch.tvto_do.network.TraktRest;

/**
 * Created by jpierce on 9/10/14.
 */
public class TraktSpiceService extends RetrofitGsonSpiceService {

    // TRAKT constants
    private static final String BASE_URL = "http://api.trakt.tv";
    private static final String TRAKT_API_KEY = "3be333da6a72543854309bf4a1894679";

    RequestInterceptor _requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addPathParam("apiKey", TRAKT_API_KEY);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(TraktRest.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder b = super.createRestAdapterBuilder();
        b.setRequestInterceptor(_requestInterceptor);
        return b;
    }
}
