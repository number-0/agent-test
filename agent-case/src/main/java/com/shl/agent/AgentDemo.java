package com.shl.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

/**
 * @author songhengliang
 * @date 2020/3/22
 */
public class AgentDemo {

  /**
   * premain：静态的织入
   * vm options : -javaagent:/.../agent-case/target/java-agent.jar
   * @param agentOps
   * @param inst
   */
  public static void premain(String[] agentOps, Instrumentation inst) {
    System.out.println("premain execute...");
    simpleDemo(agentOps, inst);
  }

  /**
   * 动态的织入：可以动态的织入到一个运行中的jvm中
   * @param agentOps
   * @param inst
   */
  public static void agentmain(String[] agentOps, Instrumentation inst) {
    System.out.println("agentmain execute...");
    simpleDemo(agentOps, inst);
    //transform是会对尚未加载的类进行增强代理层，这里是已经运行的jvm，所以类已经被加载了
    //必须主动调用retransformClasses让jvm再对运行中的类进行加上代理层
    for(Class loadedClass : inst.getAllLoadedClasses()) {
      if (loadedClass.getName().contains("com.shl.agent.Test")) {
        try {
          //将类转换掉
          inst.retransformClasses(loadedClass);
        } catch (UnmodifiableClassException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void simpleDemo(String[] agentOps, Instrumentation inst) {
    inst.addTransformer(new ClassFileTransformer() {
      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
          ProtectionDomain protectionDomain, byte[] classfileBuffer)
          throws IllegalClassFormatException {
        //判断是指定的类
        if ("com/shl/agent/Test".equals(className)) {

          try {
            //获取更改后的class字节数组
            //Test.class这个Test文件是修改后的新的class
            classfileBuffer = Files.readAllBytes(Paths.get("/Users/workoffice/java/workspace-shl/agent-test/agent-case/src/main/resources/Test.class"));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return classfileBuffer;
      }
    }, true);

  }

}
