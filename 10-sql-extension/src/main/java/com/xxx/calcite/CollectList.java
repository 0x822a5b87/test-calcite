package com.xxx.calcite;

import java.util.ArrayList;
import java.util.List;

public class CollectList {
    public List<Object> init() {
        return new ArrayList<>();
    }

    public List<Object> add(List<Object> accumulator, Object value) {
        accumulator.add(value);
        return accumulator;
    }

    public List<Object> result(List<Object> accumulator) {
        return accumulator;
    }
}
