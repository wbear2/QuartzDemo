package cn.edu.sjtu.se.dclab.quartz_demo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

/**
 * Describes the functions of this file
 *
 * @author wbear
 * @date 2015/12/24 16:21
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SimpleJob implements Job {
    private final Logger log = LoggerFactory.getLogger(SimpleJob.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey key = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("key = " + key + ", triggerKey = " + jobExecutionContext.getTrigger().getKey());
        for (String k : dataMap.getKeys()) {
            System.out.println(k + dataMap.get(k));
        }
        try {
            dataMap.put("result", jobExecutionContext.getScheduler().getSchedulerInstanceId());
            System.out.println("instanceId = " + jobExecutionContext.getScheduler().getSchedulerInstanceId()
                    + ", time = " + sdf.format(new Date()) + " ,SimpleJob is executing!");
            System.out.println("=================");
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();;
        }
    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        System.out.println(scheduler.getSchedulerName());
        scheduler.start();
        JobDetail job = JobBuilder.newJob(SimpleJob.class).withIdentity("myJob", "group1")
//                .usingJobData("a", "aa")
//                .usingJobData("b", "b1")
                .build();
        JobDetail job2 = JobBuilder.newJob(SimpleJob.class).withIdentity("myJob2", "group1").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .build();
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("myTrigger2", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/15 * * * * ?"))
                .build();
        Trigger trigger3 = TriggerBuilder.newTrigger().withIdentity("myTrigger3", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * * * ?"))
                .build();
        HashSet<Trigger> set = new HashSet<Trigger>();
        set.add(trigger);
        set.add(trigger2);
        set.add(trigger3);
        scheduler.scheduleJob(job, set, true);
        //scheduler.scheduleJob(job2, trigger2);

        //scheduler.shutdown();
    }
}
