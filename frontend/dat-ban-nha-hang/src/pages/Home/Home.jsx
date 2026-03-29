import Header from '../../components/Header/Header'
import RestaurantHero from "../../components/RestaurantHero/RestaurantHero"
import MenuSection from "../../components/MenuSection/MenuSection"
import Login from "../Auth/Login"
import "./Home.css"
import { useAtomValue } from "jotai"
import { userAtom } from "../../store/authStore"
import { Navigate } from "react-router-dom"
const Home = () => {
    const user = useAtomValue(userAtom);
    const localUser = JSON.parse(localStorage.getItem('user'));

    if (user && localUser?.roles?.includes("ROLE_CUSTOMER")) return <Navigate to="/customer" replace />
    if (user && localUser?.roles?.includes(["ROLE_MANAGER", "ROLE_WAITER", "ROLE_CHEF", "ROLE_CASHIER", "ROLE_RECEPTIONIST"])) return <Navigate to="/restaurant" replace />
    if (user && localUser?.roles?.includes("ROLE_ADMIN")) return <Navigate to="/admin" replace />

    return <Navigate to="/login" replace />
}

export default Home