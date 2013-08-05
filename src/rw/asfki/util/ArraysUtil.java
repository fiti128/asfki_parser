package rw.asfki.util;

public class ArraysUtil {

	public static byte[] insertArrayIntoArray(byte[] sourceArray, byte[] destinationArray, int startPoint) {

		int length = sourceArray.length + destinationArray.length;
		byte[] newArray = new byte[length];
		System.arraycopy(destinationArray, 0, newArray, 0, startPoint);
		System.arraycopy(sourceArray, 0, newArray, startPoint, sourceArray.length);
		System.arraycopy(destinationArray, startPoint, newArray, startPoint + sourceArray.length, destinationArray.length - startPoint);
		return newArray;
	}
	
	public static char[] insertArrayIntoArray(char[] sourceArray, char[] destinationArray, int startPoint) {

		int length = sourceArray.length + destinationArray.length;
		char[] newArray = new char[length];
		System.arraycopy(destinationArray, 0, newArray, 0, startPoint);
		System.arraycopy(sourceArray, 0, newArray, startPoint, sourceArray.length);
		System.arraycopy(destinationArray, startPoint, newArray, startPoint + sourceArray.length, destinationArray.length - startPoint);
		return newArray;
	}
	
	public static char[] replaceCharWithArrayInCharArray(char[] sourceArray, char[] destinationArray, int charIndex) {
		int length = sourceArray.length + destinationArray.length -1 ;
		char[] newArray = new char[length];
		System.arraycopy(destinationArray, 0, newArray, 0, charIndex);
		System.arraycopy(sourceArray, 0, newArray, charIndex, sourceArray.length);
		System.arraycopy(destinationArray, charIndex+1, newArray, charIndex + sourceArray.length, destinationArray.length - charIndex-1);
		return newArray;
	}
}
