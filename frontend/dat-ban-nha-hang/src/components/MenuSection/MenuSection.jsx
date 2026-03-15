import "./MenuSection.css"

function MenuSection() {
    return (
        <div className="menu-section">

            <div className="menu-container">

                <div className="menu-categories">
                    <h3>Menu</h3>

                    <ul>
                        <li className="active">Appetizers</li>
                        <li>Sushi</li>
                        <li>Ramen</li>
                    </ul>
                </div>

                <div className="menu-items">

                    <div className="menu-item">
                        <div>
                            <h4>fsfsfs</h4>
                            <p>sdfhsbdfs</p>
                        </div>
                        <span>$12</span>
                    </div>

                    <div className="menu-item">
                        <div>
                            <h4>hfbsdhfbsd</h4>
                            <p>hfbsdhf</p>
                        </div>
                        <span>$10</span>
                    </div>

                    <div className="menu-item">
                        <div>
                            <h4>Ssdfb sdhjfb</h4>
                            <p>ffhsdbfjsbbfsndkj</p>
                        </div>
                        <span>$14</span>
                    </div>

                </div>

            </div>

        </div>
    )
}

export default MenuSection