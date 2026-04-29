import { useNavigate } from 'react-router-dom';
import { useSetAtom } from 'jotai';
import { setAuthAtom } from '../store/authStore';

export default function useLogout() {
    const navigate = useNavigate();
    const setAuth = useSetAtom(setAuthAtom);

    return () => {
        setAuth(null);
        navigate('/login');
    };
}
