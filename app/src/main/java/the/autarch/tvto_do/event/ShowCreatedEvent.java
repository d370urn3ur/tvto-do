package the.autarch.tvto_do.event;

import the.autarch.tvto_do.model.database.Show;

/**
 * Created by jpierce on 9/21/14.
 */
public class ShowCreatedEvent {

    private Show show;

    public ShowCreatedEvent(Show show) {
        this.show = show;
    }

    public Show getShow() {
        return show;
    }
}
