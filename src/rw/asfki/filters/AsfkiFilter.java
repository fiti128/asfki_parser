package rw.asfki.filters;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class AsfkiFilter extends FilterInputStream {
	Logger logger = Logger.getLogger(AsfkiFilter.class);

	private char delimeter;
	public AsfkiFilter(InputStream arg0, char delimeter) {
		super(arg0);
		this.delimeter = delimeter;
	}

	@Override
	public int read(byte[] arg0, int start, int length) throws IOException {
		int retLen=super.read(arg0, start, length);
		int total=start+retLen;
		for(int i=start;i<total;++i){
//			if (arg0[i] == -62) {
//				if (arg0[i+1] == -69 || arg0[i+1] == -85) {
//					logger.debug("« or » detected. Replaced on \"");
//					arg0[i] = 34;
//					i++;
//					continue;
//				}
//			}
			
			
			char currentChar=(char)arg0[i];

			if(currentChar == delimeter) {
				logger.debug(delimeter + " detected");
				
				arg0[i]=(byte) ((delimeter == '#') ? '|': delimeter);
			}
			
			
			if(currentChar >= 0 && currentChar < ' '){
				logger.debug("fixup1:" + currentChar); 
				arg0[i]=' ';
			}
			
		}
		return retLen;
	}

}
