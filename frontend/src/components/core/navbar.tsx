import { Link } from 'react-router-dom';

import { menuItems } from '@/api/config';

export const NavBar = () => {
  return (
    <nav className="grid gap-6 text-lg font-medium">
      {menuItems.map((link) => (
        <Link
          key={link.link}
          className="flex items-center gap-4 px-2.5 text-muted-foreground hover:text-foreground"
          to={link.link}
        >
          {link.icon}
          {link.title}
        </Link>
      ))}
    </nav>
  );
};

export default NavBar;
