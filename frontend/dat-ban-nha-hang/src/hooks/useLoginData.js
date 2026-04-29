import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSetAtom } from "jotai";
import { authService } from "../services/authService";
import { formatApiError } from "../services/apiShape";
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
            setAuth(data);

            const roles = data.user?.roles || [];

            if (roles.includes("ADMIN")) {
                navigate("/admin");
            } else if (roles.includes("RECEPTIONIST")) {
                navigate("/receptionist");
            } else if (roles.includes("WAITER")) {
                navigate("/waiter");
            } else if (roles.includes("CASHIER")) {
                navigate("/cashier");
            } else if (roles.includes("CUSTOMER")) {
                navigate("/customer");
            } else if (roles.includes("MANAGER")) {
                navigate("/manager");
            } else {
                navigate("/");
            }
        } catch (err) {
            setError(formatApiError(err, 'Tên đăng nhập hoặc mật khẩu không đúng!').displayMessage);
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