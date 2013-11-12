package rw.asfki.util.fieldComparable;

/**
 * User: Siarhei Yanusheusky
 * Date: 11.10.13
 * Time: 8:50
 */
public class StringComparable implements FieldComparable<StringComparable> {
    private String value;


    public StringComparable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    @Override
    public String compareFields(StringComparable field) {
        return String.format(ComparableMessages.STRING_MESSAGE, field.getValue(), value);
    }
}
