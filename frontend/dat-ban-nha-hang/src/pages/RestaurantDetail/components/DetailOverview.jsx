import React from 'react';
import { MapPin, Clock } from 'lucide-react';

const DetailOverview = ({ restaurant }) => {
    return (
        <div className="space-y-6 animate-fade-in">
            <div className="bg-white p-5 rounded-3xl shadow-sm border border-gray-100 flex flex-col gap-4">
                <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-full bg-orange-50 flex items-center justify-center flex-shrink-0">
                        <MapPin size={20} className="text-orange-500"/>
                    </div>
                    <div>
                        <h3 className="font-bold text-gray-900 mb-1">Vị trí</h3>
                        <p className="text-sm text-gray-600 leading-relaxed">{restaurant.restaurantAddress}</p>
                    </div>
                </div>
                <div className="h-px bg-gray-100 ml-14"></div>
                <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center flex-shrink-0">
                        <Clock size={20} className="text-blue-500"/>
                    </div>
                    <div>
                        <h3 className="font-bold text-gray-900 mb-1">Giờ hoạt động</h3>
                        <p className="text-sm text-gray-600">
                            Mở cửa: {restaurant.restaurantOperationTimes?.[0]?.open} - {restaurant.restaurantOperationTimes?.[0]?.close}
                        </p>
                    </div>
                </div>
            </div>

            <div>
                <h2 className="text-xl font-bold text-gray-900 mb-3">Giới thiệu</h2>
                <p className="text-gray-600 leading-relaxed text-sm">
                    {restaurant.restaurantDescription}
                </p>
            </div>
        </div>
    );
};

export default DetailOverview;
