import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-react';
import { cn } from '@/lib/cn';

interface ReportPaginationProps {
  page: number;
  pageCount: number;
  pageSize: number;
  total: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
}

const PAGE_SIZES = [10, 25, 50];

type PageItem = number | 'ellipsis';

function getPageItems(page: number, pageCount: number): PageItem[] {
  if (pageCount <= 7) {
    return Array.from({ length: pageCount }, (_, index) => index + 1);
  }

  const items: PageItem[] = [];
  const pages = new Set<number>([1, pageCount, page - 1, page, page + 1]);
  const sorted = [...pages].filter((value) => value >= 1 && value <= pageCount).sort((a, b) => a - b);

  sorted.forEach((value, index) => {
    if (index > 0 && value - sorted[index - 1] > 1) {
      items.push('ellipsis');
    }
    items.push(value);
  });

  return items;
}

const NAV_BUTTON =
  'inline-flex h-8 w-8 items-center justify-center rounded-md border border-line bg-white text-muted transition-colors hover:bg-soft hover:text-ink disabled:pointer-events-none disabled:opacity-40';

export function ReportPagination({
  page,
  pageCount,
  pageSize,
  total,
  onPageChange,
  onPageSizeChange,
}: ReportPaginationProps) {
  const start = total === 0 ? 0 : (page - 1) * pageSize + 1;
  const end = Math.min(page * pageSize, total);
  const items = getPageItems(page, pageCount);

  return (
    <div className="flex flex-col gap-3">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <p className="text-[13px] text-muted">
          Mostrando <span className="font-medium text-ink">{start}</span>-
          <span className="font-medium text-ink">{end}</span> de{' '}
          <span className="font-medium text-ink">{total}</span>{' '}
          {total === 1 ? 'recurso' : 'recursos'}
        </p>

        <label className="flex h-8 items-center gap-2 rounded-md border border-line bg-white px-2.5 text-[13px]">
          <span className="font-semibold text-ink">Filas por página:</span>
          <select
            value={pageSize}
            onChange={(event) => onPageSizeChange(Number(event.target.value))}
            aria-label="Filas por página"
            className="cursor-pointer appearance-none bg-transparent text-[13px] text-muted outline-none"
          >
            {PAGE_SIZES.map((size) => (
              <option key={size} value={size}>
                {size}
              </option>
            ))}
          </select>
          <ChevronRight className="h-3.5 w-3.5 rotate-90 text-muted" aria-hidden />
        </label>
      </div>

      <nav
        aria-label="Paginación del reporte"
        className="flex flex-wrap items-center gap-1.5"
      >
        <button
          type="button"
          onClick={() => onPageChange(1)}
          disabled={page <= 1}
          aria-label="Primera página"
          className={NAV_BUTTON}
        >
          <ChevronsLeft className="h-4 w-4" aria-hidden />
        </button>
        <button
          type="button"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 1}
          aria-label="Página anterior"
          className={NAV_BUTTON}
        >
          <ChevronLeft className="h-4 w-4" aria-hidden />
        </button>

        <div className="flex items-center gap-1.5">
          {items.map((item, index) =>
            item === 'ellipsis' ? (
              <span
                key={`ellipsis-${index}`}
                className="inline-flex h-8 w-8 items-center justify-center text-[13px] text-muted"
                aria-hidden
              >
                …
              </span>
            ) : (
              <button
                key={item}
                type="button"
                onClick={() => onPageChange(item)}
                aria-label={`Página ${item}`}
                aria-current={item === page ? 'page' : undefined}
                className={cn(
                  'inline-flex h-8 min-w-8 items-center justify-center rounded-md border px-2 text-[13px] transition-colors',
                  item === page
                    ? 'border-brand/30 bg-active-soft font-semibold text-brand'
                    : 'border-line bg-white text-ink hover:bg-soft',
                )}
              >
                {item}
              </button>
            ),
          )}
        </div>

        <button
          type="button"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= pageCount}
          aria-label="Página siguiente"
          className={NAV_BUTTON}
        >
          <ChevronRight className="h-4 w-4" aria-hidden />
        </button>
        <button
          type="button"
          onClick={() => onPageChange(pageCount)}
          disabled={page >= pageCount}
          aria-label="Última página"
          className={NAV_BUTTON}
        >
          <ChevronsRight className="h-4 w-4" aria-hidden />
        </button>
      </nav>
    </div>
  );
}
