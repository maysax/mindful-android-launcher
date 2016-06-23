package minium.co.launcher2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahab on 6/23/2016.
 */
public class ContactItem {

    private long contactId;
    private String contactName;
    private List<ContactNumber> numbers;

    public ContactItem() {
    }

    public ContactItem(long contactId, String contactName) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.numbers = new ArrayList<>();
    }

    public long getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public List<ContactNumber> getNumbers() {
        return numbers;
    }

    public void addNumbers(String label, String number) {
        getNumbers().add(new ContactNumber(number, label));
    }

    public boolean hasMultipleNumber() {
        if (numbers == null) return false;
        if (numbers.isEmpty()) return false;
        if (numbers.size() == 1) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ContactItem{" +
                "contactId=" + contactId +
                ", contactName='" + contactName + '\'' +
                ", numbers=" + numbers +
                '}';
    }

    public class ContactNumber {
        private String label;
        private String number;

        public ContactNumber(String number, String label) {
            this.number = number;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return "ContactNumber{" +
                    "label='" + label + '\'' +
                    ", number='" + number + '\'' +
                    '}';
        }
    }

}
