package the.autarch.tvto_do.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import the.autarch.tvto_do.model.ExtendedInfoGson;

public class ExtendedInfoRequest extends RetrofitSpiceRequest<ExtendedInfoGson, TvRageRest> {

    private String _tvRageId;

    public ExtendedInfoRequest(String tvRageId) {
        super(ExtendedInfoGson.class, TvRageRest.class);
        _tvRageId = tvRageId;
    }

    @Override
    public ExtendedInfoGson loadDataFromNetwork() throws Exception {
        return getService().getExtendedInfo(_tvRageId);
    }

    public String createCacheKey() {
        return "extendedInfo." + _tvRageId;
    }
}
