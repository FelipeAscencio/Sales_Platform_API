package ar.uba.fi.ingsoft1.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStateConverter implements AttributeConverter<OrderState, String> {

    @Override
    public String convertToDatabaseColumn(OrderState orderState) {
        if (orderState == null) {
            return null;
        }
        return orderState.toString();
    }

    @Override
    public OrderState convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new PedidoState();
        }
        switch (dbData) {
            case "EnProceso":
                return new EnProcesoState();
            case "Enviado":
                return new EnviadoState();
            case "Cancelado":
                return new CanceladoState();
            default:
                return new PedidoState();
        }
    }
}