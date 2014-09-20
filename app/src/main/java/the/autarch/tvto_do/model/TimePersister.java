package the.autarch.tvto_do.model;

import android.text.format.Time;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;

/**
 * Created by jpierce on 9/13/14.
 */
public class TimePersister extends BaseDataType {

    private static final TimePersister instance = new TimePersister();

    public static TimePersister getSingleton() {
        return instance;
    }

    private TimePersister() {
        super(SqlType.LONG, new Class<?>[] { Time.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) {
            return null;
        } else {
            return ((Time) javaObject).toMillis(false);
        }
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getLong(columnPos);
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return Long.parseLong(defaultStr);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Long millis = (Long)sqlArg;
        if (millis == null) {
            return null;
        } else {
            Time t = new Time();
            t.set(millis);
            return t;
        }
    }
}
