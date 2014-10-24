package the.autarch.tvto_do.model.database;

import com.j256.ormlite.dao.Dao;

import the.autarch.tvto_do.model.gson.ExtendedInfoGson;

/**
 * Created by jpierce on 9/20/14.
 */
public interface ShowDao extends Dao<Show, Integer> {
    public void createInBackground(Show show);
    public void updateInBackground(Show show);
    public void deleteInBackground(Show show);
    public void updateExtendedInfoInBackground(ExtendedInfoGson extendedInfoGson);
}
