import type { ReportExportFormat, ReportFilters, ReportRow } from '@/types';
import { formatDisplayDate } from '@/lib/format';

const HEADERS = [
  'ID Recurso',
  'Nombre del Recurso',
  'Tipo',
  'Ubicación Física',
  'Estado',
  'Aforo / Capacidad',
  'Fecha Registro',
];

function rowValues(row: ReportRow): string[] {
  return [
    row.id,
    row.name,
    row.type,
    row.location,
    statusLabel(row),
    row.capacity,
    formatDisplayDate(row.date),
  ];
}

function statusLabel(row: ReportRow): string {
  const labels: Record<ReportRow['status'], string> = {
    disponible: 'Disponible',
    reservado: 'Reservado',
    'en-uso': 'En uso',
    mantenimiento: 'En mantenimiento',
    bloqueo: 'Bloqueo académico',
  };
  return labels[row.status];
}

function escapeCsv(value: string): string {
  const clean = value.replace(/"/g, '""');
  return `"${clean}"`;
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function download(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}

function fileStamp(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
}

function filterDescription(filters: ReportFilters): string {
  const kind =
    filters.kind === 'activo'
      ? 'Activos físicos'
      : filters.kind === 'espacio'
        ? 'Espacios académicos'
        : 'Todos los recursos';
  return `${kind} · ${formatDisplayDate(filters.from)} - ${formatDisplayDate(filters.to)}`;
}

export function exportReport(
  rows: ReportRow[],
  format: ReportExportFormat,
  filters: ReportFilters,
): void {
  if (format === 'csv') {
    const lines = [
      HEADERS.map(escapeCsv).join(';'),
      ...rows.map((row) => rowValues(row).map(escapeCsv).join(';')),
    ];
    const blob = new Blob([`\uFEFF${lines.join('\r\n')}`], {
      type: 'text/csv;charset=utf-8;',
    });
    download(blob, `reporte-recursos-${fileStamp()}.csv`);
    return;
  }

  if (format === 'excel') {
    const head = HEADERS.map((header) => `<th>${escapeHtml(header)}</th>`).join('');
    const body = rows
      .map((row) => {
        const cells = rowValues(row)
          .map((value) => `<td>${escapeHtml(value)}</td>`)
          .join('');
        return `<tr>${cells}</tr>`;
      })
      .join('');
    const html = `<!doctype html><html lang="es"><head><meta charset="utf-8" /><style>
      body { font-family: Arial, sans-serif; }
      h1 { font-size: 18px; }
      p { font-size: 12px; color: #475569; }
      table { border-collapse: collapse; width: 100%; }
      th { background: #f1f5f9; text-align: left; }
      th, td { border: 1px solid #cbd5e1; padding: 6px 8px; font-size: 12px; }
    </style></head><body>
      <h1>Reporte de recursos universitarios</h1>
      <p>${escapeHtml(filterDescription(filters))} · ${rows.length} resultados</p>
      <table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>
    </body></html>`;
    const blob = new Blob([`\uFEFF${html}`], {
      type: 'application/vnd.ms-excel;charset=utf-8;',
    });
    download(blob, `reporte-recursos-${fileStamp()}.xls`);
    return;
  }

  const win = window.open('', '_blank', 'width=1100,height=800');
  if (!win) return;

  const head = HEADERS.map((header) => `<th>${escapeHtml(header)}</th>`).join('');
  const body = rows
    .map((row) => {
      const cells = rowValues(row)
        .map((value) => `<td>${escapeHtml(value)}</td>`)
        .join('');
      return `<tr>${cells}</tr>`;
    })
    .join('');

  win.document.write(`<!doctype html><html lang="es"><head><meta charset="utf-8" />
    <title>Reporte de recursos universitarios</title>
    <style>
      * { box-sizing: border-box; }
      body { font-family: Arial, Helvetica, sans-serif; color: #1e293b; margin: 32px; }
      header { display: flex; justify-content: space-between; align-items: flex-end; border-bottom: 2px solid #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; }
      h1 { font-size: 20px; margin: 0 0 4px; color: #1e3a8a; }
      p { font-size: 11px; margin: 0; color: #475569; }
      table { border-collapse: collapse; width: 100%; }
      th { background: #f1f5f9; text-align: left; font-size: 10px; text-transform: uppercase; letter-spacing: 0.04em; }
      th, td { border-bottom: 1px solid #e2e8f0; padding: 8px 6px; font-size: 10px; }
      tr:nth-child(even) td { background: #f8fafc; }
      footer { margin-top: 18px; font-size: 10px; color: #64748b; }
      @media print { body { margin: 16mm; } }
    </style></head><body>
      <header>
        <div>
          <h1>Universidad del Magdalena · Gestión de activos</h1>
          <p>Reporte de recursos universitarios</p>
        </div>
        <p>${escapeHtml(filterDescription(filters))}</p>
      </header>
      <table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>
      <footer>${rows.length} resultados encontrados · Generado el ${formatDisplayDate(new Date().toISOString().slice(0, 10))}</footer>
      <script>window.onload = function () { window.print(); };</script>
    </body></html>`);
  win.document.close();
}
