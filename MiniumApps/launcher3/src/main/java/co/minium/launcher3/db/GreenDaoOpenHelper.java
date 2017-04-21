package co.minium.launcher3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

/**
 * Created by tkb on 2017-04-12.
 */

public class GreenDaoOpenHelper extends DaoMaster.OpenHelper {
    public GreenDaoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // MigrationHelper.migrate(db,TableNotificationSmsDao.class);
        switch (oldVersion) {
            case 1:
                // no statement
            case 2:
                MigrationHelper.migrate(db, StatusBarNotificationStorageDao.class);
            case 3:
        }

    }
}