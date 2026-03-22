import './Header.css'
import { Link } from "react-router-dom"

function Header() {
    return (
        <header className="header">
            <div className="container">

                <div className="logo">
                    <span>Logo</span>
                </div>

                <nav className="nav">
                    <Link to="/">Home</Link>
                    <Link to="/restaurants">Restaurants</Link>
                    <Link to="/lists">Lists</Link>
                    <Link to="/contact">Contact</Link>
                </nav>

                <div className="auth">
                    <Link to="/login" className="login">Login</Link>
                    <Link to="/register" className="signup">Sign up</Link>
                </div>

            </div>
        </header>
    )
}

export default Header