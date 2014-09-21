package the.autarch.tvto_do.model.database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by jpierce on 9/21/14.
 */
public class DatePersister extends BaseDataType {

    private static final DatePersister instance = new DatePersister();

    public static DatePersister getSingleton() {
        return instance;
    }

    private DatePersister() {
        super(SqlType.LONG, new Class<?>[] { Date.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) {
            return null;
        } else {
            long millis = ((Date)javaObject).getTime();
            return Long.valueOf(millis);
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
            return new Date(millis);
        }
    }
}
