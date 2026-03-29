import { Link } from 'react-router-dom';
import { Loader2, ArrowLeft } from 'lucide-react';
import useLoginData from '../../hooks/useLoginData';

const Login = () => {
    const { username, password, isLoading, error, handleChange, handleLogin, navigate } = useLoginData();

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col justify-center px-4 font-sans">
            <button
                onClick={() => navigate(-1)}
                className="absolute top-6 left-4 p-2 bg-white rounded-full shadow-sm text-gray-600"
            >
                <ArrowLeft size={20} />
            </button>
            <div className="max-w-md w-full mx-auto">
                {/* Logo / Title */}
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-extrabold text-gray-900 mb-2">Đăng nhập</h2>
                    <p className="text-gray-500">Chào mừng bạn quay trở lại!</p>
                </div>

                {/* Form */}
                <form onSubmit={handleLogin} className="bg-white p-6 rounded-3xl shadow-sm border border-gray-100 space-y-5">

                    {error && (
                        <div className="p-3 bg-red-50 border border-red-100 rounded-xl text-red-500 text-sm font-medium">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-bold text-gray-700 mb-2">Tên đăng nhập</label>
                        <input
                            type="text"
                            required
                            value={username}
                            name="username"
                            onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-colors"
                            placeholder="Nhập tên đăng nhập"
                        />
                    </div>
                    <div>
                        <div className="flex justify-between items-center mb-2">
                            <label className="block text-sm font-bold text-gray-700">Mật khẩu</label>
                            <a href="#" className="text-xs font-semibold text-orange-500 hover:text-orange-600">Quên mật khẩu?</a>
                        </div>
                        <input
                            type="password"
                            required
                            value={password}
                            name="password"
                            onChange={handleChange}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-colors"
                            placeholder="••••••••"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full mt-2 py-4 rounded-xl bg-orange-500 text-white font-bold text-lg hover:bg-orange-600 transition-colors shadow-lg shadow-orange-500/30 flex justify-center items-center"
                    >
                        {isLoading ? <Loader2 className="animate-spin" size={24} /> : 'Đăng nhập'}
                    </button>
                </form>

                {/* Footer */}
                <p className="text-center text-gray-500 mt-8 font-medium">
                    Chưa có tài khoản?{' '}
                    <Link to="/register" className="text-orange-600 font-bold hover:underline">
                        Đăng ký ngay
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Login;