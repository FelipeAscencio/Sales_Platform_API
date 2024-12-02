import { ArrowLeft, Moon, Sun } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast, Toaster } from 'sonner';

import { client } from '@/api/common/client';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';

export default function ForgotPasswordPage() {
  const [darkMode, setDarkMode] = useState(false);
  const [email, setEmail] = useState('');
  const [momName, setMomName] = useState('');
  const [petName, setPetName] = useState('');
  const [color, setColor] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const isDarkMode = localStorage.getItem('darkMode') === 'true';
    setDarkMode(isDarkMode);
    document.documentElement.classList.toggle('dark', isDarkMode);
  }, []);

  const toggleDarkMode = () => {
    const newDarkMode = !darkMode;
    setDarkMode(newDarkMode);
    localStorage.setItem('darkMode', newDarkMode.toString());
    document.documentElement.classList.toggle('dark', newDarkMode);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const payload = { email, momName, petName, color };
      const response = await client.api.post(
        '/users/recovery-password',
        payload,
      );

      if (response.status !== 200) {
        throw new Error('Password recovery failed');
      }

      const data = response.data;
      console.log('Password recovery successful:', data);
      setNewPassword(data.message.split(': ')[1]);
      setIsModalOpen(true);
    } catch (error) {
      console.error('Error during password recovery:', error);
      toast.error('Password recovery failed. Please try again.');
    }
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
    navigate('/login');
  };

  return (
    <div className="flex min-h-screen bg-gray-100 transition-colors duration-300 dark:bg-gray-900">
      <Toaster richColors />
      {/* Left Section */}
      <div className="relative hidden bg-sky-600 text-white lg:flex lg:w-1/2 dark:bg-sky-800">
        <div className="flex size-full flex-col">
          <div className="flex justify-between p-6">
            <div className="text-2xl font-bold">TIENDITA</div>
            <Link
              to="/"
              className="flex items-center rounded-md px-3 py-2 text-sm font-medium text-white/80 hover:bg-white/10 hover:text-white"
            >
              <ArrowLeft className="mr-2 size-4" />
              Back to website
            </Link>
          </div>
          <div className="relative flex flex-1 flex-col items-center justify-center p-12 text-center">
            <div
              className="absolute inset-0 bg-cover bg-center opacity-70 dark:opacity-50"
              style={{
                backgroundImage:
                  "url('/placeholder.svg?height=1080&width=1920')",
                backgroundPosition: 'center 65%',
              }}
            />
            <div className="relative z-10">
              <h2 className="mb-4 text-4xl font-bold">
                If you&apos;re grabbing more,
                <br />
                it&apos;s &apos;cause you&apos;re feeling it!
              </h2>
              <div className="mt-8 flex justify-center gap-2">
                <div className="size-2 rounded-full bg-white/30" />
                <div className="size-2 rounded-full bg-white/30" />
                <div className="size-2 rounded-full bg-white" />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Right Section */}
      <div className="flex flex-1 flex-col justify-center bg-white px-6 transition-colors duration-300 lg:px-12 dark:bg-gray-800">
        <div className="mx-auto w-full max-w-md space-y-6">
          <div className="flex items-center justify-between">
            <div className="space-y-2 text-gray-900 dark:text-white">
              <h1 className="text-4xl font-bold tracking-tight">
                Recover Password
              </h1>
              <p className="text-gray-600 dark:text-gray-400">
                Fill in the details to reset your password.
              </p>
            </div>
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
              <span className="sr-only">Toggle dark mode</span>
            </Button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="border-gray-300 bg-gray-100 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                required
              />
            </div>

            <div className="space-y-2">
              <Input
                type="text"
                placeholder="Your mom's name"
                value={momName}
                onChange={(e) => setMomName(e.target.value)}
                className="border-gray-300 bg-gray-100 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                required
              />
            </div>

            <div className="space-y-2">
              <Input
                type="text"
                placeholder="Your first pet's name"
                value={petName}
                onChange={(e) => setPetName(e.target.value)}
                className="border-gray-300 bg-gray-100 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                required
              />
            </div>

            <div className="space-y-2">
              <Input
                type="text"
                placeholder="Your favorite color"
                value={color}
                onChange={(e) => setColor(e.target.value)}
                className="border-gray-300 bg-gray-100 text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                required
              />
            </div>

            <Button
              type="submit"
              className="w-full bg-sky-600 text-white hover:bg-sky-500 dark:bg-sky-700 dark:hover:bg-sky-600"
            >
              Recover Password
            </Button>
          </form>

          <div className="text-center">
            <Link
              to="/login"
              className="text-sm text-sky-600 hover:text-sky-500 dark:text-sky-400 dark:hover:text-sky-300"
            >
              Back to Login
            </Link>
          </div>
        </div>
      </div>

      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Password Successfully Updated</DialogTitle>
            <DialogDescription>
              Your password has been reset. Please make sure to save your new
              password securely.
            </DialogDescription>
          </DialogHeader>
          <div className="bg-muted my-4 rounded-md p-4">
            <p className="text-center font-mono text-lg">
              Your new password is: <strong>{newPassword}</strong>
            </p>
          </div>
          <DialogFooter>
            <Button onClick={handleModalClose} className="w-full">
              OK
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
