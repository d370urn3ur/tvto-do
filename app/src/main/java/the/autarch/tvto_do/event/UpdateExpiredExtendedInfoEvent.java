package the.autarch.tvto_do.event;

import java.util.List;

import the.autarch.tvto_do.model.database.Show;

/**
 * Created by jpierce on 9/21/14.
 */
public class UpdateExpiredExtendedInfoEvent {

    private List<Show> expiredShows;

    public UpdateExpiredExtendedInfoEvent(List<Show> shows) {
        expiredShows = shows;
    }

    public List<Show> getExpiredShows() {
        return expiredShows;
    }
}
