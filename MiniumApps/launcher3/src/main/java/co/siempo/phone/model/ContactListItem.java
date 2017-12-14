package co.siempo.phone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahab on 2/16/2017.
 */

public class ContactListItem extends MainListItem {

    private long contactId;
    private String contactName;
    private List<ContactNumber> numbers;
    private String imageUri;

    public ContactListItem(long contactId, String contactName) {
        super(0, contactName, "{fa-user-o}", MainListItemType.CONTACT);
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

    public ContactNumber getNumber() {
        return numbers.get(0);
    }

    public void addNumbers(String label, String number) {
        if (!isNumberExists(number))
            getNumbers().add(new ContactNumber(number, label));

    }

    private boolean isNumberExists(String number) {
        String strNum = number.replaceAll("[\\D]", "");
        if (getNumbers() != null) {
            for (ContactNumber strNumber : getNumbers()) {
                String str2 = strNumber.getNumber().replaceAll("[\\D]", "");
                if (str2.equals(strNum))
                    return true;
                else
                    return false;

            }
        }
        return false;
    }


    public boolean hasMultipleNumber() {
        if (numbers == null) return false;
        if (numbers.isEmpty()) return false;
        if (numbers.size() == 1) return false;
        return true;
    }

    @Override
    public String toString() {
        return "ContactListItem{" +
                "contactId=" + contactId +
                ", contactName='" + contactName + '\'' +
                ", numbers=" + numbers +
                '}';
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public class ContactNumber {
        private String label;
        private String number;

        public ContactNumber(String number, String label) {
            this.number = number;
            this.label = label;
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
