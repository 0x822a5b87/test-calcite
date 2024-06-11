package com.xxx.calcite.practice.simple;

import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.List;

public class SimpStatistic implements Statistic {

    private final Double rowCount;

    public SimpStatistic(double rouCount) {
        this.rowCount = rouCount;
    }

    @Override
    public Double getRowCount() {
        return rowCount;
    }

    @Override
    public boolean isKey(ImmutableBitSet columns) {
        return Statistic.super.isKey(columns);
    }

    @Override
    public List<ImmutableBitSet> getKeys() {
        return Statistic.super.getKeys();
    }

    @Override
    public List<RelReferentialConstraint> getReferentialConstraints() {
        return Statistic.super.getReferentialConstraints();
    }

    @Override
    public List<RelCollation> getCollations() {
        return Statistic.super.getCollations();
    }

    @Override
    public RelDistribution getDistribution() {
        return Statistic.super.getDistribution();
    }
}
