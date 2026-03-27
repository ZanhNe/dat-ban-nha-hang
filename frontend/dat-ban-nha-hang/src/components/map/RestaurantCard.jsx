import React from 'react';
import { useAtom, useSetAtom } from 'jotai';
import { Star, MapPin, Clock } from 'lucide-react';
import { selectedRestaurantAtom, focusModeAtom } from '../../store/mapStore';

export default function RestaurantCard({ restaurant }) {
  const setSelected = useSetAtom(selectedRestaurantAtom);
  const setFocusMode = useSetAtom(focusModeAtom);

  const handleClick = () => {
    setSelected(restaurant);
    setFocusMode(true);
  };

  return (
    <div 
      className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 mb-3 hover:shadow-md cursor-pointer transition-shadow"
      onClick={handleClick}
    >
      <div className="flex gap-3">
        <img 
          src={restaurant.restaurantLogo || "https://placehold.co/100x100?text=No+Image"} 
          alt={restaurant.restaurantName}
          className="w-20 h-20 object-cover rounded-md"
        />
        <div className="flex-1">
          <h3 className="font-semibold text-gray-900 leading-tight line-clamp-2">
            {restaurant.restaurantName}
          </h3>
          
          <div className="flex items-center gap-2 mt-1 text-sm text-gray-600">
            <span className="flex items-center text-yellow-500 font-medium">
              <Star className="w-4 h-4 fill-current mr-1" />
              {restaurant.restaurantAvgRating?.toFixed(1) || "0.0"}
            </span>
            <span>({restaurant.restaurantTotalReviews} đánh giá)</span>
          </div>

          <div className="flex items-center justify-between mt-2 text-sm text-gray-500">
            <span className="flex items-center">
              <MapPin className="w-4 h-4 mr-1" />
              {restaurant.restaurantDistance ? `${restaurant.restaurantDistance.toFixed(1)} km` : "N/A"}
            </span>
            <span className={`flex items-center font-medium ${restaurant.restaurantIsOpen ? 'text-green-600' : 'text-red-500'}`}>
              <Clock className="w-4 h-4 mr-1" />
              {restaurant.restaurantIsOpen ? "Đang mở cửa" : "Đã đóng cửa"}
            </span>
          </div>
        </div>
      </div>
      
      <div className="mt-3 flex gap-1 flex-wrap">
        {restaurant.restaurantCuisines?.map(c => (
          <span key={c} className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
            {c}
          </span>
        ))}
      </div>
    </div>
  );
}
