package com.mak001.api.plugins;

import java.util.TimerTask;

import com.mak001.ircBot.ScheduledTaskHandler;

/**
 * Allows the bot to have scheduled tasks. Better than having multiple, wasteful
 * threads for each task or plugin.
 * 
 * @author Mak001
 */
public class ScheduledTask {

	private final ScheduledTaskHandler handler;

	protected final Plugin plugin;
	protected final TimerTask task;
	protected long period;

	/**
	 * 
	 * @param plugin
	 *            - The plugin the task originates from
	 * @param task
	 *            - The task to do
	 * @param delay
	 *            - How long between executions of the task
	 */
	public ScheduledTask(Plugin plugin, TimerTask task, long period) {
		this.plugin = plugin;
		this.task = task;
		this.period = period;

		handler = plugin.bot.getTaskHandler();
	}

	/**
	 * Changes the time between executions
	 * 
	 * @param new_delay
	 *            - The new time between executions
	 */
	public void changePeriod(long new_period) {
		period = new_period;
		handler.changeTimedEvent(this, new_period);
	}

	/**
	 * @return The task to run
	 */
	public TimerTask getTask() {
		return task;
	}

	/**
	 * @return - The parent plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * @return - The time between executions
	 */
	public long getPeriod() {
		return period;
	}
}
