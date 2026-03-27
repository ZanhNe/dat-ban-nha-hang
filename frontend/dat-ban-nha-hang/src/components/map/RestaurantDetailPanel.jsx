import React, { useEffect, useState } from 'react';
import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import { ArrowLeft, Navigation, Info, Star, Clock, MapPin } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { selectedRestaurantAtom, focusModeAtom, originAtom, restaurantDetailAtom } from '../../store/mapStore';
import { mapService } from '../../services/mapService';

export default function RestaurantDetailPanel() {
  const navigate = useNavigate();
  const selectedRest = useAtomValue(selectedRestaurantAtom);
  const setFocusMode = useSetAtom(focusModeAtom);
  const origin = useAtomValue(originAtom);
  const [detail, setDetail] = useAtom(restaurantDetailAtom);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (selectedRest && origin) {
      const fetchDetail = async () => {
        setLoading(true);
        try {
          const originStr = `${origin.latitude},${origin.longitude}`;
          const res = await mapService.getRestaurantDetail(selectedRest.restaurantId, originStr);
          setDetail(res.data || res); // Kiểm tra chính xác api format data trả về
        } catch (error) {
          console.error("Lỗi lấy chi tiết:", error);
        } finally {
          setLoading(false);
        }
      };
      fetchDetail();
    }
  }, [selectedRest, origin]);

  if (!selectedRest) return null;

  const handleBack = () => {
    setFocusMode(false);
    setDetail(null); // Clear detail when exit focus mode
  };

  const handleNavigateMap = () => {
    if (origin && selectedRest.restaurantLocation) {
      const { latitude: oLat, longitude: oLng } = origin;
      const { latitude: dLat, longitude: dLng } = selectedRest.restaurantLocation;
      window.open(`https://www.google.com/maps/dir/?api=1&origin=${oLat},${oLng}&destination=${dLat},${dLng}`, '_blank');
    }
  };

  const currentDisplay = detail || selectedRest;

  return (
    <div className="bg-white h-full flex flex-col shadow-lg">
      {/* Header back */}
      <div className="p-4 border-b flex items-center gap-3 sticky top-0 bg-white z-10">
        <button onClick={handleBack} className="p-2 hover:bg-gray-100 rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5 text-gray-700" />
        </button>
        <h2 className="font-semibold text-lg text-gray-800 line-clamp-1">Trở về danh sách</h2>
      </div>

      <div className="overflow-y-auto flex-1 p-4 pb-20">
        <img 
          src={currentDisplay.restaurantImage || currentDisplay.restaurantLogo || "https://placehold.co/400x200?text=No+Image"} 
          alt={currentDisplay.restaurantName}
          className="w-full h-48 object-cover rounded-xl shadow-sm mb-4"
        />

        <h1 className="text-xl font-bold text-gray-900 mb-2">{currentDisplay.restaurantName}</h1>
        
        <div className="flex items-center gap-4 text-sm text-gray-600 mb-4">
          <span className="flex items-center text-yellow-500 font-medium">
            <Star className="w-4 h-4 fill-current mr-1" />
            {currentDisplay.restaurantAvgRating?.toFixed(1)} ({currentDisplay.restaurantTotalReviews})
          </span>
          <span className="flex items-center">
            <MapPin className="w-4 h-4 mr-1" />
            {currentDisplay.restaurantDistance?.toFixed(1) || (detail && "Chi tiết")} km
          </span>
        </div>

        <p className="text-sm text-gray-700 mb-6 leading-relaxed">
          {detail?.restaurantDescription || "Đang tải mô tả..."}
        </p>

        {/* Buttons Action */}
        <div className="flex flex-col gap-3">
          <button 
            onClick={handleNavigateMap}
            className="w-full bg-blue-600 text-white font-semibold py-3 px-4 rounded-lg flex items-center justify-center hover:bg-blue-700 transition"
          >
            <Navigation className="w-5 h-5 mr-2" />
            Bắt đầu chỉ đường
          </button>
          
          <button 
            onClick={() => navigate(`/restaurants/${currentDisplay.restaurantId}`)}
            className="w-full bg-gray-100 text-gray-800 font-semibold py-3 px-4 rounded-lg flex items-center justify-center hover:bg-gray-200 transition"
          >
            <Info className="w-5 h-5 mr-2" />
            Xem thông tin nhà hàng chi tiết
          </button>
        </div>

        {loading && <div className="text-center mt-6 text-gray-500 text-sm">Đang tải chi tiết...</div>}
        
        {/* Extra Info nếu có */}
        {detail?.restaurantOperationTimes && (
          <div className="mt-6 border-t pt-4">
            <h3 className="font-semibold mb-3 flex items-center text-gray-800"><Clock className="w-5 h-5 mr-2" /> Giờ hoạt động</h3>
            <ul className="text-sm text-gray-600 space-y-2">
              {detail.restaurantOperationTimes.map((time, idx) => (
                <li key={idx} className="flex justify-between">
                  <span className="font-medium">{time.day}</span>
                  <span>{time.open} - {time.close}</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
