package rw.asfki.domain;

/**
 * @author Yanusheusky S.
 *
 */
public class ColumnAttributes {
	private String typeAttribute;
	private String sizeAttribute;
	private String commentAttribute;
	private String nullableAttribute;
	private String formatAttribute;
	private String decimalDigitsAttribute;
	
	private ColumnAttributes(String typeAttribute, String sizeAttribute,
			String commentAttribute, String nullableAttribute,
			String formatAttribute, String decimalDigitsAttribute) {
		this.typeAttribute = typeAttribute;
		this.sizeAttribute = sizeAttribute;
		this.commentAttribute = commentAttribute;
		this.nullableAttribute = nullableAttribute;
		this.formatAttribute = formatAttribute;
		this.decimalDigitsAttribute = decimalDigitsAttribute;
	}
	
	public static ColumnAttributes getInstance(String typeAttribute, String sizeAttribute,
			String commentAttribute, String nullableAttribute,
			String formatAttribute, String decimalDigitsAttribute) {
		return new ColumnAttributes(typeAttribute,sizeAttribute,commentAttribute,nullableAttribute,formatAttribute,decimalDigitsAttribute);
	}
	public String getTypeAttribute() {
		return typeAttribute;
	}
	public void setTypeAttribute(String typeAttribute) {
		this.typeAttribute = typeAttribute;
	}
	public String getSizeAttribute() {
		return sizeAttribute;
	}
	public void setSizeAttribute(String sizeAttribute) {
		this.sizeAttribute = sizeAttribute;
	}
	public String getCommentAttribute() {
		return commentAttribute;
	}
	public void setCommentAttribute(String commentAttribute) {
		this.commentAttribute = commentAttribute;
	}
	public String getNullableAttribute() {
		return nullableAttribute;
	}
	public void setNullableAttribute(String nullableAttribute) {
		this.nullableAttribute = nullableAttribute;
	}
	public String getFormatAttribute() {
		return formatAttribute;
	}
	public void setFormatAttribute(String formatAttribute) {
		this.formatAttribute = formatAttribute;
	}
	public String getDecimalDigitsAttribute() {
		return decimalDigitsAttribute;
	}
	public void setDecimalDigitsAttribute(String decimalDigitsAttribute) {
		this.decimalDigitsAttribute = decimalDigitsAttribute;
	}
	
}
