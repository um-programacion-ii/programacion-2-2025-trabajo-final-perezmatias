import dayjs from 'dayjs/esm';

import { IVenta, NewVenta } from './venta.model';

export const sampleWithRequiredData: IVenta = {
  id: 21245,
  fechaVenta: dayjs('2025-12-11T05:52'),
  total: 8279.35,
  asientos: 'aha shark ah',
};

export const sampleWithPartialData: IVenta = {
  id: 12058,
  fechaVenta: dayjs('2025-12-11T20:44'),
  total: 25583.9,
  asientos: 'blah',
};

export const sampleWithFullData: IVenta = {
  id: 8723,
  fechaVenta: dayjs('2025-12-11T18:43'),
  total: 31981.07,
  asientos: 'misjudge duh',
};

export const sampleWithNewData: NewVenta = {
  fechaVenta: dayjs('2025-12-11T01:48'),
  total: 17775.18,
  asientos: 'ashamed',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
