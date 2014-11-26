package server.eventsrouter.events;

import server.StarNub;

public class ThreadEvent extends StarNubEvent<String> {


    private final String THREAD_OWNER;


    private final Object TASK_OBJECT;

    public ThreadEvent(String EVENT_KEY, String THREAD_OWNER, Object TASK_OBJECT) {
        super(EVENT_KEY);
        this.THREAD_OWNER = THREAD_OWNER;
        this.TASK_OBJECT = TASK_OBJECT;
    }

    public String getTHREAD_OWNER() {
        return THREAD_OWNER;
    }

    public Object getTASK_OBJECT() {
        return TASK_OBJECT;
    }

    public static void eventSend_Permanent_Thread_Started(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Permanently_Started", threadOwner, taskObject));
    }

    public static void eventSend_Thread_Task_Scheduled_One_Time(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Task_Scheduled_One_Time", threadOwner, taskObject));
    }

    public static void eventSend_Thread_Task_Scheduled_Repeating(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Task_Scheduled_Repeating", threadOwner, taskObject));
    }

    public static void eventSend_Permanent_Thread_Stopped(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Permanently_Stopped", threadOwner, taskObject));
    }

    public static void eventSend_Thread_Task_Removed_One_Time(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Task_Removed_One_Time", threadOwner, taskObject));
    }

    public static void eventSend_Thread_Task_Removed_Repeating(String threadOwner, Object taskObject){
        StarNub.getStarNubEventRouter().notify(new ThreadEvent("Thread_Task_Removed_Repeating", threadOwner, taskObject));
    }

    public String getExtraEventData(){
        return "Thread Owner: " + THREAD_OWNER + ", Task Object: " + TASK_OBJECT;
    }
}
