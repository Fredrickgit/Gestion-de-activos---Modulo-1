import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { useAuth } from '@/context/AuthContext';
import { InventoryPage } from '@/pages/InventoryPage';
import { LoginPage } from '@/pages/LoginPage';
import { RegisterResourcePage } from '@/pages/RegisterResourcePage';
import { ReportsPage } from '@/pages/ReportsPage';

function ProtectedLayout() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: `${location.pathname}${location.search}` }}
      />
    );
  }

  return <AppLayout />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedLayout />}>
        <Route path="/" element={<Navigate to="/inventario" replace />} />
        <Route path="/inventario" element={<InventoryPage tab="todos" />} />
        <Route path="/inventario/activos" element={<InventoryPage tab="activo" />} />
        <Route path="/inventario/espacios" element={<InventoryPage tab="espacio" />} />
        <Route path="/registrar" element={<RegisterResourcePage />} />
        <Route path="/reportes" element={<ReportsPage />} />
        <Route path="*" element={<Navigate to="/inventario" replace />} />
      </Route>
    </Routes>
  );
}
