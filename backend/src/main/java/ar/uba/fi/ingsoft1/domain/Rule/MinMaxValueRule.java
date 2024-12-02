package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.Map;

public class MinMaxValueRule extends BaseRule {
    private String attribute;
    private double amount;
    private boolean combinedProductsMode;
    private String mode;

    public MinMaxValueRule(String attribute, double amount, String mode) {
        this.attribute = attribute;
        this.amount = amount;
        this.combinedProductsMode = false;
        this.mode = mode;
    }

    public MinMaxValueRule(String attribute, double amount, String mode, boolean combinedProductsMode) {
        this.attribute = attribute;
        this.amount = amount;
        this.combinedProductsMode = combinedProductsMode;
        this.mode = mode;
    }

    @Override
    protected boolean rulePass(Order order, Map<Integer, Product> orderProducts) {
        double total = 0;
        Map<Integer, Integer> orderedProducts = order.getOrderedProducts();
        for (Integer id : orderedProducts.keySet()) {
            Map<String, String> attributes = RuleConfiguration.getAllAttributes(orderProducts.get(id));
            double attributeValue = attributes.get(attribute) != null ? Double.parseDouble(attributes.get(attribute)) : 0.00;
            if (combinedProductsMode) {
                total += (attributeValue * orderedProducts.get(id));
            } else {
                if (attributeValue > total) {
                    total = attributeValue;
                }
            }
        }
        return switch (mode) {
            case "<" -> total >= amount;
            case ">" -> total <= amount;
            default -> false;
        };
    }

    @Override
    protected void triggerRuleFail(String msg) {
        if (msg != null) {
            throw new IllegalArgumentException("valor " + attribute + " " + mode + " a " + amount + " no permitido combinado con" + msg);
        }
        throw new IllegalArgumentException("valor " + attribute + " " + mode + " a " + amount + " no permitido");
    }
}
