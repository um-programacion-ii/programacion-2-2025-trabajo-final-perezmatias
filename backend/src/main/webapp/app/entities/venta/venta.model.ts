import dayjs from 'dayjs/esm';
import { IEvento } from 'app/entities/evento/evento.model';
import { IUser } from 'app/entities/user/user.model';

export interface IVenta {
  id: number;
  fechaVenta?: dayjs.Dayjs | null;
  total?: number | null;
  asientos?: string | null;
  evento?: IEvento | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewVenta = Omit<IVenta, 'id'> & { id: null };
