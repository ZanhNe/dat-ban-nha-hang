import React from 'react';
import { useAtom, useSetAtom, useAtomValue } from 'jotai';
import { radiusAtom, cuisineAtom, pageAtom, searchTriggerAtom, loadingAtom } from '../../store/mapStore';
import { Search, Loader2 } from 'lucide-react';

const CUISINES = ["Tất cả", "Lẩu", "Món Trung", "Món Thái", "Hàn Quốc", "Món Việt", "BBQ"];

export default function SearchFilter() {
  const [radius, setRadius] = useAtom(radiusAtom);
  const [cuisine, setCuisine] = useAtom(cuisineAtom);
  const setPage = useSetAtom(pageAtom);
  const setSearchTrigger = useSetAtom(searchTriggerAtom);
  const loading = useAtomValue(loadingAtom);

  const handleRadiusChange = (e) => {
    setRadius(parseInt(e.target.value, 10));
  };

  const handleCuisineSelect = (c) => {
    setCuisine(c === "Tất cả" ? "" : c);
  };

  const handleSearchClick = () => {
    setPage(0);
    setSearchTrigger(prev => prev + 1);
  };

  return (
    <div className="bg-white p-4 shadow-md rounded-lg mt-2 z-10 relative">
      <div className="mb-4">
        <label className="text-sm font-semibold text-gray-700 block mb-2">
          Bán kính tìm kiếm: <span className="text-primary-600 font-bold">{radius} km</span>
        </label>
        <input 
          type="range" 
          min="1" 
          max="20" 
          value={radius} 
          onChange={handleRadiusChange}
          className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
        />
        <div className="flex justify-between text-xs text-gray-400 mt-1">
          <span>1km</span>
          <span>20km</span>
        </div>
      </div>

      <div className="mb-2">
        <label className="text-sm font-semibold text-gray-700 block mb-2">Loại hình ẩm thực</label>
        <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
          {CUISINES.map((c) => (
            <button
              key={c}
              onClick={() => handleCuisineSelect(c)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
                (cuisine === c || (cuisine === "" && c === "Tất cả"))
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {c}
            </button>
          ))}
        </div>
      </div>

      <button
        onClick={handleSearchClick}
        disabled={loading}
        className="w-full mt-2 bg-blue-600 text-white font-medium py-2.5 rounded-lg flex items-center justify-center hover:bg-blue-700 transition disabled:bg-blue-400 disabled:cursor-wait"
      >
        {loading ? <Loader2 className="w-5 h-5 mr-2 animate-spin" /> : <Search className="w-4 h-4 mr-2" />}
        {loading ? "Đang tìm kiếm..." : "Tìm kiếm nhà hàng"}
      </button>
    </div>
  );
}
