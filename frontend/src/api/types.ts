// Usuario
export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  age: number;
  photo?: string;
  email: string;
  gender?: string;
  address?: string;
  momName: string;
  petName: string;
  color: string;
  isAdmin: boolean;
  password?: string;
}

// Producto con atributos extra
export interface Product {
  id?: number; // ID del producto
  name: string; // Nombre del producto
  type: string; // Tipo del producto
  weight: number; // Peso del producto
  price: number; // Precio del producto
  quantity: number; // Cantidad disponible del producto
  state: ProductState; // Estado del producto
  extraAttributes: Record<string, string>; // Mapa de atributos extra
}

export enum ProductState {
  GASEOUS = 'Gaseous',
  LIQUID = 'Liquid',
  SOLID = 'Solid',
}

// Pedido
export interface Order {
  id?: number; // ID del pedido
  userEmail: string; // Email del usuario que creó el pedido
  items: OrderItem[]; // Lista de productos en el pedido
  status?: OrderStatus; // Estado del pedido
  createdAt?: Date; // Fecha de creación del pedido
  confirmedAt?: Date; // Fecha de confirmación del pedido, si corresponde
  sentAt?: Date; // Fecha de envío del pedido, si corresponde
}

// Elemento del pedido (representa cada producto en el pedido)
export interface OrderItem {
  productId: number; // ID del producto
  quantity: number; // Cantidad solicitada del producto
}

// Estados del pedido
export enum OrderStatus {
  PENDING = 'PENDING',
  IN_PROCESS = 'IN_PROCESS',
  SENT = 'SENT',
  CANCELLED = 'CANCELLED',
}

// Reglas de negocio para pedidos
export interface OrderRule {
  id: number; // ID de la regla
  description: string; // Descripción de la regla
  type: RuleType; // Tipo de la regla (Ej: `MAX_ITEMS`, `MAX_WEIGHT`, etc.)
  criteria: RuleCriteria; // Criterios específicos de la regla
  message: string; // Mensaje para el usuario en caso de incumplimiento
}

// Tipos de regla
export enum RuleType {
  MAX_ITEMS = 'MAX_ITEMS', // Ej: No más de 3 productos de un mismo ítem
  MAX_WEIGHT = 'MAX_WEIGHT', // Ej: Peso total no superior a 10 kg
  INCOMPATIBLE_TYPES = 'INCOMPATIBLE_TYPES', // Ej: No combinar líquidos y gaseosos
}

// Criterios específicos para cada tipo de regla
export interface RuleCriteria {
  maxItems?: number; // Máximo número de ítems permitidos
  maxWeight?: number; // Peso máximo permitido
  incompatibleTypes?: string[]; // Lista de tipos incompatibles
}
