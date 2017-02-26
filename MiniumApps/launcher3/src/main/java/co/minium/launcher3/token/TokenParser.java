package co.minium.launcher3.token;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import co.minium.launcher3.event.AtFoundEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by shahab on 2/16/17.
 */
@EBean
public class TokenParser {

    @Bean
    TokenManager manager;

    @Bean
    TokenRouter router;

    public void parse(String str) {
        if (str.isEmpty()) {
            manager.clear();
        } else if (str.equals("@")) {
            router.setCurrent(new TokenItem(TokenItemType.CONTACT));
        } else {
            for (TokenItem item : manager.getItems()) {
                if (item.getCompleteType() == TokenCompleteType.FULL) {
                    str = str.substring(item.getTitle().length() + 1);
                }
            }

            if (str.endsWith("@")) {
                router.add(new TokenItem(TokenItemType.CONTACT));
            } else {
                manager.getCurrent().setTitle(str);
            }
        }
    }
}
