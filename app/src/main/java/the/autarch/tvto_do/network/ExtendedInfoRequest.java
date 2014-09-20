package the.autarch.tvto_do.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import the.autarch.tvto_do.model.ExtendedInfoWrapper;

public class ExtendedInfoRequest extends RetrofitSpiceRequest<ExtendedInfoWrapper, TvRageRest> {

    private String _tvRageId;

    public ExtendedInfoRequest(String tvRageId) {
        super(ExtendedInfoWrapper.class, TvRageRest.class);
        _tvRageId = tvRageId;
    }

    @Override
    public ExtendedInfoWrapper loadDataFromNetwork() throws Exception {
        return getService().getExtendedInfo(_tvRageId);
    }

    public String createCacheKey() {
        return "extendedInfo." + _tvRageId;
    }
}
