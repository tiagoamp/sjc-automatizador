package com.tiagoamp.sjc.model.input.v3;

import java.io.IOException;

public interface IInputConverter {

	public ConvertedSpreadsheet convert() throws IOException;
	
}
