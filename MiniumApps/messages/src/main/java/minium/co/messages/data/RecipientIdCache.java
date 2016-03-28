package minium.co.messages.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minium.co.core.config.Config;
import minium.co.core.log.Tracer;

/**
 * Created by shahab on 3/25/16.
 */
public class RecipientIdCache {

    private static Uri sAllCanonical =
            Uri.parse("content://mms-sms/canonical-addresses");

    private static RecipientIdCache sInstance;
    static RecipientIdCache getInstance() { return sInstance; }

    private final Map<Long, String> mCache;

    private final Context mContext;


    public static class Entry {
        public long id;
        public String number;

        public Entry(long id, String number) {
            this.id = id;
            this.number = number;
        }
    }

    static void init(Context context) {
        sInstance = new RecipientIdCache(context);
        new Thread(new Runnable() {
            public void run() {
                fill();
            }
        }, "RecipientIdCache.init").start();
    }

    RecipientIdCache(Context context) {
        mCache = new HashMap<Long, String>();
        mContext = context;
    }

    public static void fill() {
        Tracer.d("[RecipientIdCache] fill: begin");

        Context context = sInstance.mContext;
        Cursor c = SqliteWrapper.query(context, context.getContentResolver(),
                sAllCanonical, null, null, null, null);
        if (c == null) {
            Tracer.w( "null Cursor in fill()");
            return;
        }

        try {
            synchronized (sInstance) {
                // Technically we don't have to clear this because the stupid
                // canonical_addresses table is never GC'ed.
                sInstance.mCache.clear();
                while (c.moveToNext()) {
                    // TODO: don't hardcode the column indices
                    long id = c.getLong(0);
                    String number = c.getString(1);
                    sInstance.mCache.put(id, number);
                }
            }
        } finally {
            c.close();
        }

        Tracer.d("[RecipientIdCache] fill: finished");
        if (Config.DEBUG) dump();
    }

    public static List<Entry> getAddresses(String spaceSepIds) {
        synchronized (sInstance) {
            List<Entry> numbers = new ArrayList<>();
            String[] ids = spaceSepIds.split(" ");
            for (String id : ids) {
                long longId;

                try {
                    longId = Long.parseLong(id);
                } catch (NumberFormatException ex) {
                    // skip this id
                    continue;
                }

                String number = sInstance.mCache.get(longId);

                if (number == null) {
                    Tracer.w( "RecipientId " + longId + " not in cache!");
                    if (Config.DEBUG) dump();

                    fill();
                    number = sInstance.mCache.get(longId);
                }

                if (TextUtils.isEmpty(number)) {
                    Tracer.w("RecipientId " + longId + " has empty number!");
                } else {
                    numbers.add(new Entry(longId, number));
                }
            }
            return numbers;
        }
    }

    public static void dump() {
        // Only dump user private data if we're in special debug mode
        synchronized (sInstance) {
            Tracer.d("*** Recipient ID cache dump ***");
            for (Long id : sInstance.mCache.keySet()) {
                Tracer.d(id + ": " + sInstance.mCache.get(id));
            }
        }
    }
}
