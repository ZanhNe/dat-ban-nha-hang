import Header from '../../components/Header/Header'
import RestaurantHero from "../../components/RestaurantHero/RestaurantHero"
import MenuSection from "../../components/MenuSection/MenuSection"
import Login from "../Auth/Login"
import "./Home.css"
function Home() {
    return (
        <>
            <Header />
            <RestaurantHero />
            <div className="content-layout">
                <div className="menu-area">
                    <MenuSection />
                </div>

                <div className="map-area">
                    MAP
                </div>
            </div>

        </>
    )
}

export default Home