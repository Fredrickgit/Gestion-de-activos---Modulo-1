import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { InventoryPage } from '@/pages/InventoryPage';
import { RegisterResourcePage } from '@/pages/RegisterResourcePage';
import { ReportsPage } from '@/pages/ReportsPage';

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
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
