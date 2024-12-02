package ar.uba.fi.ingsoft1.domain;

import ar.uba.fi.ingsoft1.services.ProductServices;

public class EnProcesoState implements OrderState {
    private static final String ENVIADO = "Enviado";

    @Override
    public void processOrder(Order order) {
        throw new IllegalStateException("El pedido ya está en proceso.");
    }

    @Override
    public void shipOrder(Order order) {
        order.setState(ENVIADO);
    }

    @Override
    public void cancelOrder(Order order) {
        throw new IllegalStateException("El pedido no puede cancelarse una vez en proceso.");
    }

    @Override
    public String toString() {
        return "EnProceso";
    }
}
