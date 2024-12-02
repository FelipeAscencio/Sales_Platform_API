import axios from 'axios';

const apiService = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}`,
});

apiService.defaults.headers.common['projects-Type'] = 'application/json';
apiService.defaults.headers.common.Accept = 'application/json';

export const client = {
  api: apiService,
};
