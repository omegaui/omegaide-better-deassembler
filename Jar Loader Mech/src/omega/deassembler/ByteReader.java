package omega.deassembler;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.LinkedList;
public class ByteReader {
	private Class c;
	public String className;
     public String type = "class";
     public String packagePath;
	public LinkedList<DataMember> dataMembers = new LinkedList<>();
	public ByteReader(Class c){
		this.c = c;
		this.className = c.getName();
          if(className.contains(" ")){
               this.type = className.substring(0, className.indexOf(' '));
               this.className = className.substring(className.indexOf(' ') + 1);
          }
		loadFields();
		loadMethods();
	}
	public void loadFields(){
		for(Field f : c.getDeclaredFields()){
               String access = "";
               String modifier = "";
               String name = "";
               String type = "";
               String member = f.toString();
               if(!member.startsWith("public"))
                    continue;
               access = member.substring(0, member.indexOf(' '));
               member = member.substring(member.indexOf(' ') + 1);
               name = member.substring(member.lastIndexOf(' ') + 1);
               if(name.contains("."))
                    name = name.substring(name.lastIndexOf('.') + 1);
               member = member.substring(0, member.lastIndexOf(' '));
               type = member.substring(member.lastIndexOf(' ') + 1);
               if(member.contains(" ")){
                    member = member.substring(0, member.lastIndexOf(' '));
                    modifier = member;
               }
               dataMembers.add(new DataMember(access, modifier, type, name, null));
          }
	}
	public void loadMethods(){
		for(Method m : c.getDeclaredMethods()){
			String access = "";
               String modifier = "";
               String name = "";
               String type = "";
               String parameters = "";
               String member = m.toString();
               if(!member.startsWith("public"))
                    continue;
               int index = member.indexOf('(');
               if(member.charAt(index + 1) != ')')
                    parameters = member.substring(member.indexOf('(') + 1, member.indexOf(')'));
               member = member.substring(0, member.indexOf('('));
               access = member.substring(0, member.indexOf(' '));
               member = member.substring(member.indexOf(' ') + 1);
               name = member.substring(member.lastIndexOf(' ') + 1);
               if(name.contains("."))
                    name = name.substring(name.lastIndexOf('.') + 1);
               member = member.substring(0, member.lastIndexOf(' '));
               type = member.substring(member.lastIndexOf(' ') + 1);
               if(member.contains(" ")){
                    member = member.substring(0, member.lastIndexOf(' '));
                    modifier = member;
               }
               dataMembers.add(new DataMember(access, modifier, type, name, parameters));
		}
	}
	public int count(String line, char ch){
		int c = 0;
		for(char chx : line.toCharArray()){
			if(ch == chx)
				c++;
		}
		return c;
	}
	public void close(){
		dataMembers.clear();
	}
}
