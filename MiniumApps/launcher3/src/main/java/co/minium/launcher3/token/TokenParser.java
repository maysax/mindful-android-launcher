package co.minium.launcher3.token;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

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
            return;
        }
        for (TokenItem item : manager.getItems()) {
            if (item.getCompleteType() == TokenCompleteType.FULL) {
                str = str.substring(item.getTitle().length() + 1);
            }
        }

        if (str.trim().equals("@")) {
            router.add(new TokenItem(TokenItemType.CONTACT));
            manager.getCurrent().setTitle("@");
        } else {
            manager.getCurrent().setTitle(str);
        }

    }
}
