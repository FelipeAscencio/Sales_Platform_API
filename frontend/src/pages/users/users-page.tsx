// src/pages/users/users-page.tsx
import React, { useEffect, useState } from 'react';
import { Toaster } from 'sonner';

import type { User } from '@/api/types';
import { useUser } from '@/hooks/user-context';

import { fetchUsers, saveUser } from './users';
import UserForm from './users-form';
import UsersTable from './users-table';

const UsersPage: React.FC = () => {
  const { token } = useUser();
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showForm, setShowForm] = useState<boolean>(false);

  useEffect(() => {
    const loadUsers = async () => {
      const data = await fetchUsers();
      setUsers(data);
    };
    loadUsers();
  }, []);

  const handleAddUser = () => {
    setSelectedUser(null);
    setShowForm(true);
  };

  const handleEditUser = (user: User) => {
    setSelectedUser(user);
    setShowForm(true);
  };

  const handleSaveUser = async (user: User, token: string) => {
    await saveUser(user, token);
    const updatedUsers = await fetchUsers();
    setUsers(updatedUsers);

    setShowForm(false);
  };

  const handleCancel = () => {
    setShowForm(false);
  };

  return (
    <main className="flex flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0">
      <Toaster richColors closeButton position="bottom-right" />
      <div className="grow">
        <UsersTable
          users={users}
          onEdit={handleEditUser}
          onAddUser={handleAddUser}
        />
      </div>
      {showForm && (
        <div className="w-96">
          <UserForm
            user={selectedUser}
            onSave={(user) => token && handleSaveUser(user, token)}
            onCancel={handleCancel}
          />
        </div>
      )}
    </main>
  );
};

export default UsersPage;
