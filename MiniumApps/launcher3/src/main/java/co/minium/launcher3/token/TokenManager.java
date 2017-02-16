package co.minium.launcher3.token;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 2/16/2017.
 */
@EBean(scope = EBean.Scope.Singleton)
public class TokenManager {

    private List<TokenItem> items = new ArrayList<>();

    private String patternString;

    public void init() {
        items.clear();
        items.add(new TokenItem(TokenItemType.DATA));
    }

    public void clear() {
        init();
        fireEvent();
    }

    public TokenItem get(int pos) {
        return items.get(pos);
    }

    public void add(TokenItem item) {
        items.add(item);
    }

    public void deleteLast() {
        if (!items.isEmpty()) items.remove(items.size() - 1);
        if (!items.isEmpty()) items.remove(items.size() - 1);
        if (items.isEmpty()) clear();
    }

    public TokenItem getCurrent() {
        return items.get(items.size() - 1);
    }

    public void setCurrent(TokenItem item) {
        items.set(items.size() - 1, item);
    }

    public List<TokenItem> getItems() {
        return items;
    }

    public void fireEvent() {
        EventBus.getDefault().post(new TokenManagerEvent());
    }

    public boolean has(TokenItem item) {
        for (TokenItem token: items) {
            if (token.getItemType() == item.getItemType()) return true;
        }
        return false;
    }

    public int getLength() {
        return items.size();
    }

    public String getPattern() {
        return patternString;
    }

    public void setPattern(String pattern) {
        this.patternString = pattern;
    }
}
