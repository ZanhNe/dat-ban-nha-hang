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
import UserManagement from "./pages/Admin/UserManagement"
import BroadcastCenter from "./pages/Admin/BroadcastCenter"
import ManagerLayout from "./pages/Manager/ManagerLayout"
import ManagerDashboard from "./pages/Manager/ManagerDashboard"
import RestaurantInfo from "./pages/Manager/RestaurantInfo"
import StaffManagement from "./pages/Manager/StaffManagement"
import MenuManagement from "./pages/Manager/MenuManagement"
import TableManagement from "./pages/Manager/TableManagement"

import BookingRequests from "./pages/Receptionist/BookingRequests"
import ReceptionistLayout from "./pages/Receptionist/ReceptionistLayout"
import CheckIn from "./pages/Receptionist/CheckIn"

import WaiterLayout from "./pages/Waiter/WaiterLayout"
import WaiterDashboard from "./pages/Waiter/WaiterDashboard"
import PosScreen from "./pages/Waiter/PosScreen"


import FoodStatusScreen from "./pages/Waiter/FoodStatusScreen"
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
            <Route path="broadcast" element={<BroadcastCenter />} />
            <Route path="user-management" element={<UserManagement />} />
          </Route>
        </Route>

        <Route path="/manager" element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
          <Route element={<ManagerLayout />}>

            <Route index element={<ManagerDashboard />} />


            <Route path="info" element={<RestaurantInfo />} />
            <Route path="staff" element={<StaffManagement />} />
            <Route path="menu" element={<MenuManagement />} />
            <Route path="tables" element={<TableManagement />} />
          </Route>
        </Route>

        <Route path="/receptionist" element={<ProtectedRoute allowedRoles={["RECEPTIONIST"]} />}>
          <Route element={<ReceptionistLayout />}>
            <Route index element={<BookingRequests />} />
            <Route path="check-in" element={<CheckIn />} />
          </Route>
        </Route>

        <Route path="/waiter" element={<ProtectedRoute allowedRoles={["WAITER"]} />}>
          <Route element={<WaiterLayout />}>
            <Route index element={<WaiterDashboard />} />
            <Route path="pos/:sessionId" element={<PosScreen />} />
            <Route path="orders/:sessionId" element={<FoodStatusScreen />} />
          </Route>
        </Route>

        <Route path="/cashier" element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
          <Route element={<ReceptionistLayout />}>
            <Route index element={<BookingRequests />} />
            <Route path="check-in" element={<CheckIn />} />
          </Route>
        </Route>






      </Routes>
    </BrowserRouter >
  )
}

export default App
