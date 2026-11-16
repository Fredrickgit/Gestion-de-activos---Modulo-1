import { useId, type InputHTMLAttributes, type ReactNode, type SelectHTMLAttributes } from 'react';
import { ChevronDown, CircleCheck, CircleAlert } from 'lucide-react';
import { cn } from '@/lib/cn';

interface FieldShellProps {
  id: string;
  label: string;
  required?: boolean;
  helper?: ReactNode;
  error?: string;
  children: ReactNode;
  className?: string;
  labelClassName?: string;
}

function FieldShell({
  id,
  label,
  required,
  helper,
  error,
  children,
  className,
  labelClassName,
}: FieldShellProps) {
  return (
    <div className={cn('flex min-w-0 flex-col gap-2', className)}>
      <label
        htmlFor={id}
        className={cn(
          'font-display text-[15px] font-bold leading-none text-ink lg:text-[17px]',
          labelClassName,
        )}
      >
        {label}
        {required ? <span className="ml-0.5 text-danger">*</span> : null}
      </label>
      {children}
      {error ? (
        <p className="flex items-center gap-1.5 text-[11px] font-medium text-danger">
          <CircleAlert className="h-3.5 w-3.5" aria-hidden />
          {error}
        </p>
      ) : helper ? (
        <div className="flex items-start gap-1.5 text-[11px] leading-snug text-helper">
          <CircleCheck className="mt-px h-3.5 w-3.5 shrink-0" aria-hidden />
          <span>{helper}</span>
        </div>
      ) : null}
    </div>
  );
}

const INPUT_CLASSES =
  'h-11 w-full rounded-lg border border-line bg-white px-3 text-sm text-ink outline-none transition-colors placeholder:text-muted/70 focus:border-brand/50 focus:ring-2 focus:ring-brand/10 disabled:bg-soft disabled:text-muted';

export interface TextFieldProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'value'> {
  label: string;
  value: string;
  onChange: (value: string) => void;
  helper?: ReactNode;
  error?: string;
  mono?: boolean;
  fieldClassName?: string;
  labelClassName?: string;
  hideLabel?: boolean;
}

export function TextField({
  label,
  value,
  onChange,
  helper,
  error,
  mono = false,
  fieldClassName,
  labelClassName,
  hideLabel = false,
  id,
  required,
  className,
  ...props
}: TextFieldProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <FieldShell
      id={inputId}
      label={label}
      required={required}
      helper={helper}
      error={error}
      className={cn(hideLabel && 'gap-0', fieldClassName)}
      labelClassName={labelClassName}
    >
      {hideLabel ? (
        <label htmlFor={inputId} className="sr-only">
          {label}
        </label>
      ) : null}
      <input
        id={inputId}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        aria-invalid={Boolean(error)}
        className={cn(
          INPUT_CLASSES,
          mono && 'font-mono text-sm font-bold',
          error && 'border-danger/60 focus:border-danger focus:ring-danger/10',
          className,
        )}
        {...props}
      />
    </FieldShell>
  );
}

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectFieldProps
  extends Omit<SelectHTMLAttributes<HTMLSelectElement>, 'onChange' | 'value'> {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: SelectOption[];
  helper?: ReactNode;
  error?: string;
  fieldClassName?: string;
  labelClassName?: string;
  hideLabel?: boolean;
  placeholder?: string;
}

export function SelectField({
  label,
  value,
  onChange,
  options,
  helper,
  error,
  fieldClassName,
  labelClassName,
  hideLabel = false,
  placeholder,
  id,
  required,
  className,
  ...props
}: SelectFieldProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <FieldShell
      id={inputId}
      label={label}
      required={required}
      helper={helper}
      error={error}
      className={cn(hideLabel && 'gap-0', fieldClassName)}
      labelClassName={labelClassName}
    >
      {hideLabel ? (
        <label htmlFor={inputId} className="sr-only">
          {label}
        </label>
      ) : null}
      <div className="relative">
        <select
          id={inputId}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          aria-invalid={Boolean(error)}
          className={cn(
            INPUT_CLASSES,
            'cursor-pointer appearance-none pr-9',
            error && 'border-danger/60 focus:border-danger focus:ring-danger/10',
            className,
          )}
          {...props}
        >
          {placeholder ? (
            <option value="" disabled>
              {placeholder}
            </option>
          ) : null}
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        <ChevronDown
          className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-ink/70"
          aria-hidden
        />
      </div>
    </FieldShell>
  );
}
