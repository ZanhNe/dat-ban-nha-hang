import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSetAtom } from "jotai";
import { setAuthAtom } from "../store/authStore";
import { authService } from "../services/authService";

const useRegisterData = () => {
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

            const res = await authService.login(formData.username, formData.password);
            const data = res.data;

            localStorage.setItem('user', JSON.stringify(data.user));
            localStorage.setItem('accessToken', data.accessToken);

            setAuth(data);
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

    return {
        formData,
        isLoading,
        error,
        fieldErrors,
        handleChange,
        handleRegister,
        navigate
    };
}

export default useRegisterData;