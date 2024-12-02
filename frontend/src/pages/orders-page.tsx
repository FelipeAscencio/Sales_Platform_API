import type { ColumnDef } from '@tanstack/react-table';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { toast, Toaster } from 'sonner';

import { client } from '@/api/common/client';
import type { Order, Product } from '@/api/types';
import { CardContent } from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useUser } from '@/hooks/user-context';

import { fetchProducts } from './products/products';

export enum OrderStatus {
  PENDING = 'Pedido',
  IN_PROCESS = 'EnProceso',
  SENT = 'Enviado',
  CANCELLED = 'Cancelado',
}

const fetchOrdersForUser = async (
  email: string,
  token: string,
): Promise<Order[]> => {
  try {
    const response = await client.api.get<Order[]>('/orders/user', {
      params: { userEmail: email },
      headers: {
        Authorization: `${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error(`Error fetching orders for user ${email}:`, error);
    return [];
  }
};

const updateOrderStatus = async (
  orderId: number,
  newStatus: OrderStatus,
  token: string,
): Promise<void> => {
  try {
    let statusToSent = '';
    if (newStatus === OrderStatus.IN_PROCESS) {
      statusToSent = 'process';
    }
    if (newStatus === OrderStatus.SENT) {
      statusToSent = 'ship';
    }
    if (newStatus === OrderStatus.CANCELLED) {
      statusToSent = 'cancel';
    }
    await client.api.put(`/orders/${orderId}/${statusToSent}`, null, {
      headers: {
        Authorization: `${token}`,
      },
    });
  } catch (error) {
    console.error(`Error updating order ${orderId} to ${newStatus}:`, error);
    throw error;
  }
};

const OrdersPage = () => {
  const { user, token, isAdmin } = useUser();
  const [orders, setOrders] = useState<Order[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedEmail, setSelectedEmail] = useState<string>('');
  const [uniqueEmails, setUniqueEmails] = useState<string[]>([]);

  // Load products on component mount
  useEffect(() => {
    const loadProducts = async () => {
      try {
        const data = await fetchProducts();
        setProducts(data);
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    };
    loadProducts();
  }, []);

  useEffect(() => {
    const loadUsers = async () => {
      if (isAdmin() && token) {
        try {
          const response = await client.api.get('/users', {
            headers: { Authorization: `${token}` },
          });
          const emails = response.data.map(
            (user: { email: string }) => user.email,
          );
          setUniqueEmails(emails);

          if (emails.length > 0) {
            setSelectedEmail(emails[0]);
          }
        } catch (error) {
          console.error('Error loading users:', error);
        }
      } else if (user?.email) {
        setUniqueEmails([user.email]);
        setSelectedEmail(user.email);
      }
    };
    loadUsers();
  }, [isAdmin, token, user?.email]);

  useEffect(() => {
    const loadOrders = async () => {
      if (selectedEmail && token) {
        try {
          const data = await fetchOrdersForUser(selectedEmail, token);
          setOrders(data);
        } catch (error) {
          console.error('Error loading orders:', error);
        }
      }
    };
    loadOrders();
  }, [selectedEmail, token]);

  const handleStatusChange = useCallback(
    async (orderId: number, newStatus: OrderStatus) => {
      if (token) {
        try {
          await updateOrderStatus(orderId, newStatus, token);
          setOrders((prevOrders) =>
            prevOrders.map((order) =>
              order.id === orderId
                ? ({ ...order, status: newStatus } as unknown as Order)
                : order,
            ),
          );
          toast.success(`Order ${orderId} status updated to ${newStatus}`, {
            description: `Changed on ${new Date().toLocaleString()}`,
          });
        } catch (error) {
          toast.error(`Failed to update status for order ${orderId}`);
        }
      }
    },
    [token],
  );

  const filteredOrders = useMemo(
    () => orders.filter((order) => order.userEmail === selectedEmail),
    [orders, selectedEmail],
  );

  const columns = useMemo<ColumnDef<Order>[]>(
    () => [
      { accessorKey: 'id', header: 'ID' },
      {
        accessorKey: 'items',
        header: 'Items',
        cell: ({ row }) => {
          // Map product details for each item in the order
          const itemsDetail = row.original.items.map((item) => {
            const product = products.find((p) => p.id === item.productId);
            return product
              ? `${product.name} (x${item.quantity})`
              : `Unknown Product (x${item.quantity})`;
          });
          return itemsDetail.join(', ');
        },
      },
      {
        accessorKey: 'createdAt',
        header: 'Created At',
        cell: ({ row }) =>
          row.original.createdAt
            ? row.original.createdAt.toLocaleString()
            : 'N/A',
      },
      {
        accessorKey: 'status',
        header: 'Status',
        cell: ({ row }) => (
          <Select
            value={row.original.status}
            onValueChange={(value: OrderStatus) =>
              row.original.id !== undefined &&
              handleStatusChange(row.original.id, value)
            }
          >
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Select status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={OrderStatus.PENDING}>
                {OrderStatus.PENDING}
              </SelectItem>
              <SelectItem value={OrderStatus.IN_PROCESS}>
                {OrderStatus.IN_PROCESS}
              </SelectItem>
              <SelectItem value={OrderStatus.SENT}>
                {OrderStatus.SENT}
              </SelectItem>
              <SelectItem value={OrderStatus.CANCELLED}>
                {OrderStatus.CANCELLED}
              </SelectItem>
            </SelectContent>
          </Select>
        ),
      },
    ],
    [products, handleStatusChange],
  );

  const table = useReactTable({
    data: filteredOrders,
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  return (
    <main className="flex flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0">
      <Toaster richColors closeButton position="bottom-right" />
      <div className="grow">
        <div className="mb-4 flex justify-end">
          <span className="content-center pr-4 text-lg font-semibold">
            Users
          </span>
          <Select
            value={selectedEmail}
            onValueChange={setSelectedEmail}
            disabled={!isAdmin()}
          >
            <SelectTrigger className="w-[250px]">
              <SelectValue placeholder="Select an email" />
            </SelectTrigger>
            <SelectContent>
              {uniqueEmails.map((email) => (
                <SelectItem key={email} value={email}>
                  {email}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <CardContent>
          <Table className="border border-b-gray-300">
            <TableHeader>
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) => (
                    <TableHead key={header.id} colSpan={header.colSpan}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext(),
                          )}
                    </TableHead>
                  ))}
                </TableRow>
              ))}
            </TableHeader>
            <TableBody>
              {table.getRowModel().rows.map((row) => (
                <TableRow key={row.id}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell
                      key={cell.id}
                      className="bg-background sticky right-0"
                    >
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext(),
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </div>
    </main>
  );
};

export default OrdersPage;
