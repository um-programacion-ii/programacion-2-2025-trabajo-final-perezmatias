import dayjs from 'dayjs/esm';

import { IEvento, NewEvento } from './evento.model';

export const sampleWithRequiredData: IEvento = {
  id: 21654,
  titulo: 'inwardly chromakey',
  fechaHora: dayjs('2025-12-11T14:49'),
  precio: 9541.47,
};

export const sampleWithPartialData: IEvento = {
  id: 27897,
  titulo: 'huzzah',
  descripcion: 'anenst elegant knife',
  fechaHora: dayjs('2025-12-11T17:32'),
  ubicacion: 'eventually quarrelsomely',
  precio: 25084.61,
};

export const sampleWithFullData: IEvento = {
  id: 26545,
  titulo: 'outlaw upon',
  descripcion: 'duh labourer pleasure',
  fechaHora: dayjs('2025-12-11T21:51'),
  ubicacion: 'outrageous besides',
  precio: 22747.6,
};

export const sampleWithNewData: NewEvento = {
  titulo: 'wherever nor',
  fechaHora: dayjs('2025-12-11T06:43'),
  precio: 12901.34,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
