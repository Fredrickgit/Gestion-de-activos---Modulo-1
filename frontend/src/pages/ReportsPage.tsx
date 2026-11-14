import { useMemo, useState } from 'react';
import { FileText } from 'lucide-react';
import type { ReportFilters, ReportRow, ReportSortKey, Resource } from '@/types';
import { initialReportRows } from '@/data/reports';
import { STATUS_META } from '@/constants/status';
import { useResources } from '@/context/ResourcesContext';
import { useToast } from '@/context/ToastContext';
import { formatDisplayDate, isWithinRange } from '@/lib/format';
import { exportReport } from '@/lib/export';
import { PageHeader } from '@/components/layout/PageHeader';
import { ContentSurface } from '@/components/layout/ContentSurface';
import { Button } from '@/components/ui/Button';
import { ReportOptions } from '@/components/reports/ReportOptions';
import { ReportSummary } from '@/components/reports/ReportSummary';
import { ReportTable, type SortDirection } from '@/components/reports/ReportTable';
import { ReportPagination } from '@/components/reports/ReportPagination';

const DEFAULT_FILTERS: ReportFilters = {
  from: '2026-01-01',
  to: '2026-06-30',
  kind: 'todos',
  format: 'pdf',
};

const FORMAT_LABELS: Record<ReportFilters['format'], string> = {
  pdf: 'PDF',
  excel: 'Excel',
  csv: 'CSV',
};

function toReportRow(resource: Resource): ReportRow {
  return {
    id: resource.id,
    name: resource.name,
    type: resource.category,
    location: resource.location,
    status: resource.status,
    capacity:
      resource.kind === 'espacio' && resource.capacity
        ? `${resource.capacity} Estudiantes`
        : 'N/A',
    date: resource.registeredAt,
    kind: resource.kind,
  };
}

function compareRows(a: ReportRow, b: ReportRow, key: ReportSortKey): number {
  if (key === 'date') return a.date.localeCompare(b.date);
  if (key === 'capacity') {
    const aValue = Number.parseInt(a.capacity, 10);
    const bValue = Number.parseInt(b.capacity, 10);
    const aNumber = Number.isNaN(aValue) ? -1 : aValue;
    const bNumber = Number.isNaN(bValue) ? -1 : bValue;
    return aNumber - bNumber;
  }
  if (key === 'status') {
    return STATUS_META[a.status].shortLabel.localeCompare(
      STATUS_META[b.status].shortLabel,
      'es',
    );
  }
  if (key === 'id') return a.id.localeCompare(b.id);
  return a[key].localeCompare(b[key], 'es');
}

export function ReportsPage() {
  const { pushToast } = useToast();
  const { resources } = useResources();
  const [filters, setFilters] = useState<ReportFilters>(DEFAULT_FILTERS);
  const [sortKey, setSortKey] = useState<ReportSortKey | null>(null);
  const [sortDirection, setSortDirection] = useState<SortDirection>('asc');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const reportRows = useMemo(() => {
    const liveById = new Map(resources.map((resource) => [resource.id, resource]));

    const merged = initialReportRows.map((row) => {
      const live = liveById.get(row.id);
      if (!live) return row;
      return {
        ...row,
        name: live.name,
        type: live.category,
        location: live.location,
        status: live.status,
        capacity:
          live.kind === 'espacio' && live.capacity
            ? `${live.capacity} Estudiantes`
            : row.capacity,
      };
    });

    const known = new Set(initialReportRows.map((row) => row.id));
    const added = resources
      .filter((resource) => !known.has(resource.id))
      .map(toReportRow);

    return [...merged, ...added];
  }, [resources]);

  const filteredRows = useMemo(
    () =>
      reportRows.filter((row) => {
        const matchesKind = filters.kind === 'todos' || row.kind === filters.kind;
        const matchesRange = isWithinRange(row.date, filters.from, filters.to);
        return matchesKind && matchesRange;
      }),
    [reportRows, filters.kind, filters.from, filters.to],
  );

  const sortedRows = useMemo(() => {
    if (!sortKey) return filteredRows;
    const rows = [...filteredRows].sort((a, b) => compareRows(a, b, sortKey));
    return sortDirection === 'asc' ? rows : rows.reverse();
  }, [filteredRows, sortKey, sortDirection]);

  const total = sortedRows.length;
  const pageCount = Math.max(1, Math.ceil(total / pageSize));
  const currentPage = Math.min(page, pageCount);
  const visibleRows = sortedRows.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const handleFilterChange = (patch: Partial<ReportFilters>) => {
    setFilters((current) => {
      const next = { ...current, ...patch };
      if (next.from && next.to && next.from > next.to) {
        if (patch.from) next.to = next.from;
        else next.from = next.to;
      }
      return next;
    });
    setPage(1);
  };

  const handleSort = (key: ReportSortKey) => {
    if (sortKey === key) {
      setSortDirection((current) => (current === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDirection('asc');
    }
    setPage(1);
  };

  const handleClear = () => {
    setFilters(DEFAULT_FILTERS);
    setPage(1);
    pushToast({
      variant: 'info',
      title: 'Filtros restablecidos',
      description: `Período ${formatDisplayDate(DEFAULT_FILTERS.from)} - ${formatDisplayDate(DEFAULT_FILTERS.to)} · Todos los recursos.`,
    });
  };

  const handleGenerate = () => {
    if (sortedRows.length === 0) {
      pushToast({
        variant: 'error',
        title: 'Sin datos para exportar',
        description: 'Ajusta el período o el tipo de recurso antes de generar el reporte.',
      });
      return;
    }

    exportReport(sortedRows, filters.format, filters);

    const label = FORMAT_LABELS[filters.format];
    pushToast({
      variant: 'success',
      title: `Reporte en ${label} generado`,
      description:
        filters.format === 'pdf'
          ? `${total} recursos incluidos. Se abrió el diálogo de impresión para guardar el PDF.`
          : `${total} recursos incluidos. La descarga del archivo ${label} comenzó.`,
    });
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setPage(1);
  };

  const canClear =
    filters.from !== DEFAULT_FILTERS.from ||
    filters.to !== DEFAULT_FILTERS.to ||
    filters.kind !== DEFAULT_FILTERS.kind;

  return (
    <>
      <PageHeader title="Generar reporte" className="mt-4 md:mt-[21px]" />

      <div className="mt-3 px-3 pb-8 sm:px-4 lg:mt-[13px] lg:px-[13px] lg:pr-5">
        <ContentSurface className="flex flex-col gap-3">
          <ReportOptions filters={filters} onChange={handleFilterChange} />

          <ReportSummary
            filters={filters}
            count={total}
            canClear={canClear}
            onClear={handleClear}
          />

          <div className="overflow-hidden rounded-xl border border-line bg-white">
            <ReportTable
              rows={visibleRows}
              sortKey={sortKey}
              sortDirection={sortDirection}
              onSort={handleSort}
            />

            <div className="flex flex-col gap-4 border-t border-line p-4 sm:p-5">
              <Button
                variant="navy"
                size="lg"
                onClick={handleGenerate}
                icon={<FileText className="h-4 w-4" aria-hidden />}
                className="w-full self-start font-sans text-[20px] sm:w-auto xl:text-[24px]"
              >
                Generar reporte
              </Button>

              <ReportPagination
                page={currentPage}
                pageCount={pageCount}
                pageSize={pageSize}
                total={total}
                onPageChange={setPage}
                onPageSizeChange={handlePageSizeChange}
              />
            </div>
          </div>
        </ContentSurface>
      </div>
    </>
  );
}
