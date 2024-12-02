package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.Map;

public class MustAvoidAttributeRule extends BaseRule {
    private String attribute;
    private String value;

    public MustAvoidAttributeRule(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public MustAvoidAttributeRule(String attribute) {
        this.attribute = attribute;
        this.value = "true";
    }

    @Override
    protected boolean rulePass(Order order, Map<Integer, Product> orderProducts) {
        for (Product p : orderProducts.values()) {
            Map<String, String> attributes = RuleConfiguration.getAllAttributes(p);
            String value = attributes.get(attribute);
            if (value != null && value.equals(this.value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void triggerRuleFail(String msg) {
        if (msg != null) {
            throw new IllegalArgumentException("producto con " + attribute + ": " + value + " no permitido combinado con " + msg);
        }
        throw new IllegalArgumentException("producto con " + attribute + ": " + value + " no permitido");
    }
}
