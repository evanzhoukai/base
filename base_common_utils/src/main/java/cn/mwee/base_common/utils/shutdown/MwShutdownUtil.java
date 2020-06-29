package cn.mwee.base_common.utils.shutdown;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

/**
 * Created by liaomengge on 2018/12/20.
 */
@UtilityClass
public class MwShutdownUtil {

    public <T> void registerShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

    /**
     * to see {@link MwShutdownUtil#registerShutdownHook(Runnable)}
     *
     * @param t
     * @param consumer
     * @param <T>
     */
    @Deprecated
    public <T> void registerShutdownHook(T t, Consumer<T> consumer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> consumer.accept(t)));
    }
}
