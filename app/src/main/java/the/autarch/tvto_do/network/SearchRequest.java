package the.autarch.tvto_do.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import the.autarch.tvto_do.model.gson.SearchResultGson;

public class SearchRequest extends RetrofitSpiceRequest<SearchResultGson.List, TraktRest> {

    private String _searchQuery;

    public SearchRequest(String searchQuery) {
        super(SearchResultGson.List.class, TraktRest.class);
        _searchQuery = searchQuery;
    }

    @Override
    public SearchResultGson.List loadDataFromNetwork() throws Exception {
        return getService().searchForShow(_searchQuery);
    }

    public String createCacheKey() {
        return "search." + _searchQuery;
    }
}
