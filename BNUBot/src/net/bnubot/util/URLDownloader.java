/**
 * This file is distributed under the GPL 
 * $Id$
 */

package net.bnubot.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @author sanderson
 *
 */
public class URLDownloader {
	public static void downloadURL(URL url, File to, SHA1Sum sha1, boolean force) throws Exception {
		// Don't download the file if it already exists
		if(to.exists() && (force == false)) {
			// If no MD5 sum was given
			if(sha1 == null)
				return;
			
			// If the MD5 sums match
			SHA1Sum fSHA1 = new SHA1Sum(to);
			if(fSHA1.equals(sha1))
				return;
			
			Out.info(URLDownloader.class, "SHA1 mismatch for " + to.getName() + "\nExpected: " + sha1 + "\nCalculated: " + fSHA1);
		}
		
		// Make sure the path to the file exists
		{
			String sep = System.getProperty("file.separator");
			String folders = to.getPath();
			String path = "";
			for(int i = 0; i < folders.length(); i++) {
				path += folders.charAt(i);
				if(path.endsWith(sep)) {
					File f = new File(path);
					if(!f.exists())
						f.mkdir();
					if(!f.isDirectory()) {
						Out.error(URLDownloader.class, path + " is not a directory!");
						return;
					}
				}
			}
		}
		
		Out.info(URLDownloader.class, "Downloading " + url.toExternalForm());
		
		DataInputStream is = new DataInputStream(new BufferedInputStream(url.openStream()));
		FileOutputStream os = new FileOutputStream(to);
		byte[] b = new byte[0x1000];
		do {
			int c = is.read(b);
			if(c == -1)
				break;
			os.write(b, 0, c);
		} while(true);
		
		os.close();
		is.close();
	}
}
