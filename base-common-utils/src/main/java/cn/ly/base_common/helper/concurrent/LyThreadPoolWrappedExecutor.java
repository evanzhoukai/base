package cn.ly.base_common.helper.concurrent;

import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by liaomengge on 2019/1/7.
 */
@AllArgsConstructor
public class LyThreadPoolWrappedExecutor extends AbstractExecutorService implements LyThreadHelper, DisposableBean {

    private static final Logger log = LyLogger.getInstance(LyThreadPoolWrappedExecutor.class);

    @Getter
    private final ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void execute(Runnable task) {
        threadPoolExecutor.execute(createWrappedRunnable(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(createWrappedCallable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return threadPoolExecutor.submit(createWrappedRunnable(task), result);
    }

    @Override
    public void doExceptionHandle(Throwable e) {
        log.error("Current Thread[{}], Exec Exception ===> {}", Thread.currentThread().getName(),
                LyThrowableUtil.getStackTrace(e));
    }

    @Override
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return threadPoolExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return threadPoolExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPoolExecutor.awaitTermination(timeout, unit);
    }

    @Override
    public void destroy() {
        threadPoolExecutor.shutdown();
    }
}
