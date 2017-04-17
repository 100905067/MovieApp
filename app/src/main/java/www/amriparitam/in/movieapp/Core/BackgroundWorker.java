package www.amriparitam.in.movieapp.Core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Amrita Pritam on 4/14/2017.
 */

public class BackgroundWorker {
    private static BackgroundWorker backgroundWorker;
    private Executor executor;
    private Thread thread;
    private Scheduler scheduler;

    private BackgroundWorker() {
        executor = Executors.newSingleThreadExecutor();
        scheduler = Schedulers.from(executor);
        executor.execute(new Thread(Thread.currentThread()));
    }

    public static BackgroundWorker get() {
        if(backgroundWorker == null) {
            backgroundWorker = new BackgroundWorker();
        }
        return backgroundWorker;
    }

    public Executor getExecutor() {
        return executor;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
