import React from 'react';
import { Navigate } from 'react-router-dom';

import Layout from '@/components/core/layout';
import { useUser } from '@/hooks/user-context';

const AdminProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { loading, isAuthenticated, isAdmin } = useUser();

  if (loading) {
    return (
      <Layout>
        <div className="flex h-screen items-center justify-center">
          <p>Loading...</p>
        </div>
      </Layout>
    );
  }

  if (!isAuthenticated() || !isAdmin()) {
    return <Navigate to="/not-authorized" replace />;
  }

  return <Layout>{children}</Layout>;
};

export default AdminProtectedRoute;
