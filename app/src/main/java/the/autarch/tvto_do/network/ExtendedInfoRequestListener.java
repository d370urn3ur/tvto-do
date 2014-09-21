package the.autarch.tvto_do.network;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.gson.ExtendedInfoGson;

/**
 * Created by jpierce on 9/21/14.
 */
public class ExtendedInfoRequestListener implements RequestListener<ExtendedInfoGson> {

    private Show show;

    public ExtendedInfoRequestListener(Show show) {
        this.show = show;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.e(getClass().getSimpleName(), "got error in extended info request: " + spiceException);
    }

    @Override
    public void onRequestSuccess(ExtendedInfoGson extendedInfoWrapper) {
        show.updateWithExtendedInfo(extendedInfoWrapper);
        TVTDApplication.model().getShowDao().updateInBackground(show);
    }
}
