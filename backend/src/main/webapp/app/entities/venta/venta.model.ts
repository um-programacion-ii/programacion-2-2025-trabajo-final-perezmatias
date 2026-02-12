import dayjs from 'dayjs/esm';
import { IEvento } from 'app/entities/evento/evento.model';

export interface IVenta {
  id: number;
  fechaVenta?: dayjs.Dayjs | null;
  total?: number | null;
  asientos?: string | null;
  nombreComprador?: string | null;
  dniComprador?: string | null;
  evento?: IEvento | null;
}

export type NewVenta = Omit<IVenta, 'id'> & { id: null };
