package com.github.liaomengge.base_common.helper.lock.distributed;

/**
 * Created by liaomengge on 17/12/19.
 */
public interface AcquiredLockWorker<T> {

    T lockSuccess();

    T lockFail();
}
