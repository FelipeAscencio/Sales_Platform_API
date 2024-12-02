import { Link, useLocation } from 'react-router-dom';

import { menuItems } from '@/api/config';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';

export const Sidebar = () => {
  const location = useLocation();

  return (
    <aside className="fixed inset-y-0 left-0 z-10 hidden w-14 flex-col border-r bg-background sm:flex">
      <nav className="flex flex-col items-center gap-4 px-2 sm:py-5">
        <TooltipProvider>
          {menuItems.map((item) => (
            <Tooltip key={item.link}>
              <TooltipTrigger asChild>
                <Link
                  className={`flex size-9 items-center justify-center rounded-lg transition-colors hover:text-foreground md:size-8 ${
                    location.pathname === item.link
                      ? 'font-bold text-foreground'
                      : 'text-muted-foreground'
                  }`}
                  to={item.link}
                >
                  {item.icon}
                  <span className="sr-only">{item.title}</span>
                </Link>
              </TooltipTrigger>
              <TooltipContent side="right">{item.title}</TooltipContent>
            </Tooltip>
          ))}
        </TooltipProvider>
      </nav>
    </aside>
  );
};

export default Sidebar;
