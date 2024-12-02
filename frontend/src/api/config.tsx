import { PackageSearchIcon } from 'lucide-react';
import { ShoppingCartIcon, UsersIcon } from 'lucide-react';
import type { ReactNode } from 'react';

import { HomeIcon } from '@/components/core/icons-core';

export type MenuItem = {
  title: string;
  description?: string;
  icon: ReactNode;
  link: string;
};

export const ICON_SIZE_CLASS = 'size-5';

export const menuItems: MenuItem[] = [
  {
    title: 'Home',
    icon: <HomeIcon className={ICON_SIZE_CLASS} />,
    link: '/backoffice',
  },
  {
    title: 'Productos',
    description: 'Administra tus productos',
    icon: <PackageSearchIcon className={ICON_SIZE_CLASS} />,
    link: '/backoffice/products',
  },
  {
    title: 'Pedidos',
    description: 'Gestiona los pedidos de los clientes',
    icon: <ShoppingCartIcon className={ICON_SIZE_CLASS} />,
    link: '/backoffice/orders',
  },
  {
    title: 'Usuarios',
    description: 'Administra los usuarios del sistema',
    icon: <UsersIcon className={ICON_SIZE_CLASS} />,
    link: '/backoffice/users',
  },
];
