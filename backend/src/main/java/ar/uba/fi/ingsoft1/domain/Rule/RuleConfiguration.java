package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Product;

import java.util.HashMap;
import java.util.Map;

public class RuleConfiguration {
    public static final double MAX_WEIGHT_ORIGINAL = 10.0;
    public static final double MAX_WEIGHT_MODIFIED = 15.0;
    public static final int MAX_PRODUCTS = 3;
    public static final String STATE_GASEOUS = "Gaseous";
    public static final String STATE_LIQUID = "Liquid";

    public static Map<String, String> getAllAttributes(Product product) {
        Map<String, String> allAttributes = new HashMap<>();

        // Static attributes.
        allAttributes.put("type", product.getType());
        allAttributes.put("state", product.getState());
        allAttributes.put("quantity", String.valueOf(product.getQuantity()));
        allAttributes.put("price", String.valueOf(product.getPrice()));
        allAttributes.put("weight", String.valueOf(product.getWeight()));

        // Dynamic attributes.
        Map<String, String> dynamicAttributes = product.getAllAttributes();
        if (dynamicAttributes != null) {
            allAttributes.putAll(dynamicAttributes);
        }

        return allAttributes;
    }
}
