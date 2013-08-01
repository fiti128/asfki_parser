package rw.asfki.filters;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class AsfkiFilter extends FilterInputStream {
	private final static char BRACERS_OPEN = '�';
	private final static char BRACERS_END ='�';
	Logger logger = Logger.getLogger(AsfkiFilter.class);
	private int flag=0;
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
			char currentChar=(char)arg0[i];

			if(currentChar == delimeter) {
				logger.debug(delimeter + " detected");
				
				arg0[i]=(byte) ((delimeter == '#') ? '|': delimeter);
			}
			
			if (currentChar == BRACERS_OPEN) {
				logger.debug(BRACERS_OPEN + " detected");
				arg0[i] = (byte) '"';
			}
			if (currentChar == BRACERS_END) {
				logger.debug(BRACERS_END + " detected");
				arg0[i] = (byte) '"';
			}
			
			if(currentChar >= 0 && currentChar < ' '){
				logger.debug("fixup1:" + currentChar); 
				arg0[i]=' ';
			}
			if((flag <= 0 && currentChar == '>') ||	(flag >= 1 && currentChar == '<')){
				logger.debug("fixup1<>:" + arg0[i] + "@" +i);
				arg0[i]='_';
				logger.debug(" fixed1<>:" + arg0[i]);
//				System.err.write(arg0,(i > 10) ? i-10 :i,(i < total-20) ? 30: total-i);
			}
			if(currentChar == '<'){ 
				if(i < total-1){
					char nextChar=(char)arg0[i+1];
					if (nextChar<'A' && nextChar!='/' && nextChar!='?') {
						logger.debug("fixup1.1<>:"+currentChar+"@"+i);
						arg0[i]='_';
						logger.debug(" fixed1.1<>:"+arg0[i]);
					} else 	flag++;
				} else 	flag++;
			}
			if(currentChar == '>') 
				flag--;
		}
		return retLen;
	}

}
