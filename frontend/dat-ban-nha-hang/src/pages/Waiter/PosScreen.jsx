import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { usePos } from '../../hooks/waiter/usePos';
import './PosScreen.css';

function PosScreen() {
    const { sessionId } = useParams(); // Lấy ID phiên bàn từ URL
    const navigate = useNavigate();
    const {
        menuGroups, cart, isLoading, isSubmitting,
        error,
        addToCart, updateQuantity, removeFromCart, calculateTotal, handleSendToKitchen
    } = usePos(sessionId);

    const [activeGroupId, setActiveGroupId] = useState(null);
    const [optionModal, setOptionModal] = useState({ isOpen: false, food: null, selectedOpts: [] });

    // Khởi tạo active tab mặc định
    React.useEffect(() => {
        if (menuGroups.length > 0 && !activeGroupId) {
            setActiveGroupId(menuGroups[0].groupId);
        }
    }, [menuGroups, activeGroupId]);

    const handleFoodClick = (food) => {
        if (food.optionGroups && food.optionGroups.length > 0) {
            // Có tùy chọn -> Mở popup
            setOptionModal({ isOpen: true, food, selectedOpts: [] });
        } else {
            // Không có tùy chọn -> Thêm luôn vào giỏ
            addToCart(food, []);
        }
    };

    const toggleOption = (option) => {
        const isSelected = optionModal.selectedOpts.find(o => o.optionId === option.optionId);
        if (isSelected) {
            setOptionModal(prev => ({ ...prev, selectedOpts: prev.selectedOpts.filter(o => o.optionId !== option.optionId) }));
        } else {
            setOptionModal(prev => ({ ...prev, selectedOpts: [...prev.selectedOpts, option] }));
        }
    };

    const confirmOptionsAndAddToCart = () => {
        addToCart(optionModal.food, optionModal.selectedOpts);
        setOptionModal({ isOpen: false, food: null, selectedOpts: [] });
    };

    if (isLoading) return <div className="pos-loading">Đang tải Menu...</div>;

    const activeGroup = menuGroups.find(g => g.groupId === activeGroupId);

    return (
        <div className="pos-container">
            {/* KHU VỰC TRÁI: MENU */}
            <div className="pos-menu-section">
                <div className="pos-header">
                    <button className="btn-back" onClick={() => navigate('/waiter')}> Quay lại</button>
                    <h2>Ghi món (Phiên #{sessionId})</h2>
                </div>
                {error && <div className="pos-error" style={{ marginBottom: 10, color: '#b91c1c' }}>{error}</div>}

                {/* Tabs Nhóm món */}
                <div className="menu-tabs">
                    {menuGroups.map(group => (
                        <button
                            key={group.groupId}
                            className={`tab-btn ${activeGroupId === group.groupId ? 'active' : ''}`}
                            onClick={() => setActiveGroupId(group.groupId)}
                        >
                            {group.name}
                        </button>
                    ))}
                </div>

                {/* Danh sách món ăn */}
                <div className="food-grid">
                    {activeGroup?.foods.map(food => (
                        <div key={food.foodId} className="food-card" onClick={() => handleFoodClick(food)}>
                            <div className="food-img-placeholder">🍲</div>
                            <h4>{food.name}</h4>
                            <p className="price">{food.price.toLocaleString('vi-VN')} ₫</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* KHU VỰC PHẢI: GIỎ HÀNG (CART) */}
            <div className="pos-cart-section">
                <h3 className="cart-title">Giỏ hàng ({cart.length})</h3>

                <div className="cart-items">
                    {cart.length === 0 && <p className="empty-cart">Chưa có món nào được chọn.</p>}
                    {cart.map((item) => (
                        <div key={item.cartItemId} className="cart-item">
                            <div className="item-info">
                                <strong>{item.food.name}</strong>
                                {item.options.length > 0 && (
                                    <div className="item-options">
                                        {item.options.map(o => o.name).join(', ')}
                                    </div>
                                )}
                                <div className="item-price">
                                    {((item.food.price + item.options.reduce((s, o) => s + (o.price || 0), 0)) * item.quantity).toLocaleString('vi-VN')} ₫
                                </div>
                            </div>

                            <div className="item-controls">
                                <div className="qty-controls">
                                    <button onClick={() => updateQuantity(item.cartItemId, -1)}>-</button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.cartItemId, 1)}>+</button>
                                </div>
                                <button className="btn-remove" onClick={() => removeFromCart(item.cartItemId)}>🗑️</button>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="cart-footer">
                    <div className="total-row">
                        <span>Tổng tạm tính:</span>
                        <strong className="total-price">{calculateTotal().toLocaleString('vi-VN')} ₫</strong>
                    </div>
                    <button
                        className="btn-submit-order"
                        disabled={cart.length === 0 || isSubmitting}
                        onClick={handleSendToKitchen}
                    >
                        {isSubmitting ? "ĐANG GỬI..." : " XÁC NHẬN & GỬI BẾP"}
                    </button>
                </div>
            </div>

            {/* MODAL TÙY CHỌN (OPTIONS) */}
            {optionModal.isOpen && (
                <div className="modal-overlay">
                    <div className="modal-content pos-modal">
                        <h3>Tùy chọn: {optionModal.food.name}</h3>
                        <div className="options-list">
                            {(optionModal.food.optionGroups || []).map((group) => (
                                <div key={group.optionGroupId} style={{ marginBottom: 10 }}>
                                    <div style={{ fontWeight: 700, marginBottom: 6 }}>{group.name}</div>
                                    {(group.options || []).map((opt) => {
                                        const isSelected = optionModal.selectedOpts.find(o => o.optionId === opt.optionId);
                                        return (
                                            <div
                                                key={opt.optionId}
                                                className={`option-item ${isSelected ? 'selected' : ''}`}
                                                onClick={() => toggleOption(opt)}
                                            >
                                                <span>{opt.name}</span>
                                                {opt.price > 0 && <span>+{opt.price.toLocaleString('vi-VN')} ₫</span>}
                                            </div>
                                        );
                                    })}
                                </div>
                            ))}
                        </div>
                        <div className="modal-actions">
                            <button className="btn-save" onClick={confirmOptionsAndAddToCart}>Xong & Thêm vào giỏ</button>
                            <button className="btn-cancel" onClick={() => setOptionModal({ isOpen: false, food: null, selectedOpts: [] })}>Hủy</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default PosScreen;