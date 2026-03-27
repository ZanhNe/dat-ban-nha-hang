import apiClient from './apiClient';

export const mapService = {
  /**
   * 1. Lấy tọa độ từ địa chỉ (GET /api/v1/restaurants/get-coordinates)
   * @param {string} address - Chuỗi địa chỉ nhập vào
   */
  getCoordinates: async (address) => {
    if (!address) throw new Error("Vui lòng nhập địa chỉ");
    const response = await apiClient.get('/geolocation/get-coordinates', {
      params: { address },
    });
    return response.data; // dựa theo schema: response.data.geometry.location
  },

  /**
   * 2. Tìm kiếm nhà hàng (GET /api/v1/restaurants)
   * @param {Object} params - Các query params
   * @param {string} params.origin - Tọa độ của khách hàng "lat,lng" (BB bắt buộc)
   * @param {string} [params.cuisine] - Loại hình ẩm thực
   * @param {number} [params.radius] - Bán kính km (mặc định 2)
   * @param {number} [params.page] - Số trang
   * @param {number} [params.limit] - Số phần tử/trang
   */
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

  /**
   * 3. Lấy chi tiết nhà hàng kèm đường đi (GET /api/v1/restaurants/{id})
   * @param {number} id - ID nhà hàng
   * @param {string} origin - "lat,lng" của người dùng
   */
  getRestaurantDetail: async (id, origin) => {
    if (!id || !origin) throw new Error("Thiếu ID nhà hàng hoặc tọa độ người dùng");
    const response = await apiClient.get(`/restaurants/${id}`, {
      params: { origin },
    });
    return response.data;
  },
};
