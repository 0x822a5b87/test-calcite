package com.xxx.calcite;

import com.xxx.calcite.Utils.SqlToRelNode;
import com.xxx.calcite.optimizer.CSVProjectRule;
import com.xxx.calcite.optimizer.CSVProjectRuleWithCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


class PlannerTest {
    @Test
    public void testCustomRule() throws SqlParseException {
        final String sql = "select Id from data ";
        // create builder to build hep program
        HepProgramBuilder programBuilder = HepProgram.builder();
        // add rule and rule with cost
        HepProgram program = programBuilder.addRuleInstance(CSVProjectRule.Config.DEFAULT.toRule())
                                           .addRuleInstance(CSVProjectRuleWithCost.Config.DEFAULT.toRule())
                                           .build();
        // create planner
        HepPlanner hepPlanner = new HepPlanner(program);
        RelNode    relNode    = SqlToRelNode.getSqlNode(sql, hepPlanner);
        //未优化算子树结构
        System.out.println(RelOptUtil.toString(relNode));

        // planner should set root and call findBestExp() to get optimized RelNode
        RelOptPlanner planner = relNode.getCluster().getPlanner();
        planner.setRoot(relNode);
        RelNode bestExp = planner.findBestExp();
        //优化后接结果
        System.out.println("===========RBO优化结果============");
        System.out.println(RelOptUtil.toString(bestExp));

        // planner should set root and call findBestExp() to get optimized RelNode
        RelOptPlanner relOptPlanner = relNode.getCluster().getPlanner();
        relOptPlanner.addRule(CSVProjectRule.Config.DEFAULT.toRule());
        relOptPlanner.addRule(CSVProjectRuleWithCost.Config.DEFAULT.toRule());
        relOptPlanner.setRoot(relNode);
        RelNode exp = relOptPlanner.findBestExp();
        System.out.println("===========CBO优化结果============");
        System.out.println(RelOptUtil.toString(exp));


    }

    @Test
    public void testRBOAndCBO() throws SqlParseException {
        final String      sql        = "select * from data ";

        ArrayList<RelRule.Config> ruleConfigs = new ArrayList<>();
        ruleConfigs.add(CSVProjectRule.Config.DEFAULT);
        ruleConfigs.add(CSVProjectRuleWithCost.Config.DEFAULT);
        RelNode sqlNode = SqlToRelNode.findUnoptimizedExp(sql, ruleConfigs);
        // output project before optimize
        System.out.println(RelOptUtil.toString(sqlNode));
        // LogicalProject(Id=[$0], Name=[$1], Score=[$2])
        //  LogicalTableScan(table=[[csv, data]])

        RelNode bestExp = SqlToRelNode.findHepBestExp(sqlNode, ruleConfigs);
        System.out.println(RelOptUtil.toString(bestExp));
        // CSVProject(Id=[$0], Name=[$1], Score=[$2])
        //  LogicalTableScan(table=[[csv, data]])

        ruleConfigs.clear();
        ruleConfigs.add(CSVProjectRuleWithCost.Config.DEFAULT);
        ruleConfigs.add(CSVProjectRule.Config.DEFAULT);
        bestExp = SqlToRelNode.findHepBestExp(sqlNode, ruleConfigs);
        System.out.println(RelOptUtil.toString(bestExp));
        // CSVProjectWithCost(Id=[$0], Name=[$1], Score=[$2])
        //  LogicalTableScan(table=[[csv, data]])

        RelNode bestVolcanoExp = SqlToRelNode.findVolcanoBestExp(sqlNode, ruleConfigs);
        System.out.println(RelOptUtil.toString(bestVolcanoExp));
    }

    @Test
    public void testHepPlanner() throws SqlParseException {
        final String      sql            = "select a.Id from data as a join data b on a.Id = b.Id where a.Id>1";
        HepProgramBuilder programBuilder = HepProgram.builder();
        HepProgram hepProgram = programBuilder.addRuleInstance(
                                                      FilterJoinRule.FilterIntoJoinRule.Config.DEFAULT.toRule())
                                              .build();
        HepPlanner hepPlanner = new HepPlanner(hepProgram);
        RelNode    relNode    = SqlToRelNode.getSqlNode(sql, hepPlanner);
        //未优化算子树结构
        System.out.println(RelOptUtil.toString(relNode));
        RelOptPlanner planner = relNode.getCluster().getPlanner();
        planner.setRoot(relNode);
        RelNode bestExp = planner.findBestExp();
        //优化后接结果
        System.out.println(RelOptUtil.toString(bestExp));
    }

    @Test
    public void testGraph() throws SqlParseException {
        String            sql     = "select * from data where id = 1 and name = 'test'";
        HepProgramBuilder builder = HepProgram.builder();
        HepPlanner        planner = new HepPlanner(builder.build());
        RelNode           relNode = SqlToRelNode.getSqlNode(sql, planner);
        System.out.println(RelOptUtil.toString(relNode));
        planner.setRoot(relNode);
    }

}
