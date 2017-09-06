package co.siempo.phone.token;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by shahab on 2/16/17.
 */
@SuppressWarnings("ALL")
@EBean
public class TokenParser {

    @Bean
    TokenManager manager;

    @Bean
    TokenRouter router;

    public void parse(String str) {
        if (str.isEmpty()) {
            manager.clear();
        } else if (str.equals("@") && !manager.hasCompleted(TokenItemType.CONTACT)) {
            router.setCurrent(new TokenItem(TokenItemType.CONTACT));
        }else {
            for (TokenItem item : manager.getItems()) {
                if (item.getCompleteType() == TokenCompleteType.FULL) {
                    str = str.substring(item.getTitle().length() + 1);
                }
            }
            if (str.endsWith("@")) {
                return;
            }

            if (str.endsWith("@") && !manager.hasCompleted(TokenItemType.CONTACT)) {
                router.add(new TokenItem(TokenItemType.CONTACT));
            } else {
                manager.getCurrent().setTitle(str);
            }

            if (manager.get(0).getItemType() == TokenItemType.CONTACT && manager.getCurrent().getItemType() == TokenItemType.DATA) {
                if (!manager.getCurrent().getTitle().trim().isEmpty()) router.route();
            }
        }
    }
}
