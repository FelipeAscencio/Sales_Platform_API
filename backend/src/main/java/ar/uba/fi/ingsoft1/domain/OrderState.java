package ar.uba.fi.ingsoft1.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "state")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PedidoState.class, name = "Pedido"),
        @JsonSubTypes.Type(value = EnProcesoState.class, name = "EnProceso"),
        @JsonSubTypes.Type(value = EnviadoState.class, name = "Enviado"),
        @JsonSubTypes.Type(value = CanceladoState.class, name = "Cancelado")
})
public interface OrderState {
    // Pre: The order must be in a state that allows processing.
    // Post: If processing is allowed, the order state will transition to "In Process".
    void processOrder(Order order);

    // Pre: The order must be in the "In Process" state to proceed with shipping.
    // Post: If the order is ready for shipping, its state will transition to "Shipped".
    void shipOrder(Order order);

    // Pre: The order must be in a state that allows cancellation.
    // Post: If cancellation is permitted, the order state will transition to "Cancelled".
    void cancelOrder(Order order);
}
