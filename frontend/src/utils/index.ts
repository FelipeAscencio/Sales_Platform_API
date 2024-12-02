import moment from 'moment';

export const formatDate = (date: string) => {
  return moment.utc(date).format('DD-MM-YYYY');
};
