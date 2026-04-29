import { atom } from 'jotai';

const normalizeRole = (role) => {
    if (!role) return null;
    return role.startsWith('ROLE_') ? role.replace('ROLE_', '') : role;
};

const normalizeUser = (user) => {
    if (!user) return null;
    const rawRoles = Array.isArray(user.roles) ? user.roles : [];
    const roles = rawRoles
        .map((role) => (typeof role === 'string' ? role : role?.name))
        .map(normalizeRole)
        .filter(Boolean);

    return { ...user, roles };
};

const getInitialUser = () => {
    try {
        const storedUser = localStorage.getItem('user');
        return storedUser ? normalizeUser(JSON.parse(storedUser)) : null;
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
            const normalizedUser = normalizeUser(authData.user);
            set(userAtom, normalizedUser);
            set(tokenAtom, authData.accessToken);
            localStorage.setItem('user', JSON.stringify(normalizedUser));
            localStorage.setItem('accessToken', authData.accessToken);
        } else {
            set(userAtom, null);
            set(tokenAtom, null);
            localStorage.removeItem('user');
            localStorage.removeItem('accessToken');
        }
    }
);
