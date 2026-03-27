import { useState, useEffect } from 'react';
import { restaurantService } from '../services/restaurantService';

export const useRestaurantData = (id, activeTab) => {
    const [restaurant, setRestaurant] = useState(null);
    const [menus, setMenus] = useState([]);
    const [reviews, setReviews] = useState({ meta: {}, list: [] });
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchInitialData = async () => {
            setIsLoading(true);
            try {
                const resData = await restaurantService.getRestaurantDetail(id);
                setRestaurant(resData);
            } catch (error) {
                console.error("Lỗi lấy chi tiết nhà hàng", error);
            } finally {
                setIsLoading(false);
            }
        };
        if (id) fetchInitialData();
    }, [id]);

    useEffect(() => {
        if (activeTab === 'menu' && menus.length === 0) {
            restaurantService.getRestaurantMenu(id).then(data => {
                if(data.restaurantMenus) setMenus(data.restaurantMenus);
            });
        }
        if (activeTab === 'reviews' && reviews.list.length === 0) {
            restaurantService.getRestaurantReviews(id).then(data => {
                 setReviews({ meta: data.meta, list: data.data });
            });
        }
    }, [activeTab, id, menus.length, reviews.list.length]);

    return { restaurant, menus, reviews, isLoading };
};
