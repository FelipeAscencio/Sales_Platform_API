import './globals.css';

import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Layout from './components/core/layout';
import { ThemeProvider } from './components/theme-provider';
import { UserProvider } from './hooks/user-context';
import { DashboardPage } from './pages';
import ForgotPasswordPage from './pages/forgot-password';
import LoginPage from './pages/login-page';
import OrdersPage from './pages/orders-page';
import ProductsPage from './pages/products/products-page';
import RegisterPage from './pages/register-page';
import ShopPage from './pages/shop-page';
import OrderPage from './pages/users/users-order';
import UsersPage from './pages/users/users-page';
import UserProfile from './pages/users/users-profile';

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
        <UserProvider>
          <Routes>
            {/* Routes without Layout */}
            <Route path="/" element={<ShopPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/orders" element={<OrderPage />} />
            {/* Routes with Layout */}
            <Route path="/profile" element={<UserProfile />} />
            <Route
              path="/backoffice/*"
              element={
                <Layout>
                  <Routes>
                    <Route path="/" element={<DashboardPage />} />
                    <Route path="/products" element={<ProductsPage />} />
                    <Route path="/users" element={<UsersPage />} />
                    <Route path="/orders" element={<OrdersPage />} />
                  </Routes>
                </Layout>
              }
            />
          </Routes>
        </UserProvider>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
