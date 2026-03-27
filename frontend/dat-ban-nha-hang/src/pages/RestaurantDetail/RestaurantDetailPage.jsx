import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Info, Menu as MenuIcon, MessageSquare, Loader2 } from 'lucide-react';
import { useAtomValue } from 'jotai';

import { userAtom } from '../../store/authStore';
import { useRestaurantData } from '../../hooks/useRestaurantData';
import BookingBottomSheet from '../../components/BookingBottomSheet/BookingBottomSheet';

import DetailHero from './components/DetailHero';
import DetailOverview from './components/DetailOverview';
import DetailMenu from './components/DetailMenu';
import DetailReviews from './components/DetailReviews';

const RestaurantDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const user = useAtomValue(userAtom);

    const [activeTab, setActiveTab] = useState('overview');
    const [isBookingSheetOpen, setIsBookingSheetOpen] = useState(false);

    // Sử dụng Custom Hook để dọn dẹp logic fetching
    const { restaurant, menus, reviews, isLoading } = useRestaurantData(id, activeTab);

    if (isLoading) {
        return (
            <div className="h-screen w-full flex flex-col justify-center items-center bg-gray-50">
                <Loader2 className="animate-spin text-orange-500 mb-4" size={40} />
                <p className="text-gray-500 font-medium font-sans">Đang tải thông tin quán...</p>
            </div>
        )
    }

    if (!restaurant) return <div className="p-4 text-center text-red-500">Không tìm thấy nhà hàng!</div>;

    return (
        <div className="relative min-h-screen bg-gray-50 pb-24 font-sans">
            <DetailHero restaurant={restaurant} />

            {/* Sticky Tabs */}
            <div className="sticky top-0 z-30 bg-white shadow-sm border-b border-gray-100 px-4">
                <div className="flex justify-between max-w-sm mx-auto">
                    {[
                        { id: 'overview', icon: <Info size={20} className="mb-1" />, label: 'Tổng quan' },
                        { id: 'menu', icon: <MenuIcon size={20} className="mb-1" />, label: 'Thực đơn' },
                        { id: 'reviews', icon: <MessageSquare size={20} className="mb-1" />, label: 'Đánh giá' }
                    ].map(tab => (
                        <button
                            key={tab.id}
                            onClick={() => setActiveTab(tab.id)}
                            className={`flex flex-col items-center py-4 px-2 border-b-2 transition-colors ${activeTab === tab.id ? 'border-orange-500 text-orange-600 font-bold' : 'border-transparent text-gray-500 font-medium hover:text-gray-700'}`}
                        >
                            {tab.icon} {tab.label}
                        </button>
                    ))}
                </div>
            </div>

            {/* Content Area */}
            <div className="p-4 max-w-2xl mx-auto space-y-6 mt-4">
                {activeTab === 'overview' && <DetailOverview restaurant={restaurant} />}
                {activeTab === 'menu' && <DetailMenu menus={menus} />}
                {activeTab === 'reviews' && <DetailReviews reviews={reviews} user={user} />}
            </div>

            {/* Bottom Floating CTA */}
            <div className="fixed bottom-0 inset-x-0 p-4 bg-white/80 backdrop-blur-xl border-t border-gray-100 z-40 transform transition-transform">
                <div className="max-w-md mx-auto relative flex items-center justify-between gap-4">
                    <div className="text-left hidden sm:block">
                        <p className="text-xs text-gray-500">Phí giữ chỗ</p>
                        <p className="font-bold text-lg text-gray-900">{restaurant.restaurantBaseDeposit?.toLocaleString() || 0}đ</p>
                    </div>
                    <button
                        onClick={() => {
                            if (!user) navigate('/login');
                            else setIsBookingSheetOpen(true);
                        }}
                        className="flex-1 bg-orange-500 text-white font-bold text-lg py-4 rounded-2xl shadow-lg shadow-orange-500/30 hover:bg-orange-600 transition-colors transform active:scale-[0.98]"
                    >
                        Đặt bàn ngay
                    </button>
                </div>
            </div>

            <BookingBottomSheet
                isOpen={isBookingSheetOpen}
                onClose={() => setIsBookingSheetOpen(false)}
                restaurant={restaurant}
            />
        </div>
    );
};

export default RestaurantDetailPage;
