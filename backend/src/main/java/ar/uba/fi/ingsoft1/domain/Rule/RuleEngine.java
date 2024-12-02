package ar.uba.fi.ingsoft1.domain.Rule;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ar.uba.fi.ingsoft1.domain.Rule.RuleConfiguration.*;

@Component
public class RuleEngine {
    private List<Rule> rules;

    public RuleEngine() {
        //initialize empty list
        rules = new ArrayList<>();

        // SET DE REGLAS ENUNCIADO ORIGINAL
        Rule maxRule = new MaxProductAmountRule(MAX_PRODUCTS);
        Rule maxWeightRule = new MinMaxValueRule("weight", MAX_WEIGHT_ORIGINAL, ">", true);
        Rule incompatibleTypesRule = new MustAvoidAttributeRule("state", STATE_GASEOUS).setNextCombinedRule(new MustAvoidAttributeRule("state", STATE_LIQUID));
        maxRule.setNextOrderRule(maxWeightRule);
        maxWeightRule.setNextOrderRule(incompatibleTypesRule);
        //rules.add(maxRule);

        // SET DE REGLAS AGREGADAS
        Rule weightRule2 = new MinMaxValueRule("weight", MAX_WEIGHT_MODIFIED, ">", true);
        Rule maxRule2 = new MaxProductAmountRule(MAX_PRODUCTS).setNextCombinedRule(new MustAvoidAttributeRule("state", STATE_GASEOUS).setNextCombinedRule(new MustAvoidAttributeRule("state", STATE_LIQUID)));
        Rule maxProductsPerTypeRule = new MaxProductsPerTypeRule(Map.of("electrodoméstico", 1));
        Rule promotionItemOnlyWithTenThousandItemRule = new MustAvoidAttributeRule("promocion").setNextCombinedRule(new MinMaxValueRule("price", 10000, "<"));
        Rule IncompatibleInflamableWithFuelRule = new MustAvoidAttributeRule("inflamable").setNextCombinedRule(new MustAvoidAttributeRule("combustible"));

        weightRule2.setNextOrderRule(maxRule2);
        maxRule2.setNextOrderRule(maxProductsPerTypeRule);
        maxProductsPerTypeRule.setNextOrderRule(promotionItemOnlyWithTenThousandItemRule);
        promotionItemOnlyWithTenThousandItemRule.setNextOrderRule(IncompatibleInflamableWithFuelRule);
        rules.add(weightRule2);
    }

    public void validate(Order order, Map<Integer, Product> orderProducts) {
        for (Rule rule : rules) {
            rule.validate(order, orderProducts);
        }
    }
}
