package the.autarch.tvto_do.model;

import android.text.format.Time;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;

/**
 * Created by jpierce on 9/20/14.
 */
public class ExtendedInfoStatusPersister extends BaseDataType {

    private static final ExtendedInfoStatusPersister instance = new ExtendedInfoStatusPersister();

    public static ExtendedInfoStatusPersister getSingleton() {
        return instance;
    }

    private ExtendedInfoStatusPersister() {
        super(SqlType.INTEGER, new Class<?>[] { Show.ExtendedInfoStatus.class });
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if (javaObject == null) {
            return null;
        } else {
            return ((Show.ExtendedInfoStatus) javaObject).getValue();
        }
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getInt(columnPos);
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return Integer.parseInt(defaultStr);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Integer value = (Integer)sqlArg;
        if (value == null) {
            return null;
        } else {
            return Show.ExtendedInfoStatus.fromValue(value.byteValue());
        }
    }
}
