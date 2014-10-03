package the.autarch.tvto_do.network;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;
import roboguice.util.temp.Ln;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.event.NetworkEvent;
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
        Ln.e(spiceException);
        EventBus.getDefault().post(new NetworkEvent(NetworkEvent.NetworkEventType.FAILURE, "Failed to update " + show.getTitle()));
    }

    @Override
    public void onRequestSuccess(ExtendedInfoGson extendedInfoWrapper) {
        show.updateWithExtendedInfo(extendedInfoWrapper);
        TVTDApplication.model().getShowDao().updateInBackground(show);
        EventBus.getDefault().post(new NetworkEvent(NetworkEvent.NetworkEventType.SUCCESS, "Finished updating " + show.getTitle()));
    }
}
