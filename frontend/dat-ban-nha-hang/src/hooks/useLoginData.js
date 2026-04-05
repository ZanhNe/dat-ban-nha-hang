import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSetAtom } from "jotai";
import { authService } from "../services/authService";
import { setAuthAtom } from "../store/authStore";

const useLoginData = () => {
    const navigate = useNavigate();
    const setAuth = useSetAtom(setAuthAtom);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === 'username') {
            setUsername(value);
        } else if (name === 'password') {
            setPassword(value);
        }
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const res = await authService.login(username, password);

            const data = res.data;

            // localStorage.setItem('user', JSON.stringify(data.user));
            // localStorage.setItem('accessToken', data.accessToken);

            setAuth(data);
            console.log('check', data);
            navigate('/customer');
        } catch (err) {
            setError(err.response?.data?.message || 'Tên đăng nhập hoặc mật khẩu không đúng!');
        } finally {
            setIsLoading(false);
        }
    };

    return {
        username,
        password,
        isLoading,
        error,
        handleChange,
        handleLogin,
        navigate
    };
}

export default useLoginData;