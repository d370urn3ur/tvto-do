package the.autarch.tvto_do.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import the.autarch.tvto_do.model.SearchResultJson;

public class SearchRequest extends RetrofitSpiceRequest<SearchResultJson.List, TraktRest> {

    private String _searchQuery;

    public SearchRequest(String searchQuery) {
        super(SearchResultJson.List.class, TraktRest.class);
        _searchQuery = searchQuery;
    }

    @Override
    public SearchResultJson.List loadDataFromNetwork() throws Exception {
        return getService().searchForShow(_searchQuery);
    }

    public String createCacheKey() {
        return "search." + _searchQuery;
    }
}
