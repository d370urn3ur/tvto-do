package the.autarch.tvto_do.event;

import the.autarch.tvto_do.model.Show;

/**
 * Created by jpierce on 9/14/14.
 */
public class UpdateExtendedInfoEvent {

    private Show show;

    public UpdateExtendedInfoEvent(Show show) {
        this.show = show;
    }

    public Show getShow() {
        return show;
    }
}
