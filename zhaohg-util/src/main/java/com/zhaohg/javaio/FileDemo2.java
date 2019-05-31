package com.zhaohg.javaio;

import java.io.File;
import java.io.FileFilter;

public class FileDemo2 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        File file = new File("e:\\example");
        /*String[] filenames = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				System.out.println(dir+"\\"+name);
				return name.endsWith("java");
			}
		});
		for (String string : filenames) {
			System.out.println(string);
		}*/
		/*File[] files = file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				System.out.println(dir+"\\"+name);
			
				return false;
			}
		});*/
        File[] files = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                System.out.println(pathname);

                return false;
            }
        });
    }

}
