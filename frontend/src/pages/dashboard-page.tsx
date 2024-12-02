import { Link } from 'react-router-dom';

import { menuItems } from '@/api/config';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';

export const DashboardPage = () => {
  const filteredCards = menuItems.filter((item) => item.description);

  return (
    <div className="lg:grid lg:min-h-[800px] lg:grid-cols-2">
      <div className="flex-1 px-6 py-8">
        <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-2">
          {filteredCards.map((card) => (
            <Card key={card.title}>
              <CardHeader>
                <CardTitle>{card.title}</CardTitle>
                <CardDescription>{card.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <Link
                  className="inline-flex items-center gap-2 text-sm font-medium text-black hover:underline dark:text-white"
                  to={card.link}
                >
                  {card.icon}
                  Administra {card.title}
                </Link>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
      <div className="bg-muted relative hidden lg:block lg:max-h-screen lg:overflow-hidden">
        <img
          src="/grupo10.png"
          alt="Grupo10"
          className="size-full rounded-xl object-cover object-center backdrop-brightness-200 dark:brightness-[0.2] dark:grayscale"
          style={{ maxHeight: '100vh' }}
        />
        <div className="absolute right-4 top-4 text-lg font-extrabold text-black dark:text-white">
          Backoffice
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
