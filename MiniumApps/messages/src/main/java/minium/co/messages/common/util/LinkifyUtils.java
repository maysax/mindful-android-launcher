package minium.co.messages.common.util;

import android.text.util.Linkify;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by Shahab on 3/31/2016.
 */
public class LinkifyUtils {

    private static Pattern bitcoin = Pattern.compile("bitcoin:[1-9a-km-zA-HJ-NP-Z]{27,34}(\\?[a-zA-Z0-9$\\-_.+!*'(),%:;@&=]*)?");
    private static Pattern geo = Pattern.compile("geo:[-0-9.]+,[-0-9.]+[^ \t\n\"\':]*");
    private static Pattern market = Pattern.compile("market://[^ \t\n\"\':,<>]+");
    private static Pattern openpgp4fpr = Pattern.compile("openpgp4fpr:[A-Za-z0-9]{8,40}");
    private static Pattern xmpp = Pattern.compile("xmpp:[^ \t\n\"\':,<>]+");
    private static Pattern twitterHandle = Pattern.compile("@([A-Za-z0-9_-]+)");
    private static Pattern hashtag = Pattern.compile("#([A-Za-z0-9_-]+)");


    /* Right now, if there is no app to handle */
    public static void addLinks(TextView text) {
        Linkify.addLinks(text, Linkify.ALL);
        Linkify.addLinks(text, geo, null);
        Linkify.addLinks(text, market, null);
        Linkify.addLinks(text, openpgp4fpr, null);
        Linkify.addLinks(text, bitcoin, null);
        Linkify.addLinks(text, xmpp, null);
/* SKIP        Linkify.addLinks(text, twitterHandle, "https://twitter.com/", null, returnMatchFilter);
        Linkify.addLinks(text, hashtag, "https://twitter.com/hashtag/", null, returnMatchFilter);
        text.setText(replaceAll(text.getText(), URLSpan.class, new URLSpanConverter()));*/
    }
}
