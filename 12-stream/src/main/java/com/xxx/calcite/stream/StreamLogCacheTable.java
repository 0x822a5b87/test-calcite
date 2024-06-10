package com.xxx.calcite.stream;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StreamLogCacheTable extends StreamLogTable {

    private final List<Object[]> cache = new ArrayList<>();

    public StreamLogCacheTable() {
        new Thread(() -> {
            try {
                Random r = new Random();
                while (true) {
                    TimeUnit.MICROSECONDS.sleep(r.nextInt(100));
                    long timestamp = System.currentTimeMillis();
                    String level = LEVELS[r.nextInt(LEVELS.length)];
                    Object[] o = {timestamp, level, String.format("This is a %s msg on %d", level, timestamp)};
                    cache.add(o);
                }
            } catch (Exception e) {

            }
        }).start();
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        // 每次调用返回一定数量的随机数据，否则SQL会被阻塞在scan线程内
        return Linq4j.asEnumerable(cache);
    }
}
