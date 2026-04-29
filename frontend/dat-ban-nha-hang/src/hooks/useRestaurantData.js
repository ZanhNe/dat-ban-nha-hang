import { useState, useEffect } from 'react';
import { restaurantService } from '../services/restaurantService';
import { formatApiError, unwrapData, unwrapMeta } from '../services/apiShape';

export const useRestaurantData = (id, activeTab) => {
    const [restaurant, setRestaurant] = useState(null);
    const [menus, setMenus] = useState([]);
    const [reviews, setReviews] = useState({ meta: {}, list: [] });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchInitialData = async () => {
            setIsLoading(true);
            setError('');
            try {
                const res = await restaurantService.getRestaurantDetail(id);
                const resData = unwrapData(res);
                setRestaurant(resData);
            } catch (error) {
                setError(formatApiError(error, 'Không thể tải chi tiết nhà hàng.').displayMessage);
            } finally {
                setIsLoading(false);
            }
        };
        if (id) fetchInitialData();
    }, [id]);

    useEffect(() => {
        if (activeTab === 'menu' && menus.length === 0) {
            restaurantService.getRestaurantMenu(id)
                .then(res => {
                    const data = unwrapData(res);
                    if (data?.restaurantMenus) setMenus(data.restaurantMenus);
                    else setMenus([]);
                })
                .catch(error => {
                    setError(formatApiError(error, 'Không thể tải menu nhà hàng.').displayMessage);
                });
        }
        if (activeTab === 'reviews' && reviews.list.length === 0) {
            restaurantService.getRestaurantReviews(id)
                .then(res => {
                    setReviews({ meta: unwrapMeta(res) || {}, list: unwrapData(res) || [] });
                })
                .catch(error => {
                    setError(formatApiError(error, 'Không thể tải đánh giá nhà hàng.').displayMessage);
                });
        }
    }, [activeTab, id, menus.length, reviews.list.length]);

    console.log(restaurant);

    return { restaurant, menus, reviews, isLoading, error };
};
