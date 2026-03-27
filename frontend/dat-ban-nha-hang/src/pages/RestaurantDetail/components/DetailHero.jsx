import React from 'react';
import { ChevronLeft, Share, Heart, Star } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const DetailHero = ({ restaurant }) => {
    const navigate = useNavigate();

    return (
        <div className="relative h-[45vh] w-full">
            <div className="absolute top-0 inset-x-0 p-4 pt-6 flex justify-between items-center z-20">
                <button onClick={() => navigate(-1)} className="w-10 h-10 bg-black/30 backdrop-blur-md rounded-full flex justify-center items-center text-white hover:bg-black/50 transition">
                    <ChevronLeft size={24} />
                </button>
                <div className="flex gap-3">
                    <button className="w-10 h-10 bg-black/30 backdrop-blur-md rounded-full flex justify-center items-center text-white hover:bg-black/50 transition">
                        <Share size={20} />
                    </button>
                    <button className="w-10 h-10 bg-black/30 backdrop-blur-md rounded-full flex justify-center items-center text-white hover:bg-black/50 transition">
                        <Heart size={20} />
                    </button>
                </div>
            </div>

            <img 
                src={restaurant.restaurantImage || "https://images.unsplash.com/photo-1555396273-367ea4eb4db5"} 
                alt="Restaurant cover" 
                className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent"></div>

            <div className="absolute bottom-6 inset-x-4">
                <div className="bg-white/10 backdrop-blur-xl border border-white/20 p-5 rounded-3xl text-white shadow-2xl">
                    <div className="flex justify-between items-start">
                        <div>
                            <div className="flex flex-wrap gap-2 mb-2">
                                {restaurant.restaurantCuisines?.slice(0,2).map(c => (
                                    <span key={c} className="text-[10px] uppercase tracking-wider font-bold bg-white/20 px-2 py-1 rounded-sm">
                                        {c}
                                    </span>
                                ))}
                            </div>
                            <h1 className="text-3xl font-extrabold leading-tight mb-1 drop-shadow-md">
                                {restaurant.restaurantName?.split('-')[0]}
                            </h1>
                            <p className="text-gray-200 text-sm font-medium">
                                {restaurant.restaurantName?.split('-')[1]?.trim()}
                            </p>
                        </div>
                        
                        <div className="bg-white text-gray-900 px-3 py-2 rounded-xl flex flex-col items-center shadow-lg">
                            <span className="flex items-center font-bold text-lg">
                                {restaurant.restaurantAvgRating} <Star size={16} className="text-orange-500 ml-1" fill="currentColor"/>
                            </span>
                            <span className="text-[10px] text-gray-500 font-medium">
                                {restaurant.restaurantTotalReviews}+ Đánh giá
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DetailHero;
