import {
  LogOut,
  Moon,
  ShoppingBag,
  Sun,
  Trash,
  User as UserIcon,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast, Toaster } from 'sonner';

import { client } from '@/api/common/client';
import type { Order, Product } from '@/api/types';
import { OrderStatus } from '@/api/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useUser } from '@/hooks/user-context';

export default function OrderPage() {
  const { user, token, logout } = useUser();
  const [orders, setOrders] = useState<Order[]>([]);
  const [products, setProducts] = useState<{ [key: number]: Product }>({});
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem('darkMode') === 'true';
  });

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    localStorage.setItem('darkMode', darkMode.toString());
  }, [darkMode]);

  const toggleDarkMode = () => {
    setDarkMode((prev) => !prev);
  };

  useEffect(() => {
    if (!user) return;

    const fetchOrders = async () => {
      try {
        const response = await client.api.get<Order[]>(`/orders/user`, {
          params: { userEmail: user.email },
          headers: { Authorization: `${token}` },
        });
        setOrders(response.data);
      } catch (error) {
        console.error('Failed to fetch orders:', error);
      }
    };

    fetchOrders();
  }, [token, user]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await client.api.get<Product[]>('/products');
        const productMap = response.data.reduce(
          (acc, product) => ({
            ...acc,
            [product.id as number]: product,
          }),
          {},
        );
        setProducts(productMap);
      } catch (error) {
        console.error('Failed to fetch products:', error);
      }
    };

    fetchProducts();
  }, []);

  const cancelOrder = async (orderId: number) => {
    try {
      await client.api.put(`/orders/${orderId}/cancel`, null, {
        headers: {
          Authorization: `${token}`,
        },
      });
      toast.success('Order canceled successfully!');
      setOrders((prev) =>
        prev.map((order) =>
          order.id === orderId
            ? { ...order, status: OrderStatus.CANCELLED }
            : order,
        ),
      );
    } catch (error) {
      console.error(`Failed to cancel order ${orderId}:`, error);
      toast.error('Failed to cancel order. Please try again.');
    }
  };

  const getStatusColor = (status: OrderStatus) => {
    switch (status) {
      case OrderStatus.PENDING:
        return 'bg-yellow-500';
      case OrderStatus.IN_PROCESS:
        return 'bg-blue-500';
      case OrderStatus.SENT:
        return 'bg-purple-500';
      case OrderStatus.CANCELLED:
        return 'bg-red-500';
      default:
        return 'bg-gray-500';
    }
  };

  const formatDate = (date: Date) => {
    return new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(date));
  };

  if (!user) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-lg font-semibold">
          Please log in to view your orders.
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Toaster richColors closeButton position="bottom-right" />
      {/* Navbar */}
      <nav className="border-b bg-white dark:bg-gray-800">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between">
            <div className="flex items-center">
              <Link to="/" className="text-xl font-bold">
                Store
              </Link>
            </div>

            <div className="flex items-center space-x-4">
              {/* Dark Mode Toggle Button */}
              <Button
                variant="ghost"
                size="icon"
                onClick={toggleDarkMode}
                className="text-gray-600 dark:text-gray-400"
              >
                {darkMode ? (
                  <Sun className="size-5" />
                ) : (
                  <Moon className="size-5" />
                )}
              </Button>

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon" className="rounded-full">
                    <Avatar>
                      <AvatarFallback>
                        {user.firstName[0]}
                        {user.lastName[0]}
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem asChild>
                    <Link to="/profile">
                      <UserIcon className="mr-2 size-4" />
                      Profile
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/orders">
                      <ShoppingBag className="mr-2 size-4" />
                      Your Orders
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={logout}>
                    <LogOut className="mr-2 size-4" />
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <h1 className="mb-6 text-3xl font-bold">Your Orders</h1>
        <div className="space-y-6">
          {orders.map((order) => (
            <Card key={order.id}>
              <CardHeader>
                <CardTitle className="flex items-center justify-between">
                  <span>Order #{order.id}</span>
                  <Badge
                    className={`${getStatusColor(order.status ?? OrderStatus.PENDING)} text-white`}
                  >
                    {order.status
                      ? order.status.charAt(0).toUpperCase() +
                        order.status.slice(1)
                      : 'Unknown'}
                  </Badge>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="mb-4">
                  <p>
                    <strong>Created:</strong>{' '}
                    {order.createdAt ? formatDate(order.createdAt) : 'Unknown'}
                  </p>
                  {order.confirmedAt && (
                    <p>
                      <strong>Confirmed:</strong>{' '}
                      {formatDate(order.confirmedAt)}
                    </p>
                  )}
                  {order.sentAt && (
                    <p>
                      <strong>Sent:</strong> {formatDate(order.sentAt)}
                    </p>
                  )}
                </div>
                <ScrollArea className="h-[200px] w-full rounded-md border p-4">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Product</TableHead>
                        <TableHead>Quantity</TableHead>
                        <TableHead className="text-right">Price</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {order.items.map((item) => {
                        const product = products[item.productId];
                        return product ? (
                          <TableRow key={item.productId}>
                            <TableCell>{product.name}</TableCell>
                            <TableCell>{item.quantity}</TableCell>
                            <TableCell className="text-right">
                              ${(product.price * item.quantity).toFixed(2)}
                            </TableCell>
                          </TableRow>
                        ) : null;
                      })}
                    </TableBody>
                  </Table>
                </ScrollArea>
                <div className="mt-4 flex items-center justify-between">
                  <strong>Total:</strong>
                  <span>
                    $
                    {order.items
                      .reduce(
                        (total, item) =>
                          total +
                          (products[item.productId]?.price || 0) *
                            item.quantity,
                        0,
                      )
                      .toFixed(2)}
                  </span>
                </div>
                {order.status === ('Pedido' as OrderStatus) ? (
                  <div className="mt-4 text-right">
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={() =>
                        order.id !== undefined && cancelOrder(order.id)
                      }
                    >
                      <Trash className="mr-2 size-4" />
                      Cancel Order
                    </Button>
                  </div>
                ) : null}
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}
