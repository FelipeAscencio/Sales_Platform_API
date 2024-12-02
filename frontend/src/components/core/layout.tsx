import type { ReactNode } from 'react';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { useUser } from '@/hooks/user-context';

import { Header } from './header';
import { Sidebar } from './sidebar';

type LayoutProps = {
  children: ReactNode;
};

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout, isAdmin, loading } = useUser();
  const navigate = useNavigate();

  useEffect(() => {
    if (!loading) {
      if (!isAdmin()) {
        navigate('/');
      }
    }
  }, [isAdmin, navigate, loading]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="bg-muted/40 flex min-h-screen w-full">
      <Sidebar />
      <div className="flex flex-1 flex-col">
        <Header user={user} onLogout={logout} />
        <main className="flex-1 pl-20 pr-6 pt-4">{children}</main>
      </div>
    </div>
  );
};

export default Layout;
