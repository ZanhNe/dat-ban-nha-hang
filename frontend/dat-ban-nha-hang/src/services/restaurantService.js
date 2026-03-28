import axios from 'axios';

// Đổi cờ này thành 'false' để call api thật xuống localhost:8080
export const USE_MOCK = false;
const API_BASE_URL = 'http://localhost:8080/api/v1';

// ======================================
// DỮ LIỆU MOCK (Giả lập)
// ======================================
const mockRestaurantData = {
    restaurantId: 101,
    restaurantName: "Haidilao - Hùng Vương Plaza",
    restaurantImage: "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80",
    restaurantLogo: "https://cdn.haidilao.com/logo.png",
    restaurantDescription: "Thương hiệu lẩu nổi tiếng thế giới với dịch vụ chăm sóc khách hàng hàng đầu. Nước lẩu trứ danh, nguyên liệu tươi ngon.",
    restaurantAddress: "126 Hùng Vương, Quận 5, TP.HCM",
    restaurantCuisines: ["Lẩu", "Món Trung", "Buffet gọi món"],
    restaurantAvgRating: 4.8,
    restaurantTotalReviews: 1250,
    restaurantDepositPolicy: "Fixed",
    restaurantBaseDeposit: 200000,
    restaurantOperationTimes: [
        { day: "Hàng ngày", open: "09:00", close: "23:00" }
    ]
};

const mockMenuData = {
    restaurantId: 101,
    restaurantMenus: [
        {
            menuName: "Menu Chính",
            restaurantMenu: [
                {
                    groupName: "Món nhúng đặc sắc",
                    items: [
                        { itemId: 1, itemName: "Bò Mỹ cuộn", itemDescription: "Thịt bò nhập khẩu ngậy mềm", itemPrice: 150000 },
                        { itemId: 2, itemName: "Thịt dê cuốn", itemDescription: "Dê khử mùi chuẩn vị Mông Cổ", itemPrice: 160000 }
                    ]
                },
                {
                    groupName: "Hải sản tươi sống",
                    items: [
                        { itemId: 3, itemName: "Mực nang chóp", itemDescription: "Giòn sật mọng nước", itemPrice: 120000 },
                        { itemId: 4, itemName: "Tôm sú xẻ lưng", itemDescription: "Tôm tươi sống", itemPrice: 180000 }
                    ]
                }
            ]
        }
    ]
};

const mockReviewsData = {
    status: 200,
    data: [
        { reviewId: 1, user: { fullName: "Nguyễn Văn A" }, rating: 5, content: "Nước lẩu rất ngon, phục vụ cực kỳ chu đáo, sẽ quay lại!", createdAt: "2026-03-24T10:00:00" },
        { reviewId: 2, user: { fullName: "Trần Thị B" }, rating: 4, content: "Đồ ăn tươi nhưng lúc đông khách đợi bàn khá lâu (dù đã đặt trước).", createdAt: "2026-03-22T19:30:00" },
        { reviewId: 3, user: { fullName: "Lê Văn C" }, rating: 5, content: "Không gian sạch sẽ, sang trọng.", createdAt: "2026-03-20T12:00:00" },
    ],
    meta: {
        totalItems: 1250,
        avgRating: 4.8
    }
};

const mockAreaTables = [
    {
        areaName: "Trong nhà",
        tables: [
            { tableId: 1, label: "Bàn 01", capacity: 4, isAvailable: true },
            { tableId: 2, label: "Bàn 02", capacity: 2, isAvailable: false, reason: "Đã có khách" },
            { tableId: 3, label: "Bàn 03", capacity: 4, isAvailable: false, reason: "Đã đặt" },
            { tableId: 4, label: "Bàn 04", capacity: 6, isAvailable: true },
        ]
    },
    {
        areaName: "Sân thượng",
        tables: [
            { tableId: 5, label: "Tầng thượng 1", capacity: 4, isAvailable: true },
            { tableId: 6, label: "Tầng thượng 2", capacity: 8, isAvailable: true },
        ]
    }
];


export const restaurantService = {
    getRestaurantDetail: async (id) => {
        if (USE_MOCK) return Promise.resolve(mockRestaurantData);
        const res = await apiClient.get(`/restaurants/${id}`);
        return res;
    },

    getRestaurantMenu: async (id) => {
        if (USE_MOCK) return Promise.resolve(mockMenuData);
        const res = await apiClient.get(`/restaurants/${id}/menu`);
        return res;
    },

    getRestaurantReviews: async (id, limit = 10, cursor = null) => {
        if (USE_MOCK) return Promise.resolve(mockReviewsData);
        const params = new URLSearchParams();
        if (limit) params.append('limit', limit);
        if (cursor) params.append('cursor', cursor);
        const res = await apiClient.get(`/restaurants/${id}/reviews`, { params });
        return res;
    },

    getAvailableTables: async (id, date, time, guests) => {
        if (USE_MOCK) {
            return new Promise(resolve => setTimeout(() => resolve(mockAreaTables), 800));
        }

        const params = new URLSearchParams();
        params.append('date', date);
        params.append('time', time);
        params.append('guests', guests);

        const res = await apiClient.get(`/restaurants/${id}/tables`, { params });
        return res;
    }
};
