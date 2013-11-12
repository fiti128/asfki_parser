package rw.asfki.util.fieldComparable;

/**
 * User: Siarhei Yanusheusky
 * Date: 11.10.13
 * Time: 8:28
 */
public class IntComparable implements FieldComparable<IntComparable> {
    private int value;

    public IntComparable(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    @Override
    public String compareFields(IntComparable field) {
        return String.format(ComparableMessages.INTEGER_MESSAGE, field.getValue(), value);
    }
}
