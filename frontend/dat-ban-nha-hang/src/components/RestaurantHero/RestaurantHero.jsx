import "./RestaurantHero.css"

function RestaurantHero() {
    return (
        <div className="restaurant-hero">

            <img
                className="restaurant-image"
                src="https://images.unsplash.com/photo-1555396273-367ea4eb4db5"
                alt="restaurant"
            />

            <div className="restaurant-info">

                <h1>Kichi Sushi Bar</h1>

                <div className="rating">
                    ⭐ 4.5 <span className="cuisine">Japanese, Sushi</span>
                </div>

                <div className="details">
                    <span>📍 Tokyo, Japan</span>
                    <span>💰 $$</span>
                </div>

            </div>

            <div className="tabs">
                <button className="active">Menu</button>
                <button>Reviews</button>
                <button>About Us</button>
            </div>

        </div>
    )
}

export default RestaurantHero