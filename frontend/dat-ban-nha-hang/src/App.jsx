import { BrowserRouter, Routes, Route } from "react-router-dom"
import MapSearchPage from './pages/MapSearchPage'
import Home from './pages/Home/Home'
import Login from "./pages/Auth/Login"
import Register from "./pages/Auth/Register"
import RestaurantDetailPage from './pages/RestaurantDetail/RestaurantDetailPage'
import PendingBookingsPage from './pages/PendingBookings/PendingBookingsPage'
import ProtectedRoute from "./pages/RoutePage/ProtectedRoute"
import PublicRoute from "./pages/RoutePage/PublicRoute"
import HomeCustomer from "./pages/Customer/HomeCustomer"
import { Navigate } from "react-router-dom"
import AdminDashboard from "./pages/Admin/AdminDashboard"
import AdminLayout from "./pages/Admin/AdminLayout"
import RestaurantApprovals from "./pages/Admin/RestaurantApprovals"
import RestaurantManagement from "./pages/Admin/RestaurantManagement"
import CuisineManagement from "./pages/Admin/CuisineManagement"
function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        } />

        <Route path="/register" element={
          <PublicRoute>
            <Register />
          </PublicRoute>
        } />

        <Route path="/" element={<Home />} />

        <Route path="/customer/*" element={<ProtectedRoute allowedRoles={["CUSTOMER"]} />}>
          <Route index element={<HomeCustomer />} />
          <Route path="map-search" element={<MapSearchPage />} />
          <Route path="restaurants/:id" element={<RestaurantDetailPage />} />
          <Route path="bookings/pending-payment" element={<PendingBookingsPage />} />
        </Route>
        <Route path="/admin" element={<ProtectedRoute allowedRoles={["ADMIN"]} />}>
          <Route element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="approvals" element={<RestaurantApprovals />} />
            <Route path="restaurants" element={<RestaurantManagement />} />
            <Route path="cuisines" element={<CuisineManagement />} />
            <Route path="broadcast" element={<div>Broadcast</div>} />
          </Route>
        </Route>






      </Routes>
    </BrowserRouter >
  )
}

export default App
