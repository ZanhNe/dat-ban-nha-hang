import React, { useEffect, useState } from 'react';
import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import { User, LogOut, FileText, CreditCard, Loader2 } from 'lucide-react';
import LocationPicker from '../components/map/LocationPicker';
import SearchFilter from '../components/map/SearchFilter';
import MapCanvas from '../components/map/MapCanvas';
import RestaurantCard from '../components/map/RestaurantCard';
import RestaurantDetailPanel from '../components/map/RestaurantDetailPanel';
import PaginationControl from '../components/map/PaginationControl';
import { mapService } from '../services/mapService';

import {
  originAtom,
  radiusAtom,
  cuisineAtom,
  pageAtom,
  limitAtom,
  restaurantsAtom,
  totalItemsAtom,
  totalPagesAtom,
  loadingAtom,
  focusModeAtom,
  searchTriggerAtom
} from '../store/mapStore';

export default function MapSearchPage() {
  const origin = useAtomValue(originAtom);
  const radius = useAtomValue(radiusAtom);
  const cuisine = useAtomValue(cuisineAtom);
  const page = useAtomValue(pageAtom);
  const limit = useAtomValue(limitAtom);

  const setRestaurants = useSetAtom(restaurantsAtom);
  const setTotalItems = useSetAtom(totalItemsAtom);
  const setTotalPages = useSetAtom(totalPagesAtom);
  const [loading, setLoading] = useAtom(loadingAtom);

  const focusMode = useAtomValue(focusModeAtom);
  const restaurants = useAtomValue(restaurantsAtom);
  const searchTrigger = useAtomValue(searchTriggerAtom);

  const [showUserMenu, setShowUserMenu] = useState(false);
  const isLoggedIn = true;

  useEffect(() => {
    if (!origin || searchTrigger === 0) return;

    const fetchRestaurants = async () => {
      setLoading(true);
      try {
        const originStr = `${origin.latitude},${origin.longitude}`;
        const searchParams = {
          origin: originStr,
          radius: radius,
          cuisine: cuisine,
          page: page,
          limit: limit
        };
        const res = await mapService.searchRestaurants(searchParams);

        if (res?.data) {
          setRestaurants(res.data);
        }
        if (res?.meta) {
          setTotalItems(res.meta.totalItems || 0);
          setTotalPages(res.meta.totalPages || 1);
        }
      } catch (error) {
        console.error("Lỗi fetch search data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchRestaurants();

  }, [searchTrigger, page]);

  return (
    <div className="h-screen w-full flex flex-col md:flex-row overflow-hidden bg-gray-50 font-sans">

      {/* CỘT TRÁI (Result Sidebar) */}
      <div className={`w-full md:w-[420px] h-[50vh] md:h-full flex flex-col shadow-2xl z-20 ${focusMode ? 'bg-white' : 'bg-gray-50'}`}>
        {focusMode ? (
          <RestaurantDetailPanel />
        ) : (
          <div className="flex flex-col h-full bg-slate-50 relative p-4 pb-0">
            <div className="flex-1 overflow-y-auto pr-2 pb-4 scrollbar-hide">
              {!origin && (
                <div className="text-center mt-10 text-gray-500">

                  <p>Hãy nhập địa chỉ hoặc sử dụng GPS để tìm nhà hàng xung quanh bạn.</p>
                </div>
              )}

              {loading && origin && (
                <div className="flex flex-col items-center justify-center mt-12 bg-white rounded-xl p-6 shadow-sm border border-blue-100">
                  <Loader2 className="w-10 h-10 text-blue-500 animate-spin mb-3" />
                  <div className="text-blue-600 font-medium animate-pulse">Đang tìm kiếm nhà hàng...</div>
                </div>
              )}

              {!loading && origin && restaurants.length === 0 && searchTrigger > 0 && (
                <div className="text-center mt-10 text-gray-500 bg-white rounded-xl p-6 shadow-sm">
                  Không tìm thấy nhà hàng nào trong khu vực và điều kiện này.
                </div>
              )}

              {!loading && restaurants.map(res => (
                <RestaurantCard key={res.restaurantId} restaurant={res} />
              ))}
            </div>
            {/* Phân trang */}
            {origin && !loading && restaurants.length > 0 && <PaginationControl />}
          </div>
        )}
      </div>

      {/* Map View + Overlay Controls */}
      <div className="flex-1 relative h-[50vh] md:h-full">
        <MapCanvas />

        {/* Top Control Bar Overlays */}
        {!focusMode && (
          <div className="absolute top-4 left-4 right-4 md:left-8 md:right-8 lg:w-[600px] z-10">
            <LocationPicker />
            {origin && <SearchFilter />}
          </div>
        )}

        {/* User Status / Avatar Top Right */}
        {isLoggedIn && (
          <div className="absolute top-4 right-4 z-20">
            <div className="relative">
              <button
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="w-10 h-10 rounded-full bg-white shadow-md border-2 border-primary-500 flex items-center justify-center overflow-hidden hover:shadow-lg transition"
              >
                <img src="https://ui-avatars.com/api/?name=User&background=0D8ABC&color=fff" alt="Avatar" />
              </button>

              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-xl border py-2">
                  <div className="px-4 py-3 border-b">
                    <p className="font-semibold text-gray-800">Nguyen Van A</p>
                    <p className="text-sm text-gray-500">nguyenvana@gmail.com</p>
                    <p className="text-sm text-gray-500">0901234567</p>
                  </div>
                  <div className="py-2">
                    <button className="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center text-sm text-gray-700">
                      <User className="w-4 h-4 mr-2" /> Xem thông tin cá nhân
                    </button>
                    <button className="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center text-sm text-gray-700">
                      <FileText className="w-4 h-4 mr-2" /> Lịch sử đặt bàn
                    </button>
                    <button className="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center text-sm text-gray-700">
                      <CreditCard className="w-4 h-4 mr-2" /> Thanh toán đặt cọc
                    </button>
                  </div>
                  <div className="border-t py-1">
                    <button className="w-full text-left px-4 py-2 hover:bg-red-50 text-red-600 flex items-center text-sm font-medium">
                      <LogOut className="w-4 h-4 mr-2" /> Đăng xuất
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

    </div>
  );
}
