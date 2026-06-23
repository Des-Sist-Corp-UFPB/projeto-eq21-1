import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Home    from './pages/Home.jsx'
import Pedido  from './pages/Pedido.jsx'
import Login   from './pages/Login.jsx'
import Admin   from './pages/Admin.jsx'

// Protege a rota /admin — redireciona para /login se não tiver token
function RotaProtegida({ children }) {
  const token = localStorage.getItem('trokets_token')
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"       element={<Home />} />
        <Route path="/pedido" element={<Pedido />} />
        <Route path="/login"  element={<Login />} />
        <Route path="/admin"  element={
          <RotaProtegida><Admin /></RotaProtegida>
        } />
        {/* Redireciona qualquer rota desconhecida para a home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
