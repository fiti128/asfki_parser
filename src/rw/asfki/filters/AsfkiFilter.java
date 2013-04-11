package rw.asfki.filters;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AsfkiFilter extends FilterInputStream {
	private int flag=0;
	public AsfkiFilter(InputStream arg0) {
		super(arg0);
	}

	@Override
	public int read(byte[] arg0, int start, int length) throws IOException {
		int retLen=super.read(arg0, start, length);
		int total=start+retLen;
		for(int i=start;i<total;++i){
			char currentChar=(char)arg0[i];
			if(currentChar>=0 && currentChar<' '){
				System.err.println("fixup1:"+currentChar); 
				arg0[i]=' ';
			}
			if((flag<=0 && currentChar=='>') ||	(flag>=1 && currentChar=='<')){
				System.err.print("fixup1<>:"+arg0[i]+"@"+i);
				arg0[i]='_';
				System.err.println(" fixed1<>:"+arg0[i]);
				System.err.write(arg0,(i>10)?i-10:i,(i<total-20)?30:total-i);
			}
			if(currentChar=='<'){ 
				if(i<total-1){
					char nextChar=(char)arg0[i+1];
					if (nextChar<'A' && nextChar!='/' && nextChar!='?') {
						System.err.print("fixup1.1<>:"+currentChar+"@"+i);
						arg0[i]='_';
						System.out.print("total - start = " +(total-start) + ". Nextchar = " + nextChar + ". retLen = " + retLen);
						System.err.println(" fixed1.1<>:"+arg0[i]);
					} else 	flag++;
				} else 	flag++;
			}
			if(currentChar=='>') 
				flag--;
		}
		return retLen;
	}

}
