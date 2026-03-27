import React, { useState, useEffect, useMemo } from 'react';
import { X, Minus, Plus, Calendar, Clock, Users, CheckCircle2, Loader2 } from 'lucide-react';
import { restaurantService } from '../../services/restaurantService';

const BookingBottomSheet = ({ isOpen, onClose, restaurant }) => {
    const [step, setStep] = useState(1);


    const dates = useMemo(() => {
        const result = [];
        const today = new Date();
        const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];

        for (let i = 0; i < 10; i++) {
            const d = new Date(today);
            d.setDate(today.getDate() + i);


            const offset = d.getTimezoneOffset()
            const localDate = new Date(d.getTime() - (offset * 60 * 1000))
            const id = localDate.toISOString().split('T')[0];

            let label = '';
            if (i === 0) label = `Hôm nay, ${d.getDate()}`;
            else if (i === 1) label = `Ngày mai, ${d.getDate()}`;
            else label = `${days[d.getDay()]}, ${d.getDate()}`;

            result.push({ id, label, dateObj: d });
        }
        return result;
    }, []);


    const [selectedDate, setSelectedDate] = useState(dates[0].id);
    const [selectedTime, setSelectedTime] = useState('');
    const [guests, setGuests] = useState(4);


    const times = useMemo(() => {
        if (!restaurant?.restaurantOperationTimes?.length) return [];
        const opTime = restaurant.restaurantOperationTimes[0];

        const openStr = opTime.open || '09:00';
        const closeStr = opTime.close || '22:00';

        const startTime = parseInt(openStr.split(':')[0]);
        let endTime = parseInt(closeStr.split(':')[0]);
        if (endTime <= startTime) endTime += 24;

        const now = new Date();
        const isToday = selectedDate === dates[0].id;

        // Thời gian hiện tại + 30 phút
        const minTimeMs = now.getTime() + 30 * 60000;

        const result = [];
        for (let h = startTime; h < endTime; h++) {
            for (let m of ['00', '30']) {
                const hourDisp = h % 24;
                const timeStr = `${hourDisp.toString().padStart(2, '0')}:${m}`;

                if (isToday) {
                    const slotDate = new Date(now);
                    slotDate.setHours(hourDisp, parseInt(m), 0, 0);
                    if (slotDate.getTime() > minTimeMs) {
                        result.push(timeStr);
                    }
                } else {
                    result.push(timeStr);
                }
            }
        }
        return result;
    }, [selectedDate, restaurant]);

    // Tự động chọn khung giờ đầu tiên nếu thời gian cũ không hợp lệ ở ngày mới
    useEffect(() => {
        if (times.length > 0 && !times.includes(selectedTime)) {
            setSelectedTime(times[0]);
        }
    }, [times, selectedDate]);

    // State Step 2 (Gọi API lấy bàn)
    const [isLoadingTables, setIsLoadingTables] = useState(false);
    const [areasData, setAreasData] = useState([]);
    const [selectedArea, setSelectedArea] = useState('');
    const [selectedTable, setSelectedTable] = useState(null);

    // Xử lý nạp danh sách bàn từ Server khi chuyển sang Step 2
    const fetchTables = async () => {
        setIsLoadingTables(true);
        try {
            const data = await restaurantService.getAvailableTables(restaurant.restaurantId, selectedDate, selectedTime, guests);
            setAreasData(data);
            if (data.length > 0) {
                setSelectedArea(data[0].areaName);
            }
        } catch (error) {
            console.error("Lỗi lấy danh sách bàn: ", error);
        } finally {
            setIsLoadingTables(false);
            setStep(2); // Qua bước 2
        }
    };

    if (!isOpen) return null;
    const currentArea = areasData.find(a => a.areaName === selectedArea);

    return (
        <div className="fixed inset-0 z-50 flex justify-center items-end bg-black/50 backdrop-blur-sm transition-opacity duration-300">
            {/* Sheet Container */}
            <div className="w-full max-w-md bg-white rounded-t-3xl shadow-2xl flex flex-col max-h-[90vh] min-h-[50vh]">

                {/* Header */}
                <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
                    <h2 className="text-xl font-bold text-gray-900">
                        {step === 1 ? 'Chọn thời gian' : 'Chọn vị trí bàn'}
                    </h2>
                    <button onClick={onClose} className="p-2 bg-gray-100 rounded-full hover:bg-gray-200 transition">
                        <X size={20} className="text-gray-600" />
                    </button>
                </div>

                {/* Content Area */}
                <div className="flex-1 overflow-y-auto p-6 space-y-8">

                    {step === 1 && (
                        <div className="space-y-6">
                            {/* Selected Date */}
                            <div>
                                <h3 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                                    <Calendar size={16} className="text-orange-500" /> Ngày đến (Tối đa 10 ngày)
                                </h3>
                                <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
                                    {dates.map(date => (
                                        <button
                                            key={date.id}
                                            onClick={() => setSelectedDate(date.id)}
                                            className={`flex-shrink-0 px-5 py-3 rounded-2xl border-2 transition-all font-medium ${selectedDate === date.id ? 'border-orange-500 bg-orange-50 text-orange-600' : 'border-gray-100 text-gray-500 hover:border-orange-200'}`}
                                        >
                                            {date.label}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Selected Time */}
                            <div>
                                <h3 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                                    <Clock size={16} className="text-orange-500" /> Giờ nhận bàn (Cách hiện tại ≥30p)
                                </h3>
                                {times.length === 0 ? (
                                    <p className="text-red-500 text-sm font-medium bg-red-50 p-3 rounded-xl">Quán đã nghỉ hoặc hết ca nhận khách cho ngày này!</p>
                                ) : (
                                    <div className="flex flex-wrap gap-3">
                                        {times.map(t => (
                                            <button
                                                key={t}
                                                onClick={() => setSelectedTime(t)}
                                                className={`px-5 py-2.5 rounded-full border-2 transition-all font-medium ${selectedTime === t ? 'border-orange-500 bg-orange-50 text-orange-600' : 'border-gray-100 text-gray-600 hover:border-gray-300'}`}
                                            >
                                                {t}
                                            </button>
                                        ))}
                                    </div>
                                )}
                            </div>

                            {/* Guests */}
                            <div>
                                <h3 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                                    <Users size={16} className="text-orange-500" /> Số lượng người
                                </h3>
                                <div className="flex items-center gap-6 bg-gray-50 p-2 rounded-2xl w-fit">
                                    <button
                                        onClick={() => setGuests(Math.max(1, guests - 1))}
                                        className="w-10 h-10 flex items-center justify-center bg-white rounded-xl shadow-sm border border-gray-100 text-gray-600 hover:text-orange-500"
                                    >
                                        <Minus size={20} />
                                    </button>
                                    <span className="text-xl font-bold w-6 text-center">{guests}</span>
                                    <button
                                        onClick={() => setGuests(guests + 1)}
                                        className="w-10 h-10 flex items-center justify-center bg-white rounded-xl shadow-sm border border-gray-100 text-gray-600 hover:text-orange-500"
                                    >
                                        <Plus size={20} />
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {step === 2 && (
                        <div className="space-y-6">
                            {/* Summary Box */}
                            <div className="bg-orange-50 p-4 rounded-2xl flex justify-between items-center border border-orange-100">
                                <div>
                                    <p className="text-sm text-orange-600 font-medium">{dates.find(d => d.id === selectedDate)?.label} • {selectedTime}</p>
                                    <p className="text-xs text-orange-500 mt-1">{guests} người</p>
                                </div>
                                <button onClick={() => setStep(1)} className="text-sm px-3 py-1 bg-white rounded-lg border border-orange-200 text-orange-600 font-bold shadow-sm">
                                    Sửa
                                </button>
                            </div>

                            {/* Tables render logic */}
                            {areasData.length === 0 ? (
                                <div className="text-center py-10 bg-gray-50 rounded-2xl">
                                    <p className="text-gray-500 font-medium">Không có khu vực nào có sẵn lúc này.</p>
                                </div>
                            ) : (
                                <>
                                    <div className="flex gap-2 p-1 bg-gray-100 rounded-xl overflow-x-auto scrollbar-hide">
                                        {areasData.map(area => (
                                            <button
                                                key={area.areaName}
                                                onClick={() => setSelectedArea(area.areaName)}
                                                className={`whitespace-nowrap flex-1 py-2 px-4 text-sm font-semibold rounded-lg transition-all ${selectedArea === area.areaName ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500'}`}
                                            >
                                                {area.areaName}
                                            </button>
                                        ))}
                                    </div>

                                    <div className="grid grid-cols-2 gap-4">
                                        {currentArea?.tables.map(table => (
                                            <button
                                                key={table.tableId}
                                                disabled={!table.isAvailable}
                                                onClick={() => setSelectedTable(table.tableId)}
                                                className={`
                                                    relative p-4 rounded-2xl border-2 text-left transition-all overflow-hidden
                                                    ${!table.isAvailable ? 'bg-gray-50 border-gray-100 cursor-not-allowed opacity-70' :
                                                        selectedTable === table.tableId ? 'border-green-500 bg-green-50' : 'border-gray-200 hover:border-green-300 bg-white'}
                                                `}
                                            >
                                                {selectedTable === table.tableId && (
                                                    <div className="absolute top-3 right-3 text-green-500">
                                                        <CheckCircle2 size={20} fill="currentColor" className="text-white" />
                                                    </div>
                                                )}
                                                <div className={`font-bold ${table.isAvailable ? 'text-gray-900' : 'text-gray-400'}`}>{table.label}</div>
                                                <div className="text-xs text-gray-500 mt-1">
                                                    <span>Sức chứa: {table.capacity}</span>
                                                </div>
                                                {!table.isAvailable && (
                                                    <div className="text-[10px] font-bold text-red-400 mt-2 bg-red-50 w-fit px-2 py-1 rounded-md">
                                                        {table.reason || 'Đã kín'}
                                                    </div>
                                                )}
                                            </button>
                                        ))}
                                    </div>
                                </>
                            )}
                        </div>
                    )}

                </div>

                {/* Footer fixed inside sheet */}
                <div className="p-6 border-t border-gray-100 bg-white rounded-b-3xl">
                    {step === 1 ? (
                        <button
                            disabled={!selectedTime || isLoadingTables}
                            onClick={fetchTables}
                            className={`flex justify-center items-center w-full py-4 rounded-2xl font-bold text-lg transition-colors shadow-lg
                                ${(!selectedTime || isLoadingTables) ? 'bg-gray-200 text-gray-400 shadow-none' : 'bg-orange-500 text-white hover:bg-orange-600 shadow-orange-500/30'}`}
                        >
                            {isLoadingTables ? <Loader2 className="animate-spin" size={24} /> : 'Tiếp tục chọn bàn'}
                        </button>
                    ) : (
                        <button
                            disabled={!selectedTable}
                            className={`w-full py-4 rounded-2xl font-bold text-lg transition-colors shadow-lg 
                                ${selectedTable ? 'bg-green-500 text-white hover:bg-green-600 shadow-green-500/30' : 'bg-gray-200 text-gray-400 cursor-not-allowed shadow-none'}`}
                        >
                            Xác nhận & Cọc ({(restaurant?.restaurantBaseDeposit || 0).toLocaleString()}đ)
                        </button>
                    )}
                </div>

            </div>
        </div>
    );
};

export default BookingBottomSheet;
