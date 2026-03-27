import React, { useState } from 'react';
import { useSetAtom } from 'jotai';
import { MapPin, Navigation } from 'lucide-react';
import { originAtom, addressAtom, loadingAtom } from '../../store/mapStore';
import { mapService } from '../../services/mapService';

export default function LocationPicker() {
  const setOrigin = useSetAtom(originAtom);
  const setAddress = useSetAtom(addressAtom);
  const setLoading = useSetAtom(loadingAtom);
  const [inputValue, setInputValue] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!inputValue.trim()) return;

    try {
      setLoading(true);
      const data = await mapService.getCoordinates(inputValue);
      if (data?.data?.geometry?.location) {
        const { latitude, longitude } = data.data.geometry.location;
        setOrigin({ latitude, longitude });
        setAddress(inputValue);
      } else {
        alert("Không tìm thấy tọa độ cho địa chỉ này.");
      }
    } catch (error) {
      alert("Lỗi khi tìm tọa độ: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGPS = () => {
    if (!navigator.geolocation) {
      alert("Trình duyệt của bạn không hỗ trợ Geolocation.");
      return;
    }
    setLoading(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setOrigin({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        });
        setAddress("Vị trí hiện tại");
        setInputValue("Vị trí hiện tại");
        setLoading(false);
      },
      (error) => {
        alert("Không thể lấy vị trí: " + error.message);
        setLoading(false);
      }
    );
  };

  return (
    <div className="bg-white p-4 shadow-md rounded-lg flex flex-col md:flex-row gap-2 z-10 relative">
      <form onSubmit={handleSearch} className="flex-1 flex items-center border border-gray-300 rounded-md px-3 py-2">
        <MapPin className="text-gray-400 w-5 h-5 mr-2" />
        <input 
          type="text" 
          placeholder="Nhập địa chỉ của bạn..." 
          className="w-full outline-none text-sm"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
        />
        <button type="submit" className="hidden">Submit</button>
      </form>
      <button 
        onClick={handleGPS}
        className="flex items-center justify-center bg-blue-50 text-blue-600 px-4 py-2 rounded-md hover:bg-blue-100 transition whitespace-nowrap"
      >
        <Navigation className="w-4 h-4 mr-2" />
        <span className="text-sm font-medium">Vị trí hiện tại</span>
      </button>
    </div>
  );
}
