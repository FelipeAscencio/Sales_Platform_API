package ar.uba.fi.ingsoft1.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;

@Entity // Marca esta clase como una entidad JPA
@Table(name = "orders") // Nombre de la tabla en la base de datos
public class Order {

    private static final String PEDIDO = "Pedido";
    private static final String EN_PROCESO = "EnProceso";
    private static final String ENVIADO = "Enviado";
    private static final String CANCELADO = "Cancelado";

    @Id // Marca este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private int id;

    @ElementCollection // JPA puede manejar mapas con @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id")) // Define una tabla para almacenar el mapa
    @MapKeyColumn(name = "product_id") // Columna para las claves del mapa
    @Column(name = "quantity") // Columna para los valores del mapa
    private Map<Integer, Integer> orderedProducts = new HashMap<>();

    @JsonSerialize(using = OrderStateSerializer.class) // Serialización personalizada para JSON
    @Convert(converter = OrderStateConverter.class) // Conversión personalizada para la base de datos
    private OrderState state;

    @Column(name = "creation_date_time", nullable = false) // Fecha de creación
    private final LocalDateTime creationDateTime;

    @Column(name = "process_date_time") // Fecha de procesamiento (puede ser nula)
    private LocalDateTime processDateTime;

    @Column(name = "ship_date_time") // Fecha de envío (puede ser nula)
    private LocalDateTime shipDateTime;

    @Column(name = "user_email", nullable = false) // Email del usuario
    private final String userEmail;

    // Constructor JPA (obligatorio para Hibernate)
    protected Order() {
        this.creationDateTime = LocalDateTime.now();
        this.userEmail = "";
    }

    // Constructor principal
    public Order(List<Integer> productIds, List<Integer> quantities, String email) {
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("La cantidad de IDs de productos y de cantidades debe ser igual.");
        }

        saveProducts(productIds, quantities);
        this.creationDateTime = LocalDateTime.now();
        this.processDateTime = null;
        this.shipDateTime = null;
        this.state = new PedidoState();
        this.userEmail = email;
    }

    // Constructor secundario
    public Order(Map<Integer, Integer> orderedProducts, String state, LocalDateTime creationDateTime, String userEmail) {
        this.orderedProducts = orderedProducts;
        setState(state);
        this.creationDateTime = creationDateTime;
        this.processDateTime = null;
        this.shipDateTime = null;
        this.userEmail = userEmail;
    }

    private void saveProducts(List<Integer> productIds, List<Integer> quantities) {
        this.orderedProducts = new HashMap<>();
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);
            this.orderedProducts.put(productId, quantity);
        }
    }

    public void processOrder() {
        state.processOrder(this);
        this.processDateTime = LocalDateTime.now();
    }

    public void shipOrder() {
        state.shipOrder(this);
        this.shipDateTime = LocalDateTime.now();
    }

    public void cancelOrder() {
        state.cancelOrder(this);
    }

    public void setState(String state) {
        if (Objects.equals(state, PEDIDO)) {
            this.state = new PedidoState();
        } else if (Objects.equals(state, EN_PROCESO)) {
            this.state = new EnProcesoState();
        } else if (Objects.equals(state, ENVIADO)) {
            this.state = new EnviadoState();
        } else if (Objects.equals(state, CANCELADO)) {
            this.state = new CanceladoState();
        } else {
            throw new IllegalArgumentException("Estado no válido: " + state);
        }
    }

    public Object getState() {
        return this.state;
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public LocalDateTime getProcessDateTime() {
        return processDateTime;
    }

    public LocalDateTime getShipDateTime() {
        return shipDateTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setId(int id) {
        this.id = id;
    }

}
