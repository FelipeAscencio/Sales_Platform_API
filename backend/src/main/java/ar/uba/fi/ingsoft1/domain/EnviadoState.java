package ar.uba.fi.ingsoft1.domain;

import ar.uba.fi.ingsoft1.services.ProductServices;

public class EnviadoState implements OrderState {

    @Override
    public void processOrder(Order order) {
        throw new IllegalStateException("El pedido ya fue enviado y no puede procesarse nuevamente.");
    }

    @Override
    public void shipOrder(Order order) {
        throw new IllegalStateException("El pedido ya fue enviado.");
    }

    @Override
    public void cancelOrder(Order order) {
        throw new IllegalStateException("El pedido no puede cancelarse una vez enviado.");
    }

    @Override
    public String toString() {
        return "Enviado";
    }
}
