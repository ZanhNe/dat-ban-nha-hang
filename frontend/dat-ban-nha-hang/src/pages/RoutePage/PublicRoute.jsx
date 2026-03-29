import { useAtomValue } from "jotai"
import { userAtom } from "../../store/authStore"
import { Navigate } from "react-router-dom"

const PublicRoute = ({ children }) => {
    const user = useAtomValue(userAtom);
    if (user) {
        return <Navigate to="/" replace />
    }
    return children;
}

export default PublicRoute