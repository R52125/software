import javax.servlet.ServletContext;
import java.util.Calendar;
import java.util.TimerTask;

public class test extends TimerTask
    {
        private static final int C_SCHEDULE_HOUR = 0;
        private static boolean isRunning = false;
        private ServletContext context = null;
        public test(ServletContext context)
        {
            this.context = context;
        }

        public void run()
        {
            Calendar c = Calendar.getInstance();
            if(!isRunning)
            {
                if(C_SCHEDULE_HOUR == c.get(Calendar.HOUR_OF_DAY))
                {
                    isRunning = true;
                    System.out.println("hello");
//-------------------结束
                    isRunning = false;
                    System.out.println("end");
                }
                else
                {
                    System.out.println("no");
                }
            }
        }
}

