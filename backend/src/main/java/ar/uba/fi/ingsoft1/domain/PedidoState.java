package ar.uba.fi.ingsoft1.domain;

import ar.uba.fi.ingsoft1.services.ProductServices;
import java.time.LocalDateTime;

public class PedidoState implements OrderState {
    private static final int MAX_HOURS = 24;
    private static final String EN_PROCESO = "EnProceso";
    private static final String CANCELADO = "Cancelado";

    @Override
    public void processOrder(Order order) {
        order.setState(EN_PROCESO);
    }

    @Override
    public void shipOrder(Order order) {
        throw new IllegalStateException("El pedido debe estar en proceso antes de enviarse.");
    }

    @Override
    public void cancelOrder(Order order) {
        LocalDateTime creationDate = order.getCreationDateTime();
        if (creationDate.plusHours(MAX_HOURS).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede cancelar un pedido con más de 24 horas.");
        }
        order.setState(CANCELADO);
    }

    @Override
    public String toString() {
        return "Pedido";
    }
}
