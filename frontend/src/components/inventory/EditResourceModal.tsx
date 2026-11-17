import { useEffect, useState, type FormEvent } from 'react';
import type { Resource, ResourceInput, ResourceStatus } from '@/types';
import { MAX_TERMS } from '@/data/catalog';
import { CONDITION_OPTIONS, STATUS_OPTIONS } from '@/constants/status';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';
import { SelectField, TextField } from '@/components/ui/Fields';
import { useCatalogs } from '@/context/CatalogsContext';
import { useResources } from '@/context/ResourcesContext';

interface EditResourceModalProps {
  open: boolean;
  resource: Resource | null;
  onClose: () => void;
  onSave: (patch: Partial<ResourceInput>) => void;
}

interface EditForm {
  name: string;
  ubicacionId: string;
  category: string;
  condition: string;
  facultadId: string;
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
  const { catalogs } = useCatalogs();
  const { resources } = useResources();
  const [form, setForm] = useState<EditForm | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (!resource) {
      setForm(null);
      return;
    }
    const space = resources.find(
      (item) =>
        item.kind === 'espacio' &&
        (item.name === resource.location ||
          `${item.id} · ${item.name}` === resource.location),
    );
    const ubicacion = catalogs.ubicaciones.find(
      (item) => item.nombre === resource.location,
    );
    const facultad = catalogs.facultades.find(
      (item) => item.nombre === resource.faculty,
    );

    setForm({
      name: resource.name,
      ubicacionId: space?.uuid ?? ubicacion?.id ?? '',
      category: resource.category,
      condition: resource.condition,
      facultadId: facultad?.id ?? '',
      capacity: resource.capacity ? String(resource.capacity) : '',
      maxTerm: resource.maxTerm,
      status: resource.status,
      responsible: resource.responsible,
    });
    setErrors({});
  }, [resource, open, resources, catalogs]);

  if (!resource || !form) {
    return null;
  }

  const isActivo = resource.kind === 'activo';
  const spaces = resources.filter((item) => item.kind === 'espacio' && item.uuid);
  const locationOptions = isActivo
    ? spaces.map((item) => ({
        value: item.uuid as string,
        label: `${item.id} · ${item.name}`,
      }))
    : catalogs.ubicaciones.map((item) => ({ value: item.id, label: item.nombre }));
  const facultyOptions = catalogs.facultades.map((item) => ({
    value: item.id,
    label: item.nombre,
  }));

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
    if (!form.ubicacionId) nextErrors.ubicacionId = 'La ubicación es obligatoria.';
    if (!form.category) nextErrors.category = 'Seleccione una categoría.';

    if (Object.keys(nextErrors).length > 0) {
      setErrors(nextErrors);
      return;
    }

    let locationName = resource.location;
    if (isActivo) {
      const space = spaces.find((item) => item.uuid === form.ubicacionId);
      if (space) locationName = `${space.id} · ${space.name}`;
    } else {
      const ubicacion = catalogs.ubicaciones.find((item) => item.id === form.ubicacionId);
      if (ubicacion) locationName = ubicacion.nombre;
    }
    const facultyLabel = catalogs.facultades.find(
      (item) => item.id === form.facultadId,
    );
    const tipoActivo = catalogs.tiposActivo.find(
      (item) => item.nombre === form.category,
    );

    onSave({
      name: form.name.trim(),
      location: locationName,
      ubicacionId: form.ubicacionId,
      category: form.category,
      condition: form.condition,
      faculty: facultyLabel?.nombre ?? resource.faculty,
      facultadId: form.facultadId || undefined,
      tipoActivoId: isActivo ? tipoActivo?.id : undefined,
      capacity: isActivo ? undefined : Number(form.capacity) || undefined,
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
        <SelectField
          label={isActivo ? 'Espacio / ubicación' : 'Ubicación'}
          required
          value={form.ubicacionId}
          onChange={(value) => setField('ubicacionId', value)}
          options={locationOptions}
          placeholder="Seleccione una ubicación..."
          error={errors.ubicacionId}
          fieldClassName="sm:col-span-2"
        />
        <SelectField
          label={isActivo ? 'Tipo de activo' : 'Categoría del espacio'}
          value={form.category}
          onChange={(value) => setField('category', value)}
          disabled={!isActivo}
          helper={isActivo ? undefined : 'La categoría se define al registrar el espacio.'}
          options={
            isActivo
              ? [
                  ...catalogs.tiposActivo.map((item) => ({
                    value: item.nombre,
                    label: item.nombre,
                  })),
                  ...(catalogs.tiposActivo.some((item) => item.nombre === form.category)
                    ? []
                    : [{ value: form.category, label: form.category }]),
                ]
              : [{ value: form.category, label: form.category }]
          }
          error={errors.category}
        />
        {isActivo ? (
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
        <SelectField
          label="Facultad"
          value={form.facultadId}
          onChange={(value) => setField('facultadId', value)}
          options={facultyOptions}
          placeholder="Seleccione una facultad..."
          fieldClassName="sm:col-span-2"
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
