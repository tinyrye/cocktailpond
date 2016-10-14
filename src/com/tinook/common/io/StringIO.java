package com.tinook.common.io;

import java.io.*;

public class StringIO
{
	public static String toString(final InputStream stream) throws IOException
	{
		final InputStreamReader streamReader = new InputStreamReader(stream);

		try
		{
			final StringBuffer accumBuf = new StringBuffer();
			final char[] perReadBuf = new char[1024];
			perReadBuf[1023] = '\0';
			while (streamReader.read(perReadBuf, 0, 1023) != -1) {
				accumBuf.append(perReadBuf);
			}
			return accumBuf.toString();
		}
		finally {
			streamReader.close();
		}
	}
}