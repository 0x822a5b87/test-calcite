package com.xxx.calcite.optimizer;

import com.xxx.calcite.csvRelNode.CSVProject;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalProject;

public class CSVProjectRule  extends RelRule<CSVProjectRule.Config> {

    // if RelNode match rule, then enter onMatch and covert LogicalProject to
    @Override
    public void onMatch(RelOptRuleCall call) {
        final LogicalProject project = call.rel(0);
        final CSVProject converted = convert(project);
        if (converted != null) {
            call.transformTo(converted);
        }
    }

    /** Rule configuration. */
    public interface Config extends RelRule.Config {
        // set match rule : root is a LogicalProject
        Config DEFAULT = EMPTY
                .withOperandSupplier(b0 ->
                        b0.operand(LogicalProject.class).anyInputs())
                .as(Config.class);

        @Override default CSVProjectRule toRule() {
            return new CSVProjectRule(this);
        }
    }

    private CSVProjectRule(Config config) {
        super(config);
    }


    /**
     * convert LogicalProject to CSVProject
     */
    public CSVProject convert(RelNode rel) {
        final LogicalProject project = (LogicalProject) rel;
        final RelTraitSet traitSet = project.getTraitSet();
        return new CSVProject(project.getCluster(), traitSet,
                              project.getInput(), project.getProjects(),
                              project.getRowType());
    }
}
