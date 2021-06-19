import javax.servlet.ServletContextEvent;
import java.util.Timer;

public class SysContextListener {
    private static Timer timer = null;
    public static void contextInitialized(ServletContextEvent event) {//在这里初始化监听器，在tomcat启动的时候监听器启动，可以在这里实现定时器功能
        timer = new Timer(true);
        System.out.println("start");
        timer.schedule(new test(event.getServletContext()),0,5*1000);//调用exportHistoryBean，0表示任务无延迟，5*1000表示每隔5秒执行任务，60*60*1000表示一个小时。
        System.out.println("add");
    }
    public void contextDestroyed(ServletContextEvent event)
    {//在这里关闭监听器，所以在这里销毁定时器。
        timer.cancel();
        event.getServletContext().log("定时器销毁");
    }
}
