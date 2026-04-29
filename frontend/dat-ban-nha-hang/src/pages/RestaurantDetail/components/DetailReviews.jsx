import React from 'react';
import { Star, Loader2, Edit3 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { formatDateTime } from '../../../utils/dateTime';

const DetailReviews = ({ reviews, user, restaurant }) => {
    const navigate = useNavigate();
    const restaurantId = restaurant?.restaurantId;
    const avgRating = Number(restaurant?.restaurantAvgRating ?? 0);
    const totalReviews = Number(restaurant?.restaurantTotalReviews ?? 0);

    return (
        <div className="animate-fade-in space-y-6">
            <div className="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 flex justify-between items-center">
                <div className="text-center">
                    <h3 className="text-4xl font-extrabold text-gray-900 mb-1">{avgRating.toFixed(1)}</h3>
                    <div className="flex items-center justify-center text-orange-500 mb-1">
                        {[1, 2, 3, 4, 5].map(v => <Star key={v} size={16} fill={v <= Math.round(avgRating) ? "currentColor" : "none"} />)}
                    </div>
                    <p className="text-xs text-gray-500">{totalReviews} Đánh giá</p>
                </div>
            </div>

            <div className="flex justify-end">
                <button
                    onClick={() => user ? navigate(`/customer/review/${restaurantId}`) : navigate('/login')}
                    className="flex items-center gap-2 px-6 py-3 bg-white border-2 border-orange-500 text-orange-600 font-bold rounded-xl hover:bg-orange-50 transition"
                >
                    <Edit3 size={18} /> Viết đánh giá
                </button>
            </div>

            {reviews.list.length === 0 ? (
                <div className="flex justify-center p-8"><Loader2 className="animate-spin text-orange-400" /></div>
            ) : (
                <div className="space-y-4">
                    {reviews.list.map(rv => (
                        <div key={rv.reviewId} className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
                            <div className="flex justify-between items-start mb-3">
                                <div className="flex items-center gap-3">
                                    <div className="w-10 h-10 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center font-bold">
                                        {rv.userName?.charAt(0) || '?'}
                                    </div>
                                    <div>
                                        <p className="font-bold text-gray-900 text-sm">{rv.userName}</p>
                                        <div className="flex text-orange-500">
                                            {[...Array(5)].map((_, i) => <Star key={i} size={12} fill={i < rv.rating ? "currentColor" : "none"} />)}
                                        </div>
                                    </div>
                                </div>
                                <p className="text-xs text-gray-400">
                                    {formatDateTime(rv.createdAt).date}
                                </p>
                            </div>
                            <p className="text-sm text-gray-700 leading-relaxed">{rv.comment}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default DetailReviews;
