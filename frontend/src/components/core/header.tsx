import { LogOut, User } from 'lucide-react';
import { Link } from 'react-router-dom';

import type { User as UserType } from '@/api/types'; // Import User type if needed
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';

import { PanelLeftIcon } from './icons-core';
import { ModeToggle } from './mode-toggle';
import { NavBar } from './navbar';

type HeaderProps = {
  user: UserType | null; // User from context
  onLogout: () => void; // Logout function from context
};

export const Header: React.FC<HeaderProps> = ({ user, onLogout }) => {
  return (
    <header className="bg-background flex h-14 w-full items-center gap-4 border-b px-4 sm:px-6">
      <Sheet>
        <SheetTrigger asChild>
          <Button className="sm:hidden" size="icon" variant="outline">
            <PanelLeftIcon className="size-5" />
            <span className="sr-only">Toggle Menu</span>
          </Button>
        </SheetTrigger>
        <SheetContent className="sm:max-w-xs" side="left">
          <NavBar />
        </SheetContent>
      </Sheet>
      <div className="ml-auto flex items-center gap-4">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button className="relative size-8 rounded-full" variant="outline">
              <Avatar>
                <AvatarImage
                  alt={`${user?.firstName} ${user?.lastName}`}
                  src={user?.photo || '/placeholder.svg'}
                />
                <AvatarFallback>
                  {user ? user.firstName[0] : 'S'}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" alignOffset={-4} className="w-56">
            <DropdownMenuLabel>
              <div className="flex items-center space-x-2">
                <Avatar>
                  <AvatarImage
                    alt={`${user?.firstName} ${user?.lastName}`}
                    src={user?.photo || '/placeholder.svg'}
                  />
                  <AvatarFallback>
                    {user ? user.firstName[0] : 'S'}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <p className="font-medium leading-none">
                    {user ? `${user.firstName} ${user.lastName}` : 'Guest User'}
                  </p>
                  <p className="text-muted-foreground text-xs leading-none">
                    {user?.email || 'guest@example.com'}
                  </p>
                </div>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem asChild>
              <Link to="/profile">
                <User className="mr-2 size-4" />
                Profile
              </Link>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={onLogout}>
              <LogOut className="mr-2 size-4" />
              Logout
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
        <ModeToggle />
      </div>
    </header>
  );
};

export default Header;
