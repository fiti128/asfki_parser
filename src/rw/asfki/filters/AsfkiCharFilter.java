package rw.asfki.filters;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

public class AsfkiCharFilter extends FilterReader {
	private final static char BRACERS_OPEN = '«';
	private final static char BRACERS_END ='»';
	private final static char BRACERS_ANSI = '"';
	
	
	Logger logger = Logger.getLogger(AsfkiCharFilter.class);

	public AsfkiCharFilter(Reader in) {
		super(in);
	
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		
		int readLength = super.read(cbuf, off, len);
		
		int total=off+len;
		for(int i=off;i<total;++i){
			
			char currentChar=cbuf[i];

			if(currentChar == BRACERS_OPEN || currentChar == BRACERS_END) {
				cbuf[i] = BRACERS_ANSI;
				logger.debug(String.format("%c or %c detected. Replaced on %c", BRACERS_OPEN, BRACERS_END,BRACERS_ANSI));
			
			}
			
		}
		
		return readLength;
	}
	
	
}
