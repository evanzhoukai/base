package com.github.liaomengge.base_common.helper.mybatis.extension;

import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class MapResultHandler<T extends Map<?, ?>, K, V> implements ResultHandler<T> {

    private final T mappedResults;

    public MapResultHandler(T mappedResults) {
        this.mappedResults = mappedResults;
    }

    @Override
    public void handleResult(ResultContext<? extends T> resultContext) {
        Map<?, ?> retMap = resultContext.getResultObject();
        this.getMappedResults().put((K) retMap.get("key"), (V) retMap.get("value"));
    }

    public Map<K, V> getMappedResults() {
        return (Map<K, V>) mappedResults;
    }
}
