package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.Map;

public class MaxProductAmountRule extends BaseRule {
    private final int maxProducts;
    public MaxProductAmountRule(int maxAmount) {
        this.maxProducts = maxAmount;
    }

    @Override
    protected boolean rulePass(Order order, Map<Integer, Product> orderProducts) {
        Map<Integer, Integer> productCounts = order.getOrderedProducts();
        return productCounts.values().stream().allMatch(count -> count <= maxProducts);
    }

    @Override
    protected void triggerRuleFail(String msg) {
        if (msg != null) {
            throw new IllegalArgumentException("más de " + maxProducts + " items del mismo producto no permitido combinado con " + msg);
        }
        throw new IllegalArgumentException("más de " + maxProducts + " items del mismo producto no permitido");
    }
}
