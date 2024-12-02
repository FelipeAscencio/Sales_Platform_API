package ar.uba.fi.ingsoft1.domain;

import ar.uba.fi.ingsoft1.services.ProductServices;

public class CanceladoState implements OrderState {

    @Override
    public void processOrder(Order order) {
        throw new IllegalStateException("El pedido ha sido cancelado y no puede procesarse.");
    }

    @Override
    public void shipOrder(Order order) {
        throw new IllegalStateException("El pedido ha sido cancelado y no puede enviarse.");
    }

    @Override
    public void cancelOrder(Order order) {
        throw new IllegalStateException("El pedido ya está cancelado.");
    }
    
    @Override
    public String toString() {
        return "Cancelado";
    }
}
