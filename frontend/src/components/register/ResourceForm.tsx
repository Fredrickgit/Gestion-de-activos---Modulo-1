import type { ResourceKind } from '@/types';
import {
  ASSET_CATEGORIES,
  FACULTIES,
  LOCATION_SUGGESTIONS,
  MAX_TERMS,
  SPACE_CATEGORIES,
} from '@/data/catalog';
import { CONDITION_OPTIONS } from '@/constants/status';
import { SelectField, TextField } from '@/components/ui/Fields';

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
        label="Ubicación"
        required
        value={values.location}
        onChange={(value) => onChange('location', value)}
        options={LOCATION_SUGGESTIONS.map((location) => ({
          value: location,
          label: location,
        }))}
        placeholder="Seleccione una ubicación..."
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
        options={(isActivo ? ASSET_CATEGORIES : SPACE_CATEGORIES).map((category) => ({
          value: category,
          label: category,
        }))}
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
        required
        value={values.faculty}
        onChange={(value) => onChange('faculty', value)}
        options={FACULTIES.map((faculty) => ({ value: faculty, label: faculty }))}
        placeholder="Seleccione una facultad..."
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
    </div>
  );
}
