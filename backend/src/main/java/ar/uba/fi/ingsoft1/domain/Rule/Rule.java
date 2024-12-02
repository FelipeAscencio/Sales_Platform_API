package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;

import java.util.Map;

public interface Rule {
    // Pre: The order parameter must be a valid Order instance.
    // Post: If any of the products in order do not meet the validation rule an IllegalArgumentException will be thrown.
    void validate(Order order, Map<Integer, Product> orderProducts);
    void setNextOrderRule(Rule nextOrderRule);
    Rule setNextCombinedRule(Rule nextCombinedRule);
}
