import { useNavigate } from "react-router-dom";
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";

const HomeCustomer = () => {
    const navigate = useNavigate();
    const user = useAtomValue(userAtom);

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col">



            {/* CONTENT */}
            <div className="flex-1 p-6 grid grid-cols-1 md:grid-cols-2 gap-6">

                {/* TÌM NHÀ HÀNG */}
                <div
                    onClick={() => navigate("map-search")}
                    className="cursor-pointer bg-white rounded-2xl p-6 shadow hover:shadow-md transition"
                >
                    <h2 className="text-lg font-bold text-gray-800 mb-2">
                        🔍 Tìm nhà hàng
                    </h2>
                    <p className="text-gray-500 text-sm">
                        Khám phá các nhà hàng gần bạn và đặt bàn nhanh chóng
                    </p>
                </div>

                {/* ĐẶT BÀN */}
                <div
                    onClick={() => navigate("bookings/pending-payment")}
                    className="cursor-pointer bg-white rounded-2xl p-6 shadow hover:shadow-md transition"
                >
                    <h2 className="text-lg font-bold text-gray-800 mb-2">
                        💳 Booking chưa thanh toán
                    </h2>
                    <p className="text-gray-500 text-sm">
                        Xem lại các đơn đặt bàn đang chờ thanh toán
                    </p>
                </div>

                {/* LỊCH SỬ */}
                <div
                    onClick={() => alert("Chưa làm 😄")}
                    className="cursor-pointer bg-white rounded-2xl p-6 shadow hover:shadow-md transition"
                >
                    <h2 className="text-lg font-bold text-gray-800 mb-2">
                        📜 Lịch sử đặt bàn
                    </h2>
                    <p className="text-gray-500 text-sm">
                        Xem lại các lần đặt bàn trước đây
                    </p>
                </div>

                {/* PROFILE */}
                <div
                    onClick={() => alert("Chưa làm 😄")}
                    className="cursor-pointer bg-white rounded-2xl p-6 shadow hover:shadow-md transition"
                >
                    <h2 className="text-lg font-bold text-gray-800 mb-2">
                        👤 Thông tin cá nhân
                    </h2>
                    <p className="text-gray-500 text-sm">
                        Cập nhật thông tin tài khoản của bạn
                    </p>
                </div>

            </div>
        </div>
    );
};

export default HomeCustomer;