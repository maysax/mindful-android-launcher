package minium.co.messages.data;

import android.content.Context;

import minium.co.core.log.LogConfig;

/**
 * Created by shahab on 3/25/16.
 */
public class Conversation {

    protected final String TRACE_TAG = LogConfig.TRACE_TAG + "Conversation";

    private final Context mContext;

    private Conversation(Context context) {
        mContext = context;
    }
}
