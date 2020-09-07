package co.siempo.phone.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TABLE_NOTIFICATION_SMS".
*/
public class TableNotificationSmsDao extends AbstractDao<TableNotificationSms, Long> {

    public static final String TABLENAME = "TABLE_NOTIFICATION_SMS";

    /**
     * Properties of entity TableNotificationSms.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property _contact_title = new Property(1, String.class, "_contact_title", false, "_CONTACT_TITLE");
        public final static Property _message = new Property(2, String.class, "_message", false, "_MESSAGE");
        public final static Property _date = new Property(3, java.util.Date.class, "_date", false, "_DATE");
        public final static Property _contact_id = new Property(4, Integer.class, "_contact_id", false, "_CONTACT_ID");
        public final static Property _sms_id = new Property(5, Integer.class, "_sms_id", false, "_SMS_ID");
        public final static Property _snooze_time = new Property(6, Long.class, "_snooze_time", false, "_SNOOZE_TIME");
        public final static Property _is_read = new Property(7, Boolean.class, "_is_read", false, "_IS_READ");
        public final static Property App_icon = new Property(8, int.class, "app_icon", false, "APP_ICON");
        public final static Property User_icon = new Property(9, byte[].class, "user_icon", false, "USER_ICON");
        public final static Property Notification_type = new Property(10, Integer.class, "notification_type", false, "NOTIFICATION_TYPE");
        public final static Property PackageName = new Property(11, String.class, "packageName", false, "PACKAGE_NAME");
        public final static Property Content_type = new Property(12, int.class, "content_type", false, "CONTENT_TYPE");
        public final static Property Notification_id = new Property(13, int.class, "notification_id", false, "NOTIFICATION_ID");
        public final static Property Notification_date = new Property(14, long.class, "notification_date", false, "NOTIFICATION_DATE");
    }


    public TableNotificationSmsDao(DaoConfig config) {
        super(config);
    }
    
    public TableNotificationSmsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TABLE_NOTIFICATION_SMS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"_CONTACT_TITLE\" TEXT," + // 1: _contact_title
                "\"_MESSAGE\" TEXT," + // 2: _message
                "\"_DATE\" INTEGER," + // 3: _date
                "\"_CONTACT_ID\" INTEGER," + // 4: _contact_id
                "\"_SMS_ID\" INTEGER," + // 5: _sms_id
                "\"_SNOOZE_TIME\" INTEGER," + // 6: _snooze_time
                "\"_IS_READ\" INTEGER," + // 7: _is_read
                "\"APP_ICON\" INTEGER NOT NULL ," + // 8: app_icon
                "\"USER_ICON\" BLOB," + // 9: user_icon
                "\"NOTIFICATION_TYPE\" INTEGER," + // 10: notification_type
                "\"PACKAGE_NAME\" TEXT," + // 11: packageName
                "\"CONTENT_TYPE\" INTEGER NOT NULL ," + // 12: content_type
                "\"NOTIFICATION_ID\" INTEGER NOT NULL ," + // 13: notification_id
                "\"NOTIFICATION_DATE\" INTEGER NOT NULL );"); // 14: notification_date
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TABLE_NOTIFICATION_SMS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TableNotificationSms entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String _contact_title = entity.get_contact_title();
        if (_contact_title != null) {
            stmt.bindString(2, _contact_title);
        }
 
        String _message = entity.get_message();
        if (_message != null) {
            stmt.bindString(3, _message);
        }
 
        java.util.Date _date = entity.get_date();
        if (_date != null) {
            stmt.bindLong(4, _date.getTime());
        }
 
        Integer _contact_id = entity.get_contact_id();
        if (_contact_id != null) {
            stmt.bindLong(5, _contact_id);
        }
 
        Integer _sms_id = entity.get_sms_id();
        if (_sms_id != null) {
            stmt.bindLong(6, _sms_id);
        }
 
        Long _snooze_time = entity.get_snooze_time();
        if (_snooze_time != null) {
            stmt.bindLong(7, _snooze_time);
        }
 
        Boolean _is_read = entity.get_is_read();
        if (_is_read != null) {
            stmt.bindLong(8, _is_read ? 1L: 0L);
        }
        stmt.bindLong(9, entity.getApp_icon());
 
        byte[] user_icon = entity.getUser_icon();
        if (user_icon != null) {
            stmt.bindBlob(10, user_icon);
        }
 
        Integer notification_type = entity.getNotification_type();
        if (notification_type != null) {
            stmt.bindLong(11, notification_type);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(12, packageName);
        }
        stmt.bindLong(13, entity.getContent_type());
        stmt.bindLong(14, entity.getNotification_id());
        stmt.bindLong(15, entity.getNotification_date());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TableNotificationSms entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String _contact_title = entity.get_contact_title();
        if (_contact_title != null) {
            stmt.bindString(2, _contact_title);
        }
 
        String _message = entity.get_message();
        if (_message != null) {
            stmt.bindString(3, _message);
        }
 
        java.util.Date _date = entity.get_date();
        if (_date != null) {
            stmt.bindLong(4, _date.getTime());
        }
 
        Integer _contact_id = entity.get_contact_id();
        if (_contact_id != null) {
            stmt.bindLong(5, _contact_id);
        }
 
        Integer _sms_id = entity.get_sms_id();
        if (_sms_id != null) {
            stmt.bindLong(6, _sms_id);
        }
 
        Long _snooze_time = entity.get_snooze_time();
        if (_snooze_time != null) {
            stmt.bindLong(7, _snooze_time);
        }
 
        Boolean _is_read = entity.get_is_read();
        if (_is_read != null) {
            stmt.bindLong(8, _is_read ? 1L: 0L);
        }
        stmt.bindLong(9, entity.getApp_icon());
 
        byte[] user_icon = entity.getUser_icon();
        if (user_icon != null) {
            stmt.bindBlob(10, user_icon);
        }
 
        Integer notification_type = entity.getNotification_type();
        if (notification_type != null) {
            stmt.bindLong(11, notification_type);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(12, packageName);
        }
        stmt.bindLong(13, entity.getContent_type());
        stmt.bindLong(14, entity.getNotification_id());
        stmt.bindLong(15, entity.getNotification_date());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TableNotificationSms readEntity(Cursor cursor, int offset) {
        TableNotificationSms entity = new TableNotificationSms( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // _contact_title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // _message
            cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)), // _date
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // _contact_id
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // _sms_id
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // _snooze_time
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // _is_read
            cursor.getInt(offset + 8), // app_icon
            cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9), // user_icon
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // notification_type
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // packageName
            cursor.getInt(offset + 12), // content_type
            cursor.getInt(offset + 13), // notification_id
            cursor.getLong(offset + 14) // notification_date
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TableNotificationSms entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.set_contact_title(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.set_message(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.set_date(cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)));
        entity.set_contact_id(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.set_sms_id(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.set_snooze_time(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.set_is_read(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setApp_icon(cursor.getInt(offset + 8));
        entity.setUser_icon(cursor.isNull(offset + 9) ? null : cursor.getBlob(offset + 9));
        entity.setNotification_type(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setPackageName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setContent_type(cursor.getInt(offset + 12));
        entity.setNotification_id(cursor.getInt(offset + 13));
        entity.setNotification_date(cursor.getLong(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TableNotificationSms entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TableNotificationSms entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TableNotificationSms entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}