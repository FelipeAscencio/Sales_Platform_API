package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.HashMap;
import java.util.Map;

public class MaxProductsPerTypeRule extends BaseRule {
    private Map<String, Integer> typesAndAmounts;

    public MaxProductsPerTypeRule(Map<String, Integer> typesAndAmounts) {
        this.typesAndAmounts = typesAndAmounts;
    }

    @Override
    protected boolean rulePass(Order order, Map<Integer, Product> orderProducts) {
        Map<String, Integer> productCounts = new HashMap<>();
        for (Product p : orderProducts.values()) {
            String type = p.getType();
            if (productCounts.containsKey(type)) {
                productCounts.put(type, productCounts.get(type) + 1);
            } else {
                productCounts.put(type, 1);
            }
        }
        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
            if (typesAndAmounts.containsKey(entry.getKey()) && entry.getValue() > typesAndAmounts.get(entry.getKey())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void triggerRuleFail(String msg) {
        throw new IllegalArgumentException("Se excedió la cantidad máxima de productos por tipo");
    }
}
