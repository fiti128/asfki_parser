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
	private String decimalScaleAttribute;
	private String decimalPrecisionAttribute;
	

	
	
	private ColumnAttributes(String typeAttribute, String sizeAttribute,
			String commentAttribute, String nullableAttribute,
			String formatAttribute, String decimalDigitsAttribute,
			String decimalScaleAttribute, String decimalPrecisionAttribute) {
		super();
		this.typeAttribute = typeAttribute;
		this.sizeAttribute = sizeAttribute;
		this.commentAttribute = commentAttribute;
		this.nullableAttribute = nullableAttribute;
		this.formatAttribute = formatAttribute;
		this.decimalDigitsAttribute = decimalDigitsAttribute;
		this.decimalScaleAttribute = decimalScaleAttribute;
		this.decimalPrecisionAttribute = decimalPrecisionAttribute;
	}
	public static ColumnAttributes getInstance(String typeAttribute, String sizeAttribute,
			String commentAttribute, String nullableAttribute,
			String formatAttribute, String decimalDigitsAttribute,
			String decimalScaleAttribute, String decimalPrecisionAttribute) {
		return new ColumnAttributes(typeAttribute,sizeAttribute,commentAttribute,nullableAttribute,formatAttribute,decimalDigitsAttribute,decimalScaleAttribute, decimalPrecisionAttribute);
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
	public String getDecimalScaleAttribute() {
		return decimalScaleAttribute;
	}
	public void setDecimalScaleAttribute(String decimalScaleAttribute) {
		this.decimalScaleAttribute = decimalScaleAttribute;
	}
	public String getDecimalPrecisionAttribute() {
		return decimalPrecisionAttribute;
	}
	public void setDecimalPrecisionAttribute(String decimalPrecisionAttribute) {
		this.decimalPrecisionAttribute = decimalPrecisionAttribute;
	}
	
}
