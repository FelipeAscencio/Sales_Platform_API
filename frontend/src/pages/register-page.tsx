import { ArrowLeft, Eye, EyeOff, Moon, Sun } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import type { User } from '@/api/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useUser } from '@/hooks/user-context';

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState<Partial<User>>({});
  const [darkMode, setDarkMode] = useState(false);
  const [showSecurityQuestions, setShowSecurityQuestions] = useState(false);
  const { register } = useUser();
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

  const handleNextStep = () => {
    const { firstName, lastName, email, password } = formData;
    if (firstName && lastName && email && password) {
      setShowSecurityQuestions(true);
    } else {
      alert('Please complete all required fields before proceeding.');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await register(formData);
      console.log('Form data submitted:', formData);

      // Redirect to login page after submission
      navigate('/login');
    } catch (error) {
      console.error('Failed to create user:', error);
    }
  };

  return (
    <div className="flex min-h-screen bg-gray-100 transition-colors duration-300 dark:bg-gray-900">
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
                Create an account
              </h1>
              <p className="text-gray-600 dark:text-gray-400">
                Already have an account?{' '}
                <Link
                  to="/login"
                  className="text-sky-600 hover:text-sky-500 dark:text-sky-400 dark:hover:text-sky-300"
                >
                  Log in
                </Link>
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

          {/* Main Form */}
          <form onSubmit={(e) => e.preventDefault()} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                placeholder="First name"
                onChange={(e) =>
                  setFormData({ ...formData, firstName: e.target.value })
                }
              />
              <Input
                placeholder="Last name"
                onChange={(e) =>
                  setFormData({ ...formData, lastName: e.target.value })
                }
              />
            </div>
            <Input
              type="email"
              placeholder="Email"
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
            />
            <Input
              type="number"
              placeholder="Age"
              onChange={(e) =>
                setFormData({ ...formData, age: parseInt(e.target.value) || 0 })
              }
            />
            <Input
              type="text"
              placeholder="Gender"
              onChange={(e) =>
                setFormData({ ...formData, gender: e.target.value })
              }
            />
            <Input
              type="text"
              placeholder="Address"
              onChange={(e) =>
                setFormData({ ...formData, address: e.target.value })
              }
            />
            <Input
              type="text"
              placeholder="Photo URL"
              onChange={(e) =>
                setFormData({ ...formData, photo: e.target.value })
              }
            />
            <div className="relative">
              <Input
                type={showPassword ? 'text' : 'password'}
                placeholder="Password"
                onChange={(e) =>
                  setFormData({ ...formData, password: e.target.value })
                }
              />
              <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-0 top-0 h-full px-3"
              >
                {showPassword ? (
                  <EyeOff className="size-4" />
                ) : (
                  <Eye className="size-4" />
                )}
                <span className="sr-only">
                  {showPassword ? 'Hide password' : 'Show password'}
                </span>
              </Button>
            </div>

            {/* "Next" button disappears when security questions are displayed */}
            {!showSecurityQuestions && (
              <Button
                type="button"
                onClick={handleNextStep}
                className="w-full bg-sky-600 text-white hover:bg-sky-500 dark:bg-sky-700 dark:hover:bg-sky-600"
              >
                Next
              </Button>
            )}
          </form>

          {showSecurityQuestions && (
            <form onSubmit={handleSubmit} className="mt-6 space-y-4">
              <Input
                placeholder="Mother's name"
                onChange={(e) =>
                  setFormData({ ...formData, momName: e.target.value })
                }
              />
              <Input
                placeholder="First pet's name"
                onChange={(e) =>
                  setFormData({ ...formData, petName: e.target.value })
                }
              />
              <Input
                placeholder="Favorite color"
                onChange={(e) =>
                  setFormData({ ...formData, color: e.target.value })
                }
              />
              <Button
                type="submit"
                className="w-full bg-sky-600 text-white hover:bg-sky-500 dark:bg-sky-700 dark:hover:bg-sky-600"
              >
                Create account
              </Button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
