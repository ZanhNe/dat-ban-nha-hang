import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useNavigate } from 'react-router-dom';
import { formatApiError, unwrapData } from '../../services/apiShape';

export const usePos = (sessionId) => {
    const navigate = useNavigate();

    const [menuGroups, setMenuGroups] = useState([]);
    const [cart, setCart] = useState([]); // [{ cartItemId, food, quantity, options: [] }]
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    // Lấy Menu
    const fetchMenu = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await waiterService.getMenuForPos();
            const data = unwrapData(res);
            if (res.status === 200) {
                const groups = (data?.foodGroups || []).map((group) => ({
                ...group,
                foods: (group.foods || []).map((food) => ({
                    ...food,
                    foodId: food.foodDescriptionId,
                    optionGroups: food.optionGroups || [],
                })),
            }));
                setMenuGroups(groups);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải menu POS.').displayMessage);
        }
        setIsLoading(false);
    }, []);

    useEffect(() => { fetchMenu(); }, [fetchMenu]);

    // Thêm vào giỏ hàng
    const addToCart = (food, selectedOptions = []) => {
        const sortedOptionIds = (selectedOptions || []).map((opt) => opt.optionId).sort((a, b) => a - b);
        const identityKey = `${food.foodId}::${sortedOptionIds.join(',')}`;

        setCart((prev) => {
            const existedIndex = prev.findIndex((item) => {
                const itemOptionIds = (item.options || []).map((opt) => opt.optionId).sort((a, b) => a - b);
                const itemKey = `${item.food.foodId}::${itemOptionIds.join(',')}`;
                return itemKey === identityKey;
            });

            if (existedIndex >= 0) {
                return prev.map((item, index) => index === existedIndex
                    ? { ...item, quantity: item.quantity + 1 }
                    : item);
            }

            const cartItemId = Date.now() + Math.floor(Math.random() * 1000);
            return [...prev, { cartItemId, food, quantity: 1, options: selectedOptions }];
        });
    };

    // Tăng/Giảm số lượng
    const updateQuantity = (cartItemId, delta) => {
        setCart(prev => prev.map(item => {
            if (item.cartItemId === cartItemId) {
                const newQuantity = item.quantity + delta;
                return newQuantity > 0 ? { ...item, quantity: newQuantity } : item;
            }
            return item;
        }));
    };

    // Xóa khỏi giỏ
    const removeFromCart = (cartItemId) => {
        setCart(prev => prev.filter(item => item.cartItemId !== cartItemId));
    };

    // TÍNH TỔNG TIỀN
    const calculateTotal = () => {
        return cart.reduce((total, item) => {
            const optionsPrice = item.options.reduce((sum, opt) => sum + (opt.price || 0), 0);
            return total + (item.food.price + optionsPrice) * item.quantity;
        }, 0);
    };

    // BẤM NÚT GỬI BẾP (Quy trình 2 bước)
    const handleSendToKitchen = async () => {
        if (cart.length === 0) {
            setError('Giỏ hàng đang trống.');
            return;
        }
        setIsSubmitting(true);
        setError('');

        try {
            // Bước 1: Tạo Order rỗng (API 9.2)
            const orderRes = await waiterService.createOrder(sessionId);
            if (orderRes.status !== 201) throw new Error("Không thể khởi tạo Order");

            const orderId = unwrapData(orderRes)?.orderId;
            if (!orderId) throw new Error("Không lấy được orderId");

            // Bước 2: Format dữ liệu và gửi bếp (API 9.3)
            const payload = {
                items: cart.map(item => ({
                    foodDescriptionId: item.food.foodId,
                    quantity: item.quantity,
                    optionIds: item.options.map(opt => opt.optionId)
                }))
            };

            const submitRes = await waiterService.submitOrder(orderId, payload);
            if (submitRes.status === 200) {
                setCart([]); // Xóa giỏ hàng
                navigate('/waiter'); // Quay về màn hình quản lý bàn
            } else {
                setError('Không thể gửi bếp.');
            }
        } catch (error) {
            setError(formatApiError(error, 'Đã xảy ra lỗi khi gửi Order xuống bếp!').displayMessage);
        } finally {
            setIsSubmitting(false);
        }
    };

    return {
        menuGroups, cart, isLoading, isSubmitting, error,
        addToCart, updateQuantity, removeFromCart, calculateTotal, handleSendToKitchen
    };
};