// src/pages/users/users.ts
import { client } from '@/api/common/client';
import type { User } from '@/api/types';

export const fetchUsers = async (): Promise<User[]> => {
  const response = await client.api.get<User[]>('/users');
  return response.data;
};

export const saveUser = async (user: User, token: string): Promise<unknown> => {
  if (user.email) {
    const response = await client.api.put<User>(`/users`, user, {
      headers: { Authorization: token },
    });
    return response.data;
  } else {
    const response = await client.api.post<User>('/users', user);
    return response.data;
  }
};

export const deleteUser = async (
  userId: number,
  token: string,
): Promise<void> => {
  await client.api.delete(`/users/${userId}`, {
    headers: { Authorization: token },
  });
};
