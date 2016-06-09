package com.tigot.rozgar.task.bean;


public interface TaskRunnable extends Runnable {

	TaskPriority getPriority();
	Boolean isCompleted();
	void onStopNotification();
	
}
