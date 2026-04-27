import Header from '../../components/Header/Header'
import RestaurantHero from "../../components/RestaurantHero/RestaurantHero"
import MenuSection from "../../components/MenuSection/MenuSection"
import Login from "../Auth/Login"
import "./Home.css"
// import { useAtomValue } from "jotai"
// import { userAtom } from "../../store/authStore"
// import { Navigate } from "react-router-dom"
// const Home = () => {
//     const user = useAtomValue(userAtom);
//     const localUser = JSON.parse(localStorage.getItem('user'));

//     if (user && localUser?.roles?.includes("ROLE_CUSTOMER")) return <Navigate to="/customer" replace />
//     if (user && localUser?.roles?.includes(["ROLE_MANAGER", "ROLE_WAITER", "ROLE_CHEF", "ROLE_CASHIER", "ROLE_RECEPTIONIST"])) return <Navigate to="/restaurant" replace />
//     if (user && localUser?.roles?.includes("ROLE_ADMIN")) return <Navigate to="/admin" replace />

//     // return <Navigate to="/login" replace />


// }

// export default Home
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { Navigate } from "react-router-dom";

const Home = () => {
    const user = useAtomValue(userAtom);




    if (!user) {
        return <Navigate to="/login" replace />;
    }


    if (user.roles?.includes("CUSTOMER")) {
        return <Navigate to="/customer" replace />;
    }

    if (user.roles?.includes("ADMIN")) {
        return <Navigate to="/admin" replace />;
    }
    if (user.roles?.includes("MANAGER")) {
        return <Navigate to="/manager" replace />;
    }
    if (user.roles?.includes("RECEPTIONIST")) {
        return <Navigate to="/receptionist" replace />;
    }
    if (user.roles?.includes("WAITER")) {
        return <Navigate to="/waiter" replace />;
    }
    if (user.roles?.includes("CASHIER")) {
        return <Navigate to="/cashier" replace />;
    }


    const staffRoles = [
        "ROLE_MANAGER",
        "ROLE_WAITER",
        "ROLE_CHEF",
        "ROLE_CASHIER",
        "ROLE_RECEPTIONIST"
    ];

    if (user.roles?.some(role => staffRoles.includes(role))) {
        return <Navigate to="/restaurant" replace />;
    }


    return <div>Loading...</div>;
};

export default Home;