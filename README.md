# NikoBootLoader

## Start NikoBot with Libs
Load custom libs(jar) before NikoBot start.  
#### Usage:  
```Put NikoBot.jar(v3.6 or later version) and others dependencies into libs/ folder.```  
```Start NikoBootLoader```  

## Advanced
Put your program and libs into libs/ and edit EntryPoint.yml  
EntryPoint.yml  
```yaml=
#Change ClassPath to your program's class
ClassPath=com.github.smallru8.test.HelloWorld
#Change Method to the entry method
Method=hello

#Optional
#Support: String, Integer, Double, Float, Byte, Long
#Parameters you want to pass to the Method.\n");
Parameter0=(String)someStr
Parameter1=(Integer)32
#.
#.
#.
```

Sample program:  
```java=
package com.github.smallru8.test;

public class HelloWorld{
    
    public void hello(String s,int i){
        System.out.println(s+i);
    }
    
}

```

