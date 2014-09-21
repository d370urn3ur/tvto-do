package the.autarch.tvto_do.event;

import java.sql.SQLException;

/**
 * Created by jpierce on 9/21/14.
 */
public class SQLErrorEvent {

    private SQLException error;

    public SQLErrorEvent(SQLException e) {
        error = e;
    }

    public SQLException getError() {
        return error;
    }
}
