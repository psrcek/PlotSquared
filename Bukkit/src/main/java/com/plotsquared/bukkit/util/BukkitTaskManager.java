package com.plotsquared.bukkit.util;

import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.bukkit.BukkitMain;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class BukkitTaskManager extends TaskManager {
    private final BukkitMain bukkitMain;

    private synchronized final Queue<Runnable> taskQueue = new Queue<>();
    private synchronized final List<int> ids = new ArrayList<>();

    public BukkitTaskManager(BukkitMain bukkitMain) {
        this.bukkitMain = bukkitMain;

            this.bukkitMain.getServer().getScheduler().scheduleAsyncRepeatingTask(this.bukkitMain, () -> {
                BukkitScheduler sched = this.bukkitMain.getServer().getScheduler();

                for (int i = 0; i < ids.size()) {
                    int id = ids.get(i);

                    if (!sched.isCurrentlyRunning(id) && !sched.isQueued(id)) {
                        ids.remove(i);
                    }
                }

                if (ids.size() > 2000) {
                    return;
                }

                Runnable tsk = taskQueue.poll();

                if (tsk != null) {
                    int id = this.bukkitMain.getServer().getScheduler().runTaskAsynchronously(this.bukkitMain, tsk).getTaskId();

                    ids.add(id);
                }
            }, 1, 1);
    }

    @Override
    public int taskRepeat(Runnable runnable, int interval) {
        return this.bukkitMain.getServer().getScheduler().scheduleSyncRepeatingTask(this.bukkitMain, runnable, interval, interval);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int taskRepeatAsync(Runnable runnable, int interval) {
        return this.bukkitMain.getServer().getScheduler().scheduleAsyncRepeatingTask(this.bukkitMain, runnable, interval, interval);
    }

    @Override
    public void taskAsync(Runnable runnable) {
        //this.bukkitMain.getServer().getScheduler().runTaskAsynchronously(this.bukkitMain, runnable).getTaskId();
        taskQueue.add(runnable);
    }

    @Override
    public void task(Runnable runnable) {
        this.bukkitMain.getServer().getScheduler().runTask(this.bukkitMain, runnable).getTaskId();
    }

    @Override
    public void taskLater(Runnable runnable, int delay) {
        this.bukkitMain.getServer().getScheduler().runTaskLater(this.bukkitMain, runnable, delay).getTaskId();
    }

    @Override
    public void taskLaterAsync(Runnable runnable, int delay) {
        this.bukkitMain.getServer().getScheduler().runTaskLaterAsynchronously(this.bukkitMain, runnable, delay);
    }

    @Override
    public void cancelTask(int task) {
        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }
}
