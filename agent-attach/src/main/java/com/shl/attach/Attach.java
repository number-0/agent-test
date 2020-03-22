package com.shl.attach;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author songhengliang
 * @date 2020/3/22
 */
//@SpringBootApplication
public class Attach {

//  public static void main(String[] args) {
//    SpringApplication.run(Attach.class, args);
//  }

  public static void main(String[] args) {
    //查找所有jvm进程，排除attach测试工程
    List<VirtualMachineDescriptor> attach = VirtualMachine.list().stream()
        .filter(jvm -> {
        })
        .collect(Collectors.toList());
    for (int i=0; i<attach.size(); i++) {
      System.out.println("[" + i + "]" + attach.get(i).displayName() + ":" + attach.get(i).id());
    }
    System.out.println("请输入需要attach的pid编号");
    Scanner scanner = new Scanner(System.in);
    String s = scanner.nextLine();
    VirtualMachineDescriptor virtualMachineDescriptor = attach.get(Integer.valueOf(s));
    try {
      VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
      //执行的是*agent.jar中的agentmain方法
      virtualMachine.loadAgent("/.../agent-case/target/java-agent.jar", "param");
      virtualMachine.detach();
    } catch (AttachNotSupportedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (AgentLoadException e) {
      e.printStackTrace();
    } catch (AgentInitializationException e) {
      e.printStackTrace();
    }
  }
}
