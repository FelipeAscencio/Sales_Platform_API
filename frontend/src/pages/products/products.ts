import { client } from '@/api/common/client';
import type { Product } from '@/api/types';

export const fetchProducts = async (): Promise<Product[]> => {
  const response = await client.api.get<Product[]>('/products');
  return response.data;
};

export const saveProduct = async (
  product: Product,
  token: string,
): Promise<unknown> => {
  if (product.id) {
    const response = await client.api.put<string>(
      `/products/${product.id}`,
      product,
      {
        headers: { Authorization: token },
      },
    );
    return response.data;
  } else {
    // Crear nuevo producto
    const response = await client.api.post<unknown>('/products', product);
    return response.data;
  }
};

export const deleteProduct = async (productId: number, token: string) => {
  await client.api.delete(`/products/${productId}`, {
    headers: { Authorization: token },
  });
};
