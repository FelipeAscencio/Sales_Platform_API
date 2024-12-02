import {
  ArrowLeft,
  LogOut,
  MinusCircle,
  Moon,
  PlusCircle,
  Search,
  ShoppingBag,
  ShoppingCart,
  Sun,
  User,
  X,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast, Toaster } from 'sonner';

import { client } from '@/api/common/client';
import { type Order, type OrderItem, type Product } from '@/api/types';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';
import { useUser } from '@/hooks/user-context';

const pastelColors = {
  light: [
    'bg-red-100',
    'bg-blue-100',
    'bg-green-100',
    'bg-yellow-100',
    'bg-purple-100',
    'bg-pink-100',
    'bg-indigo-100',
    'bg-orange-100',
  ],
  dark: [
    'bg-red-800',
    'bg-blue-800',
    'bg-green-800',
    'bg-yellow-800',
    'bg-purple-800',
    'bg-pink-800',
    'bg-indigo-800',
    'bg-orange-800',
  ],
};

type ProductWithColor = Product & { bgColor: string };

export default function ShopPage() {
  const { user, logout } = useUser();
  const [open, setOpen] = useState(false);
  const [cartOpen, setCartOpen] = useState(false);
  const [cart, setCart] = useState<OrderItem[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [showOrderDetails, setShowOrderDetails] = useState(false);
  const [_currentOrder, setCurrentOrder] = useState<Order | null>(null);

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
    const fetchProducts = async () => {
      try {
        const response = await client.api.get<ProductWithColor[]>('/products');
        const currentColors = darkMode ? pastelColors.dark : pastelColors.light;
        const fetchedProducts = response.data.map((product, index) => ({
          ...product,
          bgColor: currentColors[index % currentColors.length],
        }));
        setProducts(fetchedProducts);
      } catch (error) {
        console.error('Failed to fetch products:', error);
      }
    };

    fetchProducts();
  }, [darkMode]);

  const addToCart = (product: Product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.productId === product.id);
      if (existing) {
        return prev.map((item) =>
          item.productId === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item,
        );
      }
      return [...prev, { productId: product.id as number, quantity: 1 }];
    });
  };

  const updateQuantity = (productId: number, delta: number) => {
    setCart(
      (prev) =>
        prev
          .map((item) => {
            if (item.productId === productId) {
              const newQuantity = Math.max(0, item.quantity + delta);
              return newQuantity === 0
                ? null
                : { ...item, quantity: newQuantity };
            }
            return item;
          })
          .filter(Boolean) as OrderItem[],
    );
  };

  const removeFromCart = (productId: number) => {
    setCart((prev) => prev.filter((item) => item.productId !== productId));
  };

  const calculateTotal = () => {
    return cart.reduce((total, item) => {
      const product = products.find((p) => p.id === item.productId);
      return total + (product?.price || 0) * item.quantity;
    }, 0);
  };

  const handleBuy = async () => {
    const newOrder: Order = {
      userEmail: user?.email || 'guest@example.com',
      items: cart,
    };

    toast.promise(client.api.post<Order>('/orders', newOrder), {
      loading: 'Processing your order...',
      success: (response) => {
        console.log('Order placed successfully:', response.data);
        setCurrentOrder(response.data);
        setCart([]);
        setShowOrderDetails(false);
        return 'Order placed successfully!';
      },
      error: (error) => {
        // Default error message
        let errorMessage = 'Failed to place order!';

        // Check if there's an HTTP response with a specific error message
        if (
          error.response?.data?.error &&
          error.response?.data?.error !== 'Bad Request'
        ) {
          errorMessage = error.response.data.error; // Use the server-provided error message
        }

        console.error('Failed to place order:', errorMessage);
        return errorMessage;
      },
    });
  };

  useEffect(() => {
    const down = (e: KeyboardEvent) => {
      if (e.key === 'k' && (e.metaKey || e.ctrlKey)) {
        e.preventDefault();
        setOpen((open) => !open);
      }
    };
    document.addEventListener('keydown', down);
    return () => document.removeEventListener('keydown', down);
  }, []);

  const CartView = () => (
    <>
      <SheetHeader>
        <SheetTitle>Your Cart</SheetTitle>
      </SheetHeader>
      <ScrollArea className="h-[calc(100vh-12rem)] pr-4">
        {cart.map((item) => {
          const product = products.find((p) => p.id === item.productId);
          if (!product) return null;
          return (
            <Card key={item.productId} className="mb-4">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="font-medium">{product.name}</h3>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                      {Object.entries(product.extraAttributes || {}).map(
                        ([key, value]) => (
                          <span key={key} className="mr-2">
                            {key}: {value}
                          </span>
                        ),
                      )}
                    </div>
                  </div>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => removeFromCart(item.productId)}
                  >
                    <X className="size-4" />
                  </Button>
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => updateQuantity(item.productId, -1)}
                    >
                      <MinusCircle className="size-4" />
                    </Button>
                    <span>{item.quantity}</span>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => updateQuantity(item.productId, 1)}
                    >
                      <PlusCircle className="size-4" />
                    </Button>
                  </div>
                  <div className="font-medium">
                    ${(product.price * item.quantity).toFixed(2)}
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </ScrollArea>
      <SheetFooter>
        <div className="w-full">
          <Separator className="my-4" />
          <div className="space-y-1.5">
            <div className="flex justify-between">
              <span className="font-medium">Total</span>
              <span className="font-medium">
                ${calculateTotal().toFixed(2)}
              </span>
            </div>
          </div>
          <Button
            className="mt-6 w-full"
            size="lg"
            onClick={() => setShowOrderDetails(true)}
            disabled={cart.length === 0}
          >
            Review Order
          </Button>
        </div>
      </SheetFooter>
    </>
  );

  const OrderDetailsView = () => (
    <>
      <SheetHeader>
        <SheetTitle>Order Details</SheetTitle>
      </SheetHeader>
      <ScrollArea className="h-[calc(100vh-12rem)] pr-4">
        {cart.map((item) => {
          const product = products.find((p) => p.id === item.productId);
          if (!product) return null;
          return (
            <Card key={item.productId} className="mb-4">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="font-medium">{product.name}</h3>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                      Quantity: {item.quantity}
                    </div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                      {Object.entries(product.extraAttributes || {}).map(
                        ([key, value]) => (
                          <span key={key} className="mr-2">
                            {key}: {value}
                          </span>
                        ),
                      )}
                    </div>
                  </div>
                  <div className="font-medium">
                    ${(product.price * item.quantity).toFixed(2)}
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </ScrollArea>
      <SheetFooter>
        <div className="w-full">
          <Separator className="my-4" />
          <div className="space-y-1.5">
            <div className="flex justify-between">
              <span className="font-medium">Total</span>
              <span className="font-medium">
                ${calculateTotal().toFixed(2)}
              </span>
            </div>
          </div>
          <div className="mt-6 flex gap-4">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => setShowOrderDetails(false)}
            >
              <ArrowLeft className="mr-2 size-4" />
              Back to Cart
            </Button>
            <Button className="flex-1" size="lg" onClick={handleBuy}>
              Confirm Order
            </Button>
          </div>
        </div>
      </SheetFooter>
    </>
  );

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
              <Button
                variant="outline"
                className="w-full justify-start text-left font-normal"
                onClick={() => setOpen(true)}
              >
                <Search className="mr-2 size-4" />
                <span>Search products... (⌘K)</span>
              </Button>
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
              <Sheet
                open={cartOpen}
                onOpenChange={(open) => {
                  setCartOpen(open);
                  if (!open) {
                    setShowOrderDetails(false);
                  }
                }}
              >
                <SheetTrigger asChild>
                  <Button variant="outline" size="icon" className="relative">
                    <ShoppingCart className="size-4" />
                    {cart.length > 0 && (
                      <Badge className="absolute -right-2 -top-2 flex size-5 items-center justify-center rounded-full p-0">
                        {cart.length}
                      </Badge>
                    )}
                  </Button>
                </SheetTrigger>
                <SheetContent className="w-full sm:max-w-lg">
                  {showOrderDetails ? <OrderDetailsView /> : <CartView />}
                </SheetContent>
              </Sheet>

              {user ? (
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="rounded-full"
                    >
                      <Avatar>
                        {user.photo ? (
                          <img
                            src={user.photo}
                            alt={`${user.firstName} ${user.lastName}`}
                            className="size-full rounded-full"
                          />
                        ) : (
                          <AvatarFallback>
                            {user.firstName[0]}
                            {user.lastName[0]}
                          </AvatarFallback>
                        )}
                      </Avatar>
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem asChild>
                      <Link to="/profile">
                        <User className="mr-2 size-4" />
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
              ) : (
                <div className="flex items-center space-x-2">
                  <Button variant="ghost" asChild>
                    <Link to="/login">Sign in</Link>
                  </Button>
                  <Button variant="default" asChild>
                    <Link to="/register">Sign up</Link>
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Product Grid */}
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
          {(products as ProductWithColor[]).map((product: ProductWithColor) => (
            <Card
              key={product.id}
              className={`overflow-hidden border ${
                darkMode ? 'bg-gray-800' : 'bg-white'
              }`}
            >
              <CardContent className={`p-6 ${product.bgColor} m-2 rounded-lg`}>
                <div className="space-y-4">
                  <div>
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                      {product.name}
                    </h3>
                    <div className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                      Type: {product.type}
                    </div>
                    <div className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                      State: {product.state}
                    </div>
                    <div className="mt-2 space-y-1">
                      {Object.entries(product.extraAttributes || {}).map(
                        ([key, value]) => (
                          <Badge
                            key={key}
                            variant="secondary"
                            className="mr-2 text-gray-900 dark:text-gray-100"
                          >
                            {key}: {value}
                          </Badge>
                        ),
                      )}
                    </div>
                  </div>
                  <div className="flex items-center justify-between pt-4">
                    <div className="text-lg font-medium text-gray-900 dark:text-white">
                      ${product.price.toFixed(2)}
                    </div>
                    <Button className="w-32" onClick={() => addToCart(product)}>
                      Add to Cart
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
        {/* Command Palette for Search */}
        <CommandDialog open={open} onOpenChange={setOpen}>
          <CommandInput placeholder="Search products..." />
          <CommandList>
            <CommandEmpty>No products found.</CommandEmpty>
            <CommandGroup heading="Products">
              {products.map((product) => (
                <CommandItem
                  key={product.id}
                  onSelect={() => {
                    addToCart(product);
                    setOpen(false);
                  }}
                >
                  <div className="flex w-full items-center justify-between">
                    <div>
                      <h3 className="font-medium">{product.name}</h3>
                      <div className="text-sm text-gray-500">
                        {Object.entries(product.extraAttributes || {}).map(
                          ([key, value]) => (
                            <span key={key} className="mr-2">
                              {key}: {value}
                            </span>
                          ),
                        )}
                      </div>
                    </div>
                    <div className="font-medium">
                      ${product.price.toFixed(2)}
                    </div>
                  </div>
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </CommandDialog>
      </div>
    </div>
  );
}
