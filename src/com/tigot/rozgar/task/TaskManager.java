package com.tigot.rozgar.task;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.tigot.rozgar.task.bean.TaskRunnable;

public class TaskManager {

	private static TaskManager inst;
	
	public synchronized static TaskManager getInst() {
		if (inst == null) {
			inst = new TaskManager();
		}
		return inst;
	}
	
	/**
	 * We can schedule executing of different tasks. PriorityBlockingQueue provides an ability to
	 * sort tasks by priority.
	 */
	private BlockingQueue<Runnable> generalWorkQueue = new PriorityBlockingQueue<Runnable>(5, new Comparator<Runnable>() {
		@Override
		public int compare(Runnable lhs, Runnable rhs) {
			return ((TaskRunnable) lhs).getPriority().compareTo(((TaskRunnable) rhs).getPriority());
		}
	}); 
	/**
	 * We use thread pool to execute tasks. So there are 5 threads downloading images at the same time.
	 * Thread creation/releasing is expensive task, thread itself uses lots of memory. So thread pool
	 * helps to make program more responsive, fast and resource economic.
	 */
	private ThreadPoolExecutor generalExecutor = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, generalWorkQueue);

	private Set<TaskRunnable> duplicateCollection = new HashSet<TaskRunnable>();

	/**
	 * duplicateCollection ensures that the same tasks will not be executed.
	 * @param runnable
	 */
	public void execute(TaskRunnable runnable) {
		for (TaskRunnable task : new HashSet<TaskRunnable>(duplicateCollection)) {
			if (task.isCompleted()) {
				duplicateCollection.remove(task);
			}
		}
		duplicateCollection.add(runnable);
		Log.w("TaskManager", "execute(), [duplicateCollection:" + duplicateCollection.size() + "]");
		generalExecutor.execute(runnable);
	}
	
	
}
