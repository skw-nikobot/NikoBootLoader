package org.skunion.smallru8.NikoBot3.Bootloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.github.smallru8.util.Pair;

public class LibLoader {
	
	/** JAR files in ./libs/ */
	public URLClassLoader LIB;
	/** Other JAR files */
	public URLClassLoader Ext_LIB;
	
	private String dir = "libs";
	private String[] libsPath;
	
	public LibLoader() throws IOException {
		fileCheck();
	}
	
	/**
	 * Check ./libs directory is exist.
	 * @throws IOException
	 */
	private void fileCheck() throws IOException {
		File libs = new File(dir);//libs
		if(!libs.exists())
			libs.mkdir();
		libsPath = libs.list();
		Arrays.sort(libsPath);
		if(!(new File("libs/extra-libs.yml").exists())) {//cfg/extra-libs.yml
			FileWriter fw = new FileWriter("libs/extra-libs.yml");
			fw.write("//Put your library path, using enter to split them.\n");
			fw.flush();
			fw.close();
		}
		
		if(!(new File("libs/EntryPoint.yml").exists())) {//cfg/extra-libs.yml
			FileWriter fw = new FileWriter("libs/EntryPoint.yml");
			fw.write("ClassPath=\n");
			fw.write("Method=\n");
			fw.write("#Parameters you want to pass to the Method.\n");
			fw.write("#Support: String, Integer, Double, Float, Byte, Long\n");
			fw.write("#Parameter0=(String)aasc\n");
			fw.write("#Parameter1=(Byte)5\n");
			fw.flush();
			fw.close();
		}
		
	}
	
	public void entryPoint() {
		
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(new File("libs/EntryPoint.yml")));
			String classPath = pro.getProperty("ClassPath");
			String method = pro.getProperty("Method");
			ArrayList<Class<?>> cLs = new ArrayList<Class<?>>();
			ArrayList<Object> oLs = new ArrayList<Object>();
			int i=0;
			String param;
			while((param = pro.getProperty("Parameter"+i,""))!="") {
				Pair<Class<?>,Object> p = new Pair<Class<?>,Object>();
				if(param.startsWith("(String)")) {
					p.makePair(String.class, param.split(")")[1]);
				}
				else if(param.startsWith("(Integer)")) {
					p.makePair(Integer.class, Integer.parseInt(param.split(")")[1]));
				}
				else if(param.startsWith("(Double)")) {
					p.makePair(Double.class, Double.parseDouble(param.split(")")[1]));
				}
				else if(param.startsWith("(Float)")) {
					p.makePair(Float.class, Float.parseFloat(param.split(")")[1]));
				}
				else if(param.startsWith("(Byte)")) {
					p.makePair(Byte.class, Byte.parseByte(param.split(")")[1]));
				}
				else if(param.startsWith("(Long)")) {
					p.makePair(Long.class, Long.parseLong(param.split(")")[1]));
				}
				cLs.add(p.first);
				oLs.add(p.second);
				i++;
			}
			
			Class<?> mainClass = Ext_LIB.loadClass(classPath);
			Object obj = mainClass.getDeclaredConstructor().newInstance();
			
			if(i>0) {
				Class<?>[] cArr = new Class<?>[i];
				for(int j=0;j<i;j++) {
					cArr[j] = cLs.get(j);
				}
				mainClass.getMethod(method,cArr).invoke(obj,oLs.toArray());
			}else {
				mainClass.getMethod(method).invoke(obj);
			}
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Load JAR files in ./libs/ to URLClassLoader LIB 
	 */
	public void loadLibs() {
		ArrayList<URL> urlTmp = new ArrayList<URL>();
		for(int i=0;i<libsPath.length;i++) {
			if(libsPath[i].endsWith(".jar")) {//only load jar file
				System.out.println("[LOAD][LIB]:Load "+libsPath[i]+".");
				try {
					urlTmp.add(new URI("file:libs/"+libsPath[i]).toURL());
				} catch (Exception e) {
					System.out.println("[WARN][LIB]:Failed. When loading " + libsPath[i]+".");
					e.printStackTrace();
				}
			}
		}
		URL[] urls = new URL[urlTmp.size()];
		for(int i=0;i<urlTmp.size();i++)
			urls[i] = urlTmp.get(i);
		urlTmp.clear();
		LIB = new URLClassLoader(urls,Ext_LIB);	
	}
	
	/**
	 * Load other JAR files to URLClassLoader Ext_LIB 
	 */
	public void loadExtLibs() throws IOException{
		FileReader fr = new FileReader("libs/extra-libs.yml");
		BufferedReader br = new BufferedReader(fr);
		String tmp = null;
		ArrayList<URL> urlTmp = new ArrayList<URL>();
		while((tmp = br.readLine())!=null) {
			if((!tmp.startsWith("//"))&&tmp.endsWith(".jar")) {
				try {
					System.out.println("[LOAD][LIB]:Load "+tmp+".");
					urlTmp.add(new URI("file:"+tmp).toURL());
				} catch (Exception exp) {
					System.out.println("[WARN][LIB]:Failed. When loading " + tmp +".");
					exp.printStackTrace();
				}
			}
		}
		br.close();
		fr.close();
		URL[] urls = new URL[urlTmp.size()];
		for(int i=0;i<urlTmp.size();i++)
			urls[i] = urlTmp.get(i);
		urlTmp.clear();
		Ext_LIB = new URLClassLoader(urls,ClassLoader.getSystemClassLoader());
	}
}