import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { useNavigate } from 'react-router-dom';

export const usePos = (sessionId) => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId || 101;
    const navigate = useNavigate();

    const [menuGroups, setMenuGroups] = useState([]);
    const [cart, setCart] = useState([]); // [{ cartItemId, food, quantity, options: [] }]
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Lấy Menu
    const fetchMenu = useCallback(async () => {
        setIsLoading(true);
        const res = await waiterService.getMenuForPos(restaurantId);
        if (res.status === 200) setMenuGroups(res.data);
        setIsLoading(false);
    }, [restaurantId]);

    useEffect(() => { fetchMenu(); }, [fetchMenu]);

    // Thêm vào giỏ hàng
    const addToCart = (food, selectedOptions = []) => {
        const cartItemId = Date.now(); // Tạo ID tạm thời cho item trong giỏ
        setCart(prev => [...prev, { cartItemId, food, quantity: 1, options: selectedOptions }]);
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
        if (cart.length === 0) return alert("Giỏ hàng đang trống!");
        setIsSubmitting(true);

        try {
            // Bước 1: Tạo Order rỗng (API 9.2)
            const orderRes = await waiterService.createOrder(sessionId);
            if (orderRes.status !== 201) throw new Error("Không thể khởi tạo Order");

            const orderId = orderRes.data.orderId;

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
                alert(" " + submitRes.message);
                setCart([]); // Xóa giỏ hàng
                navigate('/waiter'); // Quay về màn hình quản lý bàn
            } else {
                alert(" Lỗi: " + submitRes.message);
            }
        } catch (error) {
            console.error(error);
            alert("Đã xảy ra lỗi khi gửi Order xuống bếp!");
        } finally {
            setIsSubmitting(false);
        }
    };

    return {
        menuGroups, cart, isLoading, isSubmitting,
        addToCart, updateQuantity, removeFromCart, calculateTotal, handleSendToKitchen
    };
};