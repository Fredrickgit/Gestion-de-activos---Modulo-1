import type { ResourceKind } from '@/types';
import {
  ASSET_CATEGORIES,
  FACULTIES,
  MAX_TERMS,
  SPACE_CATEGORIES,
} from '@/data/catalog';
import { CONDITION_OPTIONS } from '@/constants/status';
import { SelectField, TextField } from '@/components/ui/Fields';
import { useCatalogs } from '@/context/CatalogsContext';
import { useResources } from '@/context/ResourcesContext';
import { cn } from '@/lib/cn';

export interface ResourceFormValues {
  id: string;
  name: string;
  location: string;
  category: string;
  condition: string;
  capacity: string;
  maxTerm: string;
  faculty: string;
  responsible: string;
  equipamiento: string[];
}

export type ResourceFormErrors = Partial<Record<keyof ResourceFormValues, string>>;

interface ResourceFormProps {
  kind: ResourceKind;
  values: ResourceFormValues;
  errors: ResourceFormErrors;
  onChange: <K extends keyof ResourceFormValues>(
    key: K,
    value: ResourceFormValues[K],
  ) => void;
}

const LABEL_CLASS = 'text-[17px] font-semibold lg:text-[20px]';

export function ResourceForm({ kind, values, errors, onChange }: ResourceFormProps) {
  const isActivo = kind === 'activo';
  const { catalogs } = useCatalogs();
  const { resources } = useResources();

  const spaces = resources.filter(
    (resource) => resource.kind === 'espacio' && resource.uuid,
  );
  const activos = resources.filter(
    (resource) => resource.kind === 'activo' && resource.uuid,
  );

  const locationOptions = isActivo
    ? spaces.map((space) => ({
        value: space.uuid as string,
        label: `${space.id} · ${space.name}`,
      }))
    : catalogs.ubicaciones.map((item) => ({
        value: item.id,
        label: item.nombre,
      }));

  const facultyOptions = catalogs.facultades.length
    ? catalogs.facultades.map((item) => ({ value: item.id, label: item.nombre }))
    : FACULTIES.map((faculty) => ({ value: faculty, label: faculty }));

  const categoryOptions = isActivo
    ? (catalogs.tiposActivo.length
        ? catalogs.tiposActivo.map((item) => ({ value: item.id, label: item.nombre }))
        : ASSET_CATEGORIES.map((category) => ({ value: category, label: category })))
    : SPACE_CATEGORIES.map((category) => ({ value: category, label: category }));

  const toggleEquipamiento = (uuid: string) => {
    const next = values.equipamiento.includes(uuid)
      ? values.equipamiento.filter((item) => item !== uuid)
      : [...values.equipamiento, uuid];
    onChange('equipamiento', next);
  };

  return (
    <div className="grid gap-x-6 gap-y-5 sm:grid-cols-2">
      <TextField
        label="ID"
        required
        mono
        value={values.id}
        onChange={(value) => onChange('id', value)}
        placeholder={isActivo ? 'ACT-12345' : 'ESP-201A'}
        helper={
          errors.id
            ? undefined
            : isActivo
              ? 'Código de placa único y disponible en base de datos.'
              : 'Código único del espacio y disponible en base de datos.'
        }
        error={errors.id}
        labelClassName={LABEL_CLASS}
        className="h-10 uppercase placeholder:normal-case"
        autoComplete="off"
        spellCheck={false}
      />

      <SelectField
        label={isActivo ? 'Espacio / ubicación' : 'Ubicación'}
        required
        value={values.location}
        onChange={(value) => onChange('location', value)}
        options={locationOptions}
        placeholder={
          isActivo && locationOptions.length === 0
            ? 'Registra primero un espacio...'
            : 'Seleccione una ubicación...'
        }
        helper={
          errors.location
            ? undefined
            : isActivo
              ? 'El activo se asocia a un espacio existente.'
              : 'Ubicación física del espacio.'
        }
        error={errors.location}
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
      />

      <TextField
        label="Nombre descriptivo"
        required
        value={values.name}
        onChange={(value) => onChange('name', value)}
        placeholder={isActivo ? 'Ej. Workstation HP Z2 Tower G9' : 'Ej. Lab. de Robótica'}
        error={errors.name}
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
        autoComplete="off"
      />

      <SelectField
        label={isActivo ? 'Tipo de activo' : 'Categoría del espacio'}
        required
        value={values.category}
        onChange={(value) => onChange('category', value)}
        options={categoryOptions}
        placeholder="Seleccione una categoría..."
        error={errors.category}
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
      />

      {isActivo ? (
        <SelectField
          label="Condición física"
          required
          value={values.condition}
          onChange={(value) => onChange('condition', value)}
          options={CONDITION_OPTIONS.map((condition) => ({
            value: condition,
            label: condition,
          }))}
          error={errors.condition}
          labelClassName={LABEL_CLASS}
          className="h-10 text-sm"
        />
      ) : (
        <TextField
          label="Aforo"
          required
          type="number"
          min={1}
          max={999}
          value={values.capacity}
          onChange={(value) => onChange('capacity', value)}
          placeholder="Número de personas"
          helper={errors.capacity ? undefined : 'Capacidad máxima del espacio en personas.'}
          error={errors.capacity}
          labelClassName={LABEL_CLASS}
          className="h-10 text-sm"
        />
      )}

      <SelectField
        label="Plazo máximo"
        required
        value={values.maxTerm}
        onChange={(value) => onChange('maxTerm', value)}
        options={MAX_TERMS.map((term) => ({ value: term, label: term }))}
        error={errors.maxTerm}
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
      />

      <SelectField
        label="Facultad"
        required={!isActivo}
        value={values.faculty}
        onChange={(value) => onChange('faculty', value)}
        options={facultyOptions}
        placeholder="Seleccione una facultad..."
        helper={
          isActivo
            ? 'Opcional en activos: se hereda del espacio seleccionado.'
            : undefined
        }
        error={errors.faculty}
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
      />

      <TextField
        label="Responsable"
        value={values.responsible}
        onChange={(value) => onChange('responsible', value)}
        placeholder="Opcional · se asigna a 'Sin asignar'"
        labelClassName={LABEL_CLASS}
        className="h-10 text-sm"
        autoComplete="off"
      />

      {!isActivo ? (
        <div className="sm:col-span-2">
          <p className={cn('font-display font-bold text-ink', LABEL_CLASS)}>
            Equipamiento{' '}
            {activos.length > 0 ? <span className="text-danger">*</span> : null}
          </p>
          <p className="mt-1 text-[11px] leading-snug text-helper">
            {activos.length > 0
              ? 'Selecciona los activos que equipan este espacio.'
              : 'Opcional: aún no hay activos registrados. Podrás asociarlos más adelante.'}
          </p>
          {activos.length === 0 ? (
            <p className="mt-3 rounded-lg border border-line bg-soft px-3 py-2 text-xs text-muted">
              No hay activos disponibles para asociar. Puedes registrar el espacio y
              vincular el equipamiento cuando existan activos.
            </p>
          ) : (
            <div className="mt-2 grid max-h-48 gap-2 overflow-y-auto rounded-lg border border-line p-3 sm:grid-cols-2">
              {activos.map((resource) => (
                  <label
                    key={resource.uuid}
                    className="flex cursor-pointer items-start gap-2 rounded-md px-2 py-1.5 text-sm text-ink hover:bg-soft"
                  >
                    <input
                      type="checkbox"
                      checked={values.equipamiento.includes(resource.uuid as string)}
                      onChange={() => toggleEquipamiento(resource.uuid as string)}
                      className="mt-1 h-4 w-4 accent-brand"
                    />
                    <span className="min-w-0">
                      <span className="block font-mono text-[11px] font-bold text-muted">
                        {resource.id}
                      </span>
                      <span className="block truncate">{resource.name}</span>
                    </span>
                  </label>
                ))}
            </div>
          )}
          {errors.equipamiento ? (
            <p className="mt-2 text-[11px] font-medium text-danger">{errors.equipamiento}</p>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
