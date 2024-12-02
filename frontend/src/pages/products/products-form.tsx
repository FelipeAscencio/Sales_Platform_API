import React, { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';

import { type Product, ProductState } from '@/api/types';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

type ProductFormProps = {
  product: Product | null;
  onSave: (product: Product) => void;
  onCancel: () => void;
};

const ProductForm: React.FC<ProductFormProps> = ({
  product,
  onSave,
  onCancel,
}) => {
  const { control, handleSubmit } = useForm<Product>({
    defaultValues: product || {
      name: '',
      type: '',
      weight: 0,
      price: 0,
      quantity: 0,
      state: ProductState.SOLID,
      extraAttributes: {},
    },
  });

  const [extraAttributes, setExtraAttributes] = useState(
    Object.entries(product?.extraAttributes || {}),
  );

  const addExtraAttribute = () => {
    setExtraAttributes((prev) => [
      ...prev,
      [`attr_${Date.now()}`, ''], // Nueva clave-valor
    ]);
  };

  const removeExtraAttribute = (index: number) => {
    setExtraAttributes((prev) => prev.filter((_, i) => i !== index));
  };

  const updateExtraAttributeKey = (index: number, newKey: string) => {
    setExtraAttributes((prev) =>
      prev.map(
        (attr, i) => (i === index ? [newKey, attr[1]] : attr), // Actualiza solo la clave
      ),
    );
  };

  const updateExtraAttributeValue = (index: number, newValue: string) => {
    setExtraAttributes((prev) =>
      prev.map(
        (attr, i) => (i === index ? [attr[0], newValue] : attr), // Actualiza solo el valor
      ),
    );
  };

  const onSubmit = handleSubmit((data) => {
    // Convierte el estado local de `extraAttributes` en un objeto para sincronizarlo
    const formattedAttributes = Object.fromEntries(extraAttributes);
    onSave({
      ...data,
      extraAttributes: formattedAttributes,
    });
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>
          {product ? 'Editar Producto' : 'Agregar Producto'}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="grid gap-4">
          {/* Product Name */}
          <div className="grid gap-2">
            <Label htmlFor="name">Nombre</Label>
            <Controller
              control={control}
              name="name"
              rules={{ required: 'El nombre del producto es obligatorio' }}
              render={({ field }) => <Input {...field} />}
            />
          </div>

          {/* Product Type */}
          <div className="grid gap-2">
            <Label htmlFor="type">Tipo</Label>
            <Controller
              control={control}
              name="type"
              rules={{ required: 'El tipo del producto es obligatorio' }}
              render={({ field }) => <Input {...field} />}
            />
          </div>

          {/* State */}
          <div className="grid gap-2">
            <Label htmlFor="state">Estado</Label>
            <Controller
              control={control}
              name="state"
              rules={{ required: 'El estado es obligatorio' }}
              render={({ field }) => (
                <Select
                  value={field.value}
                  onValueChange={(value) => field.onChange(value)}
                >
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="Seleccione un estado" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value={ProductState.GASEOUS}>
                      {ProductState.GASEOUS}
                    </SelectItem>
                    <SelectItem value={ProductState.LIQUID}>
                      {ProductState.LIQUID}
                    </SelectItem>
                    <SelectItem value={ProductState.SOLID}>
                      {ProductState.SOLID}
                    </SelectItem>
                  </SelectContent>
                </Select>
              )}
            />
          </div>

          {/* Weight */}
          <div className="grid gap-2">
            <Label htmlFor="weight">Peso</Label>
            <Controller
              control={control}
              name="weight"
              rules={{ required: 'El peso es obligatorio' }}
              render={({ field }) => <Input type="number" {...field} />}
            />
          </div>

          {/* Price */}
          <div className="grid gap-2">
            <Label htmlFor="price">Precio</Label>
            <Controller
              control={control}
              name="price"
              rules={{ required: 'El precio es obligatorio' }}
              render={({ field }) => <Input type="number" min={0} {...field} />}
            />
          </div>

          {/* Quantity */}
          <div className="grid gap-2">
            <Label htmlFor="quantity">Cantidad</Label>
            <Controller
              control={control}
              name="quantity"
              rules={{ required: 'La cantidad es obligatoria' }}
              render={({ field }) => <Input type="number" min={0} {...field} />}
            />
          </div>

          {/* Extra Attributes */}
          <div className="grid gap-2">
            <Label>Atributos Extra</Label>
            {extraAttributes.map(([key, value], index) => (
              <div key={index} className="flex gap-2">
                <Input
                  placeholder="Clave"
                  value={key}
                  onChange={(e) =>
                    updateExtraAttributeKey(index, e.target.value)
                  }
                />
                <Input
                  placeholder="Valor"
                  value={value}
                  onChange={(e) =>
                    updateExtraAttributeValue(index, e.target.value)
                  }
                />
                <Button
                  type="button"
                  variant="destructive"
                  size="sm"
                  onClick={() => removeExtraAttribute(index)}
                >
                  Eliminar
                </Button>
              </div>
            ))}
            <Button
              type="button"
              variant="secondary"
              size="sm"
              onClick={addExtraAttribute}
            >
              Agregar Atributo Extra
            </Button>
          </div>

          {/* Form Actions */}
          <div className="flex justify-end gap-4">
            <Button type="button" variant="secondary" onClick={onCancel}>
              Cancelar
            </Button>
            <Button type="submit">{product ? 'Actualizar' : 'Agregar'}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default ProductForm;
