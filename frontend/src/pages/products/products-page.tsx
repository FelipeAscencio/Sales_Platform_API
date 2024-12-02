import React, { useEffect, useState } from 'react';
import { Toaster } from 'sonner';

import type { Product } from '@/api/types';
import { useUser } from '@/hooks/user-context';

import { deleteProduct, fetchProducts, saveProduct } from './products';
import ProductForm from './products-form';
import ProductsTable from './products-table';

const ProductsPage: React.FC = () => {
  const { token } = useUser();
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [showForm, setShowForm] = useState<boolean>(false);

  useEffect(() => {
    const loadProducts = async () => {
      try {
        const data = await fetchProducts();
        setProducts(data);
      } catch (error) {
        console.error('Error fetching products:', error);
      } finally {
        setIsLoading(false);
      }
    };
    loadProducts();
  }, []);

  const handleAddProduct = () => {
    setSelectedProduct(null);
    setShowForm(true);
  };

  const handleEditProduct = (product: Product) => {
    setSelectedProduct(product);
    setShowForm(true);
  };

  const handleDeleteProduct = async (productId: number, token: string) => {
    try {
      await deleteProduct(productId, token);
      setProducts(products.filter((p) => p.id !== productId));
    } catch (error) {
      console.error('Error deleting product:', error);
    }
  };

  const handleSaveProduct = async (product: Product, token: string) => {
    try {
      await saveProduct(product, token);
      const updatedProducts = await fetchProducts();
      setProducts(updatedProducts);
      setShowForm(false);
    } catch (error) {
      console.error('Error saving product:', error);
    }
  };

  const handleCancel = () => {
    setShowForm(false);
  };

  if (isLoading) {
    return (
      <div className="mt-10 text-center">
        <span>Cargando productos...</span>
      </div>
    );
  }

  return (
    <main className="flex flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0">
      <Toaster richColors closeButton position="bottom-right" />
      <div className="grow">
        <ProductsTable
          products={products}
          onEdit={handleEditProduct}
          onDelete={(productId) =>
            token && handleDeleteProduct(productId, token)
          }
          onAddProduct={handleAddProduct}
        />
      </div>
      {showForm && (
        <div className="w-96">
          <ProductForm
            product={selectedProduct}
            onSave={(product) => token && handleSaveProduct(product, token)}
            onCancel={handleCancel}
          />
        </div>
      )}
    </main>
  );
};

export default ProductsPage;
