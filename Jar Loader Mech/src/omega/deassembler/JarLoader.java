package omega.deassembler;
import java.util.jar.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
public class JarLoader {
	public String jarPath;
	public LinkedList<ByteReader> readers = new LinkedList<>();
	public LinkedList<String> classNames = new LinkedList<>();
	public URLClassLoader loader;
	public JarLoader(String jarPath){
		this.jarPath = jarPath;
		load();
	}
	private void load(){
		try{
			File file = new File(jarPath);
			if(!file.exists())
				return;
			loadClassNames();
			loader = URLClassLoader.newInstance(new URL[]{
				file.toURL()
			});
			classNames.forEach(name->{
				try{
					readers.add(new ByteReader(loader.loadClass(name)));
				}
				catch(Exception e){
					System.err.println(e);
				}
			});
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	private void loadClassNames(){
		readJar();
	}
     
	public void readJar(){
		try{
			try(JarFile rtJarFile = new JarFile(jarPath)){
				for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();){
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/")) {
						String classPath = convertJarPathToPackagePath(name);
						if(classPath != null) {
							classNames.add(classPath);
						}
					}
				}
			}
		}
		catch(Exception e) {
			System.err.println(e);
		}
	}
	public static String convertJarPathToPackagePath(String zipPath){
		if(zipPath == null || zipPath.contains("$") || !zipPath.endsWith(".class") || zipPath.startsWith("META-INF"))
			return null;
		zipPath = zipPath.substring(0, zipPath.lastIndexOf('.'));
		StringTokenizer tok = new StringTokenizer(zipPath, "/");
		zipPath = "";
		while(tok.hasMoreTokens())
			zipPath += tok.nextToken() + ".";
		zipPath = zipPath.substring(0, zipPath.length() - 1);
		return zipPath.equals("module-info") ? null : zipPath;
	}
	public ByteReader getReader(String className){
		for(ByteReader br : readers){
			if(br.className.equals(className))
				return br;
		}
		return null;
	}
	public Class loadClass(String className){
		try{
			return loader.loadClass(className);
		}
		catch(Exception e){
			System.err.println(e);
		}
		return null;
	}
	public void close(){
		try{
			loader.close();
			readers.forEach(r->r.close());
			readers.clear();
			classNames.clear();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	public static void main(String[] args){
		JarLoader loader = new JarLoader("/home/ubuntu/Documents/Omega IDE/lib/openjfx-11.0.2_linux-x64_bin-sdk/javafx-sdk-11.0.2/lib/javafx.base.jar");
		System.out.println(loader.getReader("javafx.util.Builder"));
          loader.getReader("javafx.util.Builder").dataMembers.forEach(System.out::println);
	}
}
