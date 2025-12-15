import dayjs from 'dayjs/esm';

export interface IEvento {
  id: number;
  titulo?: string | null;
  descripcion?: string | null;
  fechaHora?: dayjs.Dayjs | null;
  ubicacion?: string | null;
  precio?: number | null;
  cantidadFilas?: number | null;
  cantidadColumnas?: number | null;
}

export type NewEvento = Omit<IEvento, 'id'> & { id: null };
