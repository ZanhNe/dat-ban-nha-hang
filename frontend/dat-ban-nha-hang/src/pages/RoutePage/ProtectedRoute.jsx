import { useAtomValue } from "jotai"
import { userAtom } from "../../store/authStore"
import { Navigate, Outlet } from "react-router-dom"

const ProtectedRoute = ({ allowedRoles }) => {
    const user = useAtomValue(userAtom);
    if (!user) {
        return <Navigate to="/login" replace />
    }
    if (allowedRoles && !allowedRoles.some(role => user.roles.includes(role))) {
        return <Navigate to="/403" replace />
    }
    return <Outlet />
}

export default ProtectedRoute