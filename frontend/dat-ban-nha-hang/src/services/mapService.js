import apiClient from './apiClient';
import { isApiResponse, unwrapData, unwrapMeta } from './apiShape';

export const mapService = {
  getCoordinates: async (address) => {
    if (!address) throw new Error("Vui lòng nhập địa chỉ");
    const body = await apiClient.get('/geolocation/get-coordinates', {
      params: { address },
    });

    const raw = unwrapData(body);
    if (raw && typeof raw === 'object' && typeof raw.latitude === 'number' && typeof raw.longitude === 'number') {
      return { status: 200, message: 'OK', data: raw, meta: null };
    }
    throw new Error("Response geolocation không đúng contract (expected {latitude, longitude}).");
  },

  searchRestaurants: async (params) => {
    const body = await apiClient.get('/customer/restaurants', {
      params: {
        origin: params.origin,
        cuisine: params.cuisine || undefined,
        radius: params.radius || 2,
        page: params.page || 0,
        limit: params.limit || 10,
      },
    });
    return isApiResponse(body) ? body : { status: 200, message: 'OK', data: body, meta: unwrapMeta(body) };
  },

  getRestaurantDetail: async (id, origin) => {
    if (!id || !origin) throw new Error("Thiếu ID nhà hàng hoặc tọa độ người dùng");
    const body = await apiClient.get(`/customer/restaurants/${id}`, {
      params: { origin },
    });
    return isApiResponse(body) ? body : { status: 200, message: 'OK', data: body, meta: unwrapMeta(body) };
  },
};
