import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useSetAtom } from 'jotai';
import { setAuthAtom } from '../../store/authStore';
import { authService } from '../../services/authService';
import { Loader2, ArrowLeft } from 'lucide-react';

const Register = () => {
    const navigate = useNavigate();
    const setAuth = useSetAtom(setAuthAtom);

    const [formData, setFormData] = useState({
        username: '',
        password: '',
        fullName: '',
        email: '',
        phone: '',
        address: ''
    });

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        setFieldErrors({});
        setIsLoading(true);

        try {
            await authService.register(formData);

            const loginData = await authService.login(formData.username, formData.password);
            setAuth(loginData);
            navigate('/');

        } catch (err) {
            const errData = err.response?.data;
            if (errData?.errors) {
                setFieldErrors(errData.errors);
            } else {
                setError(errData?.message || 'Đăng ký thất bại, vui lòng thử lại!');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col justify-center px-4 font-sans py-8">
            <button
                onClick={() => navigate(-1)}
                className="absolute top-6 left-4 p-2 bg-white rounded-full shadow-sm text-gray-600"
            >
                <ArrowLeft size={20} />
            </button>
            <div className="max-w-md w-full mx-auto">
                <div className="text-center mb-8 mt-6">
                    <h2 className="text-3xl font-extrabold text-gray-900 mb-2">Tạo tài khoản</h2>
                    <p className="text-gray-500">Bắt đầu trải nghiệm của bạn</p>
                </div>

                <form onSubmit={handleRegister} className="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 space-y-4">

                    {error && (
                        <div className="p-3 bg-red-50 border border-red-100 rounded-xl text-red-500 text-sm font-medium">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Họ và tên *</label>
                        <input
                            name="fullName" required value={formData.fullName} onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500"
                            placeholder="Nguyễn Văn A"
                        />
                        {fieldErrors.fullName && <p className="text-red-500 text-xs mt-1 font-semibold">{fieldErrors.fullName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Tên đăng nhập *</label>
                        <input
                            name="username" required value={formData.username} onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500"
                            placeholder="nguyenvana"
                        />
                        {fieldErrors.username && <p className="text-red-500 text-xs mt-1 font-semibold">{fieldErrors.username}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Email *</label>
                        <input
                            type="email" name="email" required value={formData.email} onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500"
                            placeholder="email@example.com"
                        />
                        {fieldErrors.email && <p className="text-red-500 text-xs mt-1 font-semibold">{fieldErrors.email}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Số điện thoại *</label>
                        <input
                            type="tel" name="phone" required value={formData.phone} onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500"
                            placeholder="0912345678"
                        />
                        {fieldErrors.phone && <p className="text-red-500 text-xs mt-1 font-semibold">{fieldErrors.phone}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Mật khẩu *</label>
                        <input
                            type="password" name="password" required value={formData.password} onChange={handleChange} minLength={6}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500"
                            placeholder="Tối thiểu 6 ký tự"
                        />
                        {fieldErrors.password && <p className="text-red-500 text-xs mt-1 font-semibold">{fieldErrors.password}</p>}
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full mt-4 py-4 rounded-xl bg-orange-500 text-white font-bold text-lg hover:bg-orange-600 transition-colors shadow-lg shadow-orange-500/30 flex justify-center items-center"
                    >
                        {isLoading ? <Loader2 className="animate-spin" size={24} /> : 'Xác nhận Đăng ký'}
                    </button>
                </form>

                <p className="text-center text-gray-500 mt-6 font-medium">
                    Đã có tài khoản?{' '}
                    <Link to="/login" className="text-orange-600 font-bold hover:underline">
                        Đăng nhập
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
