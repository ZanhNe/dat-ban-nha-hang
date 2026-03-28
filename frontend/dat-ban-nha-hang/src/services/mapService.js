import apiClient from './apiClient';

export const mapService = {
  getCoordinates: async (address) => {
    if (!address) throw new Error("Vui lòng nhập địa chỉ");
    const response = await apiClient.get('/geolocation/get-coordinates', {
      params: { address },
    });
    return response;
  },

  searchRestaurants: async (params) => {
    const response = await apiClient.get('/restaurants', {
      params: {
        origin: params.origin,
        cuisine: params.cuisine || undefined,
        radius: params.radius || 2,
        page: params.page || 0,
        limit: params.limit || 10,
      },
    });
    return response;
  },

  getRestaurantDetail: async (id, origin) => {
    if (!id || !origin) throw new Error("Thiếu ID nhà hàng hoặc tọa độ người dùng");
    const response = await apiClient.get(`/restaurants/${id}`, {
      params: { origin },
    });
    return response;
  },
};
