import { useEffect, useState, type FormEvent } from 'react';
import type { Resource, ResourceInput, ResourceStatus } from '@/types';
import { ASSET_CATEGORIES, MAX_TERMS, SPACE_CATEGORIES } from '@/data/catalog';
import { CONDITION_OPTIONS, STATUS_OPTIONS } from '@/constants/status';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';
import { SelectField, TextField } from '@/components/ui/Fields';

interface EditResourceModalProps {
  open: boolean;
  resource: Resource | null;
  onClose: () => void;
  onSave: (patch: Partial<ResourceInput>) => void;
}

interface EditForm {
  name: string;
  location: string;
  category: string;
  condition: string;
  faculty: string;
  capacity: string;
  maxTerm: string;
  status: ResourceStatus;
  responsible: string;
}

export function EditResourceModal({
  open,
  resource,
  onClose,
  onSave,
}: EditResourceModalProps) {
  const [form, setForm] = useState<EditForm | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!resource) {
      setForm(null);
      return;
    }
    setForm({
      name: resource.name,
      location: resource.location,
      category: resource.category,
      condition: resource.condition,
      faculty: resource.faculty,
      capacity: resource.capacity ? String(resource.capacity) : '',
      maxTerm: resource.maxTerm,
      status: resource.status,
      responsible: resource.responsible,
    });
    setErrors({});
  }, [resource, open]);

  if (!resource || !form) {
    return null;
  }

  const setField = <K extends keyof EditForm>(key: K, value: EditForm[K]) => {
    setForm((current) => (current ? { ...current, [key]: value } : current));
    setErrors((current) => {
      if (!current[key]) return current;
      const next = { ...current };
      delete next[key];
      return next;
    });
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const nextErrors: Record<string, string> = {};

    if (!form.name.trim()) nextErrors.name = 'El nombre del recurso es obligatorio.';
    if (!form.location.trim()) nextErrors.location = 'La ubicación es obligatoria.';
    if (!form.category) nextErrors.category = 'Seleccione una categoría.';

    if (Object.keys(nextErrors).length > 0) {
      setErrors(nextErrors);
      return;
    }

    onSave({
      name: form.name.trim(),
      location: form.location.trim(),
      category: form.category,
      condition: form.condition,
      faculty: form.faculty,
      capacity: resource.kind === 'espacio' ? Number(form.capacity) || undefined : undefined,
      maxTerm: form.maxTerm,
      status: form.status,
      responsible: form.responsible.trim() || 'Sin asignar',
    });
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Editar recurso"
      description={`Actualiza la ficha de ${resource.id}. Los cambios quedarán registrados en el historial.`}
      size="lg"
      footer={
        <>
          <Button variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit" form="edit-resource-form">
            Guardar cambios
          </Button>
        </>
      }
    >
      <form id="edit-resource-form" onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
        <TextField
          label="Nombre descriptivo"
          required
          value={form.name}
          onChange={(value) => setField('name', value)}
          error={errors.name}
          fieldClassName="sm:col-span-2"
          placeholder="Nombre del recurso"
        />
        <TextField
          label="Ubicación"
          required
          value={form.location}
          onChange={(value) => setField('location', value)}
          error={errors.location}
          fieldClassName="sm:col-span-2"
        />
        <SelectField
          label={resource.kind === 'activo' ? 'Tipo de activo' : 'Categoría del espacio'}
          required
          value={form.category}
          onChange={(value) => setField('category', value)}
          options={(resource.kind === 'activo' ? ASSET_CATEGORIES : SPACE_CATEGORIES).map(
            (category) => ({ value: category, label: category }),
          )}
          error={errors.category}
        />
        {resource.kind === 'activo' ? (
          <SelectField
            label="Condición física"
            value={form.condition}
            onChange={(value) => setField('condition', value)}
            options={CONDITION_OPTIONS.map((option) => ({ value: option, label: option }))}
          />
        ) : (
          <TextField
            label="Aforo"
            type="number"
            min={1}
            max={999}
            value={form.capacity}
            onChange={(value) => setField('capacity', value)}
            placeholder="Número de personas"
          />
        )}
        <SelectField
          label="Plazo máximo"
          value={form.maxTerm}
          onChange={(value) => setField('maxTerm', value)}
          options={MAX_TERMS.map((term) => ({ value: term, label: term }))}
        />
        <SelectField
          label="Estado"
          value={form.status}
          onChange={(value) => setField('status', value as ResourceStatus)}
          options={STATUS_OPTIONS}
        />
        <TextField
          label="Responsable"
          value={form.responsible}
          onChange={(value) => setField('responsible', value)}
          fieldClassName="sm:col-span-2"
          placeholder="Nombre del responsable o 'Sin asignar'"
        />
      </form>
    </Modal>
  );
}
