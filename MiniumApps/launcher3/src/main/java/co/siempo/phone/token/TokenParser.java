package co.siempo.phone.token;

/**
 * Created by shahab on 2/16/17.
 */
public class TokenParser {

    private TokenRouter router;

    public TokenParser(TokenRouter router) {
        this.router = router;
    }


    public void parse(String str) {
        String searchValue = str;
        if (str.isEmpty()) {
            TokenManager.getInstance().clear();
        } else if (str.equals("@") && !TokenManager.getInstance().hasCompleted(TokenItemType.CONTACT)) {

            router.setCurrent(new TokenItem(TokenItemType.CONTACT));
        } else {
            for (TokenItem item : TokenManager.getInstance().getItems()) {
                if (item.getCompleteType() == TokenCompleteType.FULL) {
                    str = str.substring(item.getTitle().length() + 1);
                }
            }
            if (str.endsWith("@") && TokenManager.getInstance().hasCompleted(TokenItemType.CONTACT)) {
                router.add(new TokenItem(TokenItemType.CONTACT));
            } else {
                TokenManager.getInstance().getCurrent().setTitle(str);
            }

        }
    }
}
