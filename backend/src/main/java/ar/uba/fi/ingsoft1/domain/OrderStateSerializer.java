package ar.uba.fi.ingsoft1.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class OrderStateSerializer extends JsonSerializer<OrderState> {

    @Override
    public void serialize(OrderState value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String stateName = "";
        if (value instanceof PedidoState) {
            stateName = "Pedido";
        } else if (value instanceof EnProcesoState) {
            stateName = "EnProceso";
        } else if (value instanceof EnviadoState) {
            stateName = "Enviado";
        } else if (value instanceof CanceladoState) {
            stateName = "Cancelado";
        }
        gen.writeString(stateName);
    }
}
