import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';

import { client } from '@/api/common/client';
import type { User } from '@/api/types';

interface UserContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  register: (data: Partial<User>) => Promise<void>;
  isAuthenticated: () => boolean;
  isAdmin: () => boolean; // Function to check admin privileges
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const getInitialUser = (): User | null => {
    const storedUser = localStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
  };

  const getInitialToken = (): string | null => {
    return localStorage.getItem('token');
  };

  const [user, setUser] = useState<User | null>(getInitialUser);
  const [token, setToken] = useState<string | null>(getInitialToken);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  const register = async (userData: Partial<User>) => {
    try {
      const basicAuth = btoa(`${userData.email}:${userData.password}`);
      const response = await client.api.post('/users', userData, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Basic ${basicAuth}`,
        },
      });
      console.log('Registration successful:', response.data);
      return response.data;
    } catch (error) {
      console.error('Registration failed:', error);
      throw new Error('Failed to register');
    }
  };

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token'); // Clear token
    navigate('/'); // Redirect after logout
  }, [navigate]);

  const login = async (email: string, password: string) => {
    try {
      setLoading(true);

      // Send login request with Basic Authorization header
      const response = await client.api.post<{
        token: string;
        user: User;
      }>(
        '/users/login',
        {},
        {
          headers: { Authorization: `Basic ${btoa(`${email}:${password}`)}` },
        },
      );

      // Extract token and user from response
      const { token, user } = response.data;

      // Save token and user in state
      setToken(token);
      setUser(user);

      // Persist token and user in localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));

      console.log('Login successful:', user);

      // Redirect to dashboard after login
      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
      throw new Error('Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  const isAuthenticated = () => !!user;

  const isAdmin = () => user?.isAdmin || false;

  useEffect(() => {
    // Check for an existing session and token when the app loads
    const fetchUser = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        setToken(storedToken);
        try {
          const response = await client.api.get<User>('/users/profile', {
            params: { userEmail: user?.email },
            headers: { Authorization: `${storedToken}` },
          });
          setUser(response.data);
        } catch (error) {
          console.error('User not authenticated:', error);
          logout();
        }
      }
      setLoading(false);
    };

    fetchUser();
  }, [logout, user?.email]);

  // Attach token to all requests
  useEffect(() => {
    if (token) {
      client.api.defaults.headers.common['Authorization'] = `${token}`;
    } else {
      delete client.api.defaults.headers.common['Authorization'];
    }
  }, [token]);

  return (
    <UserContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        logout,
        register,
        isAuthenticated,
        isAdmin,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
};
