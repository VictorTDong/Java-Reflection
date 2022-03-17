/*
Purpose: The purpose of this assignment to inspect classes using reflection 
Details: Test are run from Driver.java provided by the professor and the classes are inspected using reflection api methods
Limitations and assumption: Can only work on classes within the directory 
Known bugs: None
*/

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class<?> c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class<?> c, Object obj, boolean recursive, int depth) {
		// note: depth will be needed to capture the output indentation level
    	String indent = "";
    	for(int i = 0; i < depth; i++)
    	{
    		indent += "        ";
    	}
    	
    	System.out.println(indent + "CLASS");
    	System.out.println(indent + "Class: " + c.getName());
    	//check for array here
    	if(obj.getClass().isArray())
    	{	
    		objectIsArray(c, obj, recursive, depth, indent);
    	}
    	else
    	{
	    	inspectSuperclass(c, obj, recursive, depth, indent);
			inspectInterface(c, obj, recursive, depth, indent);
			inspectConstructor(c, indent);
			inspectMethod(c, indent);
			inspectField(c, obj, recursive, depth, indent);
	    }
    }

	private void inspectField(Class<?> c, Object obj, boolean recursive, int depth, String indent) {
		Field[] fields = c.getDeclaredFields();
		System.out.println(indent + "FIELDS( " + c.getName()+ " )");
		if(fields.length > 0)
		{
			System.out.println(indent + "Fields -> ");
			for( int i = 0 ; i < fields.length ; i++ ) 
			{
				fields[i].setAccessible(true);
				System.out.println(indent + " FIELD");
				System.out.printf(indent + "  Name: " + fields[i].getName() + "\n");
				Class<?> types = fields[i].getType();
		        System.out.println(indent + "  Type: " + types.getName());
		        int modifier = fields[i].getModifiers();
		        System.out.println(indent + "  Modifiers: " + Modifier.toString(modifier));
		        try 
		        {
					Object value = fields[i].get(obj);
					
					if(value != null && value.getClass().isArray())
			    	{	
			    		fieldIsArray(recursive, depth, indent, value);
			    	}
					else
					{
						fieldIsNotArray(recursive, depth, indent, types, value);
					}
				} 
		        catch (IllegalArgumentException | IllegalAccessException e) 
		        {
					e.printStackTrace();
				}          
			}
		}
		else
		{
			System.out.println(indent + "Fields --> NONE ");
		}
	}

	private void fieldIsNotArray(boolean recursive, int depth, String indent, Class<?> types, Object value) {
		if(!recursive && !types.isPrimitive())
		{
			if(System.identityHashCode(value) == 0)
			{
				System.out.println(indent + "  Value: null ");
			}
			else
			{
				System.out.println(indent + "  Value (ref): " + types.getName() + "@" + Integer.toHexString(System.identityHashCode(value)));
			}
		}
		else if(recursive && !types.isPrimitive() && value != null)
		{
			inspectClass(types, value, recursive, depth + 1);
		}
		else
		{
			System.out.println(indent + "  Value: " + value);
		}
	}

	private void fieldIsArray(boolean recursive, int depth, String indent, Object value) {
		int length = Array.getLength(value);
		Object[] array = new Object[length];
  	
		System.out.println(indent + " Type Name: " + value.getClass().getTypeName());
		System.out.println(indent + " Component Type: " + value.getClass().getComponentType());
		System.out.println(indent + " Length: " + length);
		System.out.println(indent + " Entries -> ");
  	
		for(int index=0;index<array.length;index++)
		{
		    Object currElement = Array.get(value, index);
		    if(currElement != null && recursive && !value.getClass().getComponentType().isPrimitive())
		    {
		    	inspectClass(currElement.getClass(), currElement, recursive, depth+1);
		    }
		    else 
		    {
		    	if(value == null) 
		    	{
		    		System.out.println(indent + " Value: null");
		    	}
		    	else
		    	{
		    		System.out.println(indent + " Value: " + currElement);
		    	}
		    }     
		}
	}

	private void inspectMethod(Class<?> c, String indent) {
		Method[] methods = c.getDeclaredMethods();
		System.out.println(indent + "METHODS( " + c.getName()+ " )");
		if(methods.length < 1)
		{
			System.out.println(indent + "Methods -> NONE");
		}
		else 
		{
			System.out.println(indent + "Methods -> ");
			for( int i = 0 ; i < methods.length ; i++ ) 
			{
				System.out.println(indent + " METHOD");
				System.out.printf(indent + "  Name: " + methods[i].getName() + "\n");
				Class<?>[] exceptions = methods[i].getExceptionTypes();
		        System.out.println(indent + "  Exceptions: ");
		        for (Class <?>exception : exceptions) 
		        {
		            System.out.println(indent + "  " + exception.getName());
		        }
		        Class<?>[] parameters = methods[i].getParameterTypes();
		        System.out.println(indent + "  Parameters: ");
		        for(Class<?> parameter : parameters)
		        {
		        	System.out.println(indent + "  " + parameter.getName());
		        }
		        System.out.println(indent + "  Return Type: " + methods[i].getReturnType());
		        int modifier = methods[i].getModifiers();
		        System.out.println(indent + "  Modifiers: " + Modifier.toString(modifier));
			}
		}
	}

	private void inspectConstructor(Class<?> c, String indent) {
		Constructor<?>[] constructors = c.getDeclaredConstructors();
		System.out.println(indent + "CONSTRUCTORS( " + c.getName()+ " )"); 
		if(constructors.length > 0)
		{
			System.out.println(indent + "Constructors-> ");
			for( int i = 0 ; i < constructors.length ; i++ ) 
			{
				System.out.println(indent + " CONSTRUCTOR");
		        System.out.println(indent + "  Name: " + constructors[i].getName());
		        int modifier = constructors[i].getModifiers();
		        System.out.println(indent + "  Modifiers: " + Modifier.toString(modifier));
		        System.out.println(indent + "  Parameters: ");
		        Class<?>[] parameters = constructors[i].getParameterTypes();
		        for(Class<?> parameter : parameters)
		        {
		        	System.out.println(indent + "  " + parameter.getName());
		        }
		        Class<?>[] exceptions = constructors[i].getExceptionTypes();
		        System.out.println(indent + "  Exceptions: ");
		        for (Class<?> exception : exceptions) 
		        {
		            System.out.println(indent + "  " + exception.getName());
		        }
		    }
		}
		else
		{
			System.out.println(indent + "Constructors -> NONE");
		}
	}

	private void inspectInterface(Class<?> c, Object obj, boolean recursive, int depth, String indent) {
		Class<?>[] interfaces = c.getInterfaces();
		System.out.println(indent + "INTERFACES( " + c.getName()+ " )"); 
		if(interfaces.length < 1)
		{
			System.out.println(indent + "INTERFACES -> NONE "); 
		}
		else
		{
			System.out.println(indent + "INTERFACES -> ");
			for( int i = 0 ; i < interfaces.length ; i++ ) 
			{
				System.out.println(indent + " INTERFACES -> Recursively Inspect "); 
		        System.out.println(indent + " " + interfaces[i].getName());
		        inspectClass(interfaces[i], obj, recursive, depth + 1);
		    }
		}
	}

	private void inspectSuperclass(Class<?> c, Object obj, boolean recursive, int depth, String indent) {
		Class<?> classSuper = c.getSuperclass();
		if(c == Object.class || classSuper == null)
		{
			System.out.println(indent + "superClass: NONE");
		}
		else
		{
			System.out.println(indent + "SUPERCLASS -> Recursively Inspect");
			System.out.println(indent + "superClass: " + classSuper.getName()); 
			inspectClass(classSuper, obj, recursive, depth + 1);
		}
	}

	private void objectIsArray(Class<?> c, Object obj, boolean recursive, int depth, String indent) {
		int length = Array.getLength(obj);
		Object[] array = new Object[length];
  	
		System.out.println(indent + " Type Name: " + c.getTypeName());
		System.out.println(indent + " Component Type: " + c.getComponentType());
		System.out.println(indent + " Length: " + length);
		System.out.println(indent + " Entries -> ");
		
		
		for(int index=0;index<array.length;index++)
		{
		    Object value = Array.get(obj, index);
		    if(value != null && recursive)
		    {
		    	inspectClass(value.getClass(), value, recursive, depth+1);
		    }
		    else 
		    {
		    	if(value == null) 
		    	{
		    		System.out.println(indent + " Value: null");
		    	}
		    	else
		    	{
		    		System.out.println(indent + " Value: " + value + "@" + Integer.toHexString(System.identityHashCode(value)));
		    	}
		    }     
		}
	}
}
