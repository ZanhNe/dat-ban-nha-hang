import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import { User, LogOut, FileText, CreditCard, Loader2 } from 'lucide-react';
import LocationPicker from '../components/map/LocationPicker';
import SearchFilter from '../components/map/SearchFilter';
import MapCanvas from '../components/map/MapCanvas';
import RestaurantCard from '../components/map/RestaurantCard';
import RestaurantDetailPanel from '../components/map/RestaurantDetailPanel';
import PaginationControl from '../components/map/PaginationControl';
import { mapService } from '../services/mapService';
import { formatApiError, unwrapData, unwrapMeta } from '../services/apiShape';

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
  const setCuisine = useSetAtom(cuisineAtom);
  const [searchParams] = useSearchParams();

  const setRestaurants = useSetAtom(restaurantsAtom);
  const setTotalItems = useSetAtom(totalItemsAtom);
  const setTotalPages = useSetAtom(totalPagesAtom);
  const [loading, setLoading] = useAtom(loadingAtom);

  const focusMode = useAtomValue(focusModeAtom);
  const restaurants = useAtomValue(restaurantsAtom);
  const searchTrigger = useAtomValue(searchTriggerAtom);

  const [showUserMenu, setShowUserMenu] = useState(false);
  const [error, setError] = useState('');
  const isLoggedIn = true;

  useEffect(() => {
    const initialCuisine = searchParams.get('cuisine');
    if (initialCuisine) {
      setCuisine(initialCuisine);
    }
  }, [searchParams, setCuisine]);

  useEffect(() => {
    if (!origin || searchTrigger === 0) return;

    const fetchRestaurants = async () => {
      setLoading(true);
      setError('');
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

        const list = unwrapData(res) || [];
        setRestaurants(Array.isArray(list) ? list : []);

        const meta = unwrapMeta(res) || res?.meta;
        if (meta) {
          setTotalItems(meta.totalItems || 0);
          setTotalPages(meta.totalPages || 1);
        } else {
          setTotalItems(0);
          setTotalPages(1);
        }
      } catch (error) {
        setError(formatApiError(error, 'Không thể tìm nhà hàng với điều kiện hiện tại.').displayMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchRestaurants();

  }, [searchTrigger, page, origin, radius, cuisine, limit, setLoading, setRestaurants, setTotalItems, setTotalPages]);

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
                  {error || 'Không tìm thấy nhà hàng nào trong khu vực và điều kiện này.'}
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

      </div>

    </div>
  );
}
