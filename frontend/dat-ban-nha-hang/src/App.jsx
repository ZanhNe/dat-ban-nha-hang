import { useState } from 'react'
import { BrowserRouter, Routes, Route } from "react-router-dom"
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
// import './App.css'
import MapSearchPage from './pages/MapSearchPage'
import Home from './pages/Home/Home'
import Login from "./pages/Auth/Login"
import Register from "./pages/Auth/Register"
import RestaurantDetailPage from './pages/RestaurantDetail/RestaurantDetailPage'
import PendingBookingsPage from './pages/PendingBookings/PendingBookingsPage'

function App() {
  const [count, setCount] = useState(0)

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/map-search" element={<MapSearchPage />} />
        <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
        <Route path="/bookings/pending-payment" element={<PendingBookingsPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
