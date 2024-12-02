// src/pages/users/users-form.tsx
import React from 'react';
import { Controller, useForm } from 'react-hook-form';

import type { User } from '@/api/types';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type UserFormProps = {
  user: User | null;
  onSave: (user: User) => void;
  onCancel: () => void;
};

const UserForm: React.FC<UserFormProps> = ({ user, onSave, onCancel }) => {
  const { control, handleSubmit } = useForm<User>({
    defaultValues: user || {
      firstName: '',
      lastName: '',
      email: '',
      age: 0,
      isAdmin: false,
    },
  });

  const onSubmit = handleSubmit((data) => {
    onSave(data);
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>{user ? 'Editar Usuario' : 'Agregar Usuario'}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} className="grid gap-4">
          <div className="grid gap-2">
            <Label htmlFor="firstName">Nombre</Label>
            <Controller
              control={control}
              name="firstName"
              rules={{ required: 'El nombre es obligatorio' }}
              render={({ field }) => <Input {...field} />}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="lastName">Apellido</Label>
            <Controller
              control={control}
              name="lastName"
              rules={{ required: 'El apellido es obligatorio' }}
              render={({ field }) => <Input {...field} />}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Controller
              control={control}
              name="email"
              rules={{ required: 'El email es obligatorio' }}
              render={({ field }) => <Input type="email" {...field} />}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="age">Edad</Label>
            <Controller
              control={control}
              name="age"
              render={({ field }) => <Input type="number" {...field} />}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="isAdmin">Es Admin</Label>
            <Controller
              control={control}
              name="isAdmin"
              render={({ field }) => (
                <Input
                  type="checkbox"
                  {...field}
                  checked={field.value}
                  onChange={(e) => field.onChange(e.target.checked)}
                  value={undefined}
                />
              )}
            />
          </div>
          <div className="flex justify-end gap-4">
            <Button type="button" variant="secondary" onClick={onCancel}>
              Cancelar
            </Button>
            <Button type="submit">{user ? 'Actualizar' : 'Agregar'}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default UserForm;
