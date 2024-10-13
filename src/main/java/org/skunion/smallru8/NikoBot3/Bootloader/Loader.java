package org.skunion.smallru8.NikoBot3.Bootloader;

import java.io.IOException;

public class Loader {

	public static void main(String[] args) throws IOException
    {
		LibLoader LL = new LibLoader();
		LL.loadExtLibs();
		LL.loadLibs();
		LL.entryPoint();
    }
	
}
