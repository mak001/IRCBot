package com.mak001.ircbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.mak001.api.plugins.Plugin;
import com.mak001.api.plugins.ScheduledTask;


public class ScheduledTaskHandler {

	private List<ScheduledTask> tasks = new ArrayList<ScheduledTask>();
	private Timer timer;

	public ScheduledTaskHandler() {
		timer = new Timer();
	}

	public void changeTimedEvent(ScheduledTask scheduledTask, long new_delay) {
		
		// TODO Auto-generated method stub

	}

	public void removePluginTasks(Plugin p) {
		List<ScheduledTask> _tasks = new ArrayList<ScheduledTask>();
		for (ScheduledTask task : tasks) {
			if (task.getPlugin().equals(p)) {
				_tasks.add(task);
			}
		}
		removeTasks(_tasks);
	}

	private void removeTasks(List<ScheduledTask> _tasks) {
		tasks.removeAll(_tasks);
		timer.cancel();
		timer.purge();
		for (ScheduledTask task : tasks) {
			addTask(task);
		}
	}

	public void removeTask(ScheduledTask task) {
		if (tasks.contains(task)) {
			tasks.remove(task);
			timer.cancel();
			timer.purge();
			for (ScheduledTask _task : tasks) {
				addTask(_task);
			}
		}
	}

	public void addTask(ScheduledTask task) {
		timer.scheduleAtFixedRate(task.getTask(), 1, task.getPeriod());
		if (!tasks.contains(task)) tasks.add(task);
	}

}
