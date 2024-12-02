package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.Map;

public abstract class BaseRule implements Rule {
    private Rule nextOrderRule = null;
    private Rule nextCombinedRule = null;

    public void validate(Order order, Map<Integer, Product> orderProducts) {
        if (this.rulePass(order, orderProducts)) {
            this.validateNextRule(order, orderProducts);
        } else {
            this.validateNextCombinedRule(order, orderProducts);
        }
    }

    public void setNextOrderRule(Rule nextOrderRule) {
        this.nextOrderRule = nextOrderRule;
        if (nextCombinedRule != null) {
            nextCombinedRule.setNextOrderRule(nextOrderRule);
        }
    }
    public Rule setNextCombinedRule(Rule nextCombinedRule) {
        this.nextCombinedRule = nextCombinedRule;
        nextCombinedRule.setNextOrderRule(this.nextOrderRule);
        return this;
    }

    protected abstract boolean rulePass(Order order, Map<Integer, Product> orderProducts);
    protected abstract void triggerRuleFail(String msg); // Raise an exception according to the rule type

    protected void validateNextRule(Order order, Map<Integer, Product> orderProducts) {
        if (nextOrderRule != null) {
            nextOrderRule.validate(order, orderProducts);
        }
    }

    protected void validateNextCombinedRule(Order order, Map<Integer, Product> orderProducts) {
        if (nextCombinedRule != null) {
            try {
                nextCombinedRule.validate(order, orderProducts);
            } catch (IllegalArgumentException e) {
                this.triggerRuleFail(e.getMessage());
            }
        } else {
            this.triggerRuleFail(null);
        }
    }
}
