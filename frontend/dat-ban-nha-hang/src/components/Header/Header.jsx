import './Header.css'

function Header() {
    return (
        <header className="header">
            <div className="container">

                <div className="logo">
                    <span>Logo</span>
                </div>

                <nav className="nav">
                    <a href="/">Home</a>
                    <a href="/restaurants">Restaurants</a>
                    <a href="/lists">Lists</a>
                    <a href="/contact">Contact</a>
                </nav>

                <div className="auth">
                    <button className="login">Login</button>
                    <button className="signup">Sign up</button>
                </div>

            </div>
        </header>
    )
}

export default Header