import { useState } from "react"
import "./Login.css"

function Login() {
    const [isShowPassword, setIsShowPassword] = useState(false)
    const [password, setPassword] = useState("")
    const [username, setUsername] = useState("")
    const [errMessage, setErrMessage] = useState("")

    const handleLogin = () => {
        if (!username || !password) {
            setErrMessage("Vui lòng nhập đầy đủ thông tin")
            return
        }

        console.log("Login:", username, password)
        setErrMessage("")
    }

    return (
        <div className='login-background'>
            <div className='login-container'>
                <div className='login-content'>
                    <h2 className='title'>Đăng nhập</h2>

                    <div className='form-group'>
                        <label>Tài khoản</label>
                        <input
                            type="text"
                            placeholder="Nhập username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>

                    <div className='form-group'>
                        <label>Password:</label>

                        <div className='custom-input-password'>
                            <input
                                type={isShowPassword ? 'text' : 'password'}
                                placeholder="Nhập mật khẩu"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />

                            <span onClick={() => setIsShowPassword(!isShowPassword)}>
                                <i className={isShowPassword ? "fa-solid fa-eye" : "fa-solid fa-eye-slash"}></i>
                            </span>
                        </div>
                    </div>

                    <div style={{ color: "red" }}>
                        {errMessage}
                    </div>

                    <button className='btn-login' onClick={handleLogin}>
                        Login
                    </button>

                    <div>
                        <span className='forgot-password'>Quên mật khẩu?</span>
                    </div>

                    <div>
                        <span className='text-center'>Or Login with:</span>
                    </div>

                    <div className='social-login'>
                        <i className="fa-brands fa-google"></i>
                        <i className="fa-brands fa-facebook"></i>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login