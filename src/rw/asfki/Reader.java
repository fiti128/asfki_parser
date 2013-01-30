package rw.asfki;

import java.util.List;

import rw.asfki.domain.ASFKI_RowColumn;

public interface Reader {
	
	public boolean hasNext();
	
	public List<ASFKI_RowColumn> next();
	
	public void close();
	

}
