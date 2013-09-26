package rw.asfki.filters;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class AsfkiCharFilter extends FilterReader {
	private final static char BRACERS_OPEN = '«';
	private final static char BRACERS_END ='»';
	private final static char UGLY_BRACERS_OPEN = '„';
	private final static char UGLY_BRACERS_END = '“';
	private final static char BRACERS_ANSI ='"';
	private final static char BELARUSSIAN_I='²';
	private final static char ANSI_I='I';
	private final static char UNKNOWN_406_I = 406;
	private final static char UGLY_BRACER = '”';
//	private final static char[] AMPERSANT= {'&','a','m','p',';'};
//	private final static char[] GREATER_THAN= {'&','g','t',';'};
//	private final static char[] LOWER_THAN= {'&','l','t',';'};
	
	private final static Map<Character,Character> replaceMap = new HashMap<Character,Character>();
	{
		replaceMap.put(BRACERS_OPEN, BRACERS_ANSI);
		replaceMap.put(BRACERS_END, BRACERS_ANSI);
		replaceMap.put(UGLY_BRACERS_OPEN, BRACERS_ANSI);
		replaceMap.put(UGLY_BRACERS_END, BRACERS_ANSI);
		replaceMap.put(UGLY_BRACER, BRACERS_ANSI);
		replaceMap.put(BELARUSSIAN_I, ANSI_I);
		replaceMap.put(UNKNOWN_406_I, ANSI_I);
		
	}
	
	
	
	Logger logger = Logger.getLogger(AsfkiCharFilter.class);

//	private int flag = 0;
	
	public AsfkiCharFilter(Reader in) {
		super(in);
	
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		
		int readLength = super.read(cbuf, off, len);
		
		int total=off+len;
		for(int i=off;i<total;++i){
			
			char currentChar=cbuf[i];

			for (char c : replaceMap.keySet()) {
				if (currentChar == c) {
					char replacingChar = replaceMap.get(c);
					logger.debug(String.format("%c detected. Replaced on %c", c, replacingChar ));
					cbuf[i] = replacingChar;
				}
			}
//			if(currentChar == BRACERS_OPEN || currentChar == BRACERS_END || 
//					currentChar == UGLY_BRACERS_OPEN || currentChar == UGLY_BRACERS_END
//					 || currentChar == UGLY_BRACER) {
//				logger.debug(String.format("%c detected. Replaced on %c", cbuf[i], BRACERS_ANSI));
//				cbuf[i] = BRACERS_ANSI;
//			
//			}
//			if(currentChar == BELARUSSIAN_I) {
//				cbuf[i] = ANSI_I;
//				logger.debug(String.format("Changing %c on %c", BELARUSSIAN_I, ANSI_I));
//			}
//			
//			if(currentChar == UNKNOWN_406_I) {
//				cbuf[i] = ANSI_I;
//				logger.debug(String.format("Changing %c on %c", UNKNOWN_406_I, ANSI_I));
//			}
//			
//			
//			if((flag <= 0 && currentChar == '>') ||	(flag >= 1 && currentChar == '<')){
//				logger.debug("fixup1<>:" + cbuf[i] + "@" +i);
//				cbuf[i]='_';
//				logger.debug(" fixed1<>:" + cbuf[i]);
////				System.err.write(arg0,(i > 10) ? i-10 :i,(i < total-20) ? 30: total-i);
//			}
//			if(currentChar == '<'){ 
//				if(i < total-1){
//					char nextChar=cbuf[i+1];
//					if (nextChar<'A' && nextChar!='/' && nextChar!='?') {
//						logger.debug("fixup1.1<>:"+currentChar+"@"+i);
//						cbuf[i]='_';
//						logger.debug(" fixed1.1<>:"+cbuf[i]);
//					} else 	flag++;
//				} else 	flag++;
//			}
//			if(currentChar == '>') 
//				flag--;
			
			
			
			
		}
		
		return readLength;
	}
	
	
}
