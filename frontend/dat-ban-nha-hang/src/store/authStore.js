import { atom } from 'jotai';

const getInitialUser = () => {
    try {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    } catch {
        return null;
    }
};

export const userAtom = atom(getInitialUser());
export const tokenAtom = atom(localStorage.getItem('accessToken') || null);

export const setAuthAtom = atom(
    null,
    (get, set, authData) => {
        if (authData) {
            set(userAtom, authData.user);
            set(tokenAtom, authData.accessToken);
            localStorage.setItem('user', JSON.stringify(authData.user));
            localStorage.setItem('accessToken', authData.accessToken);
        } else {
            set(userAtom, null);
            set(tokenAtom, null);
            localStorage.removeItem('user');
            localStorage.removeItem('accessToken');
        }
    }
);
