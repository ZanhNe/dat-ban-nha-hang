import { atom } from 'jotai';

export const originAtom = atom(null);
export const addressAtom = atom('');

export const radiusAtom = atom(2);
export const cuisineAtom = atom('');

export const restaurantsAtom = atom([]);
export const totalPagesAtom = atom(1);
export const totalItemsAtom = atom(0);
export const pageAtom = atom(0);
export const limitAtom = atom(10);
export const loadingAtom = atom(false);
export const searchTriggerAtom = atom(0);

export const selectedRestaurantAtom = atom(null);
export const focusModeAtom = atom(false);
export const restaurantDetailAtom = atom(null);
