import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { Database, Info } from 'lucide-react';
import type { ResourceKind } from '@/types';
import { useResources } from '@/context/ResourcesContext';
import { useToast } from '@/context/ToastContext';
import { PageHeader } from '@/components/layout/PageHeader';
import { ContentSurface } from '@/components/layout/ContentSurface';
import { Button } from '@/components/ui/Button';
import {
  ResourceForm,
  type ResourceFormErrors,
  type ResourceFormValues,
} from '@/components/register/ResourceForm';
import { ResourceTypeSelector } from '@/components/register/ResourceTypeSelector';

const ID_PATTERN = /^(ACT|ESP)-[A-Z0-9]{3,6}$/;

function initialValues(): ResourceFormValues {
  return {
    id: '',
    name: '',
    location: '',
    category: '',
    condition: 'Bueno',
    capacity: '',
    maxTerm: '1 día',
    faculty: '',
    responsible: '',
  };
}

export function RegisterResourcePage() {
  const { resources, addResource } = useResources();
  const { pushToast } = useToast();
  const navigate = useNavigate();

  const [kind, setKind] = useState<ResourceKind>('activo');
  const [values, setValues] = useState<ResourceFormValues>(() => initialValues());
  const [errors, setErrors] = useState<ResourceFormErrors>({});

  const handleKindChange = (nextKind: ResourceKind) => {
    if (nextKind === kind) return;
    setKind(nextKind);
    setValues((current) => ({
      ...current,
      id: '',
      category: '',
      capacity: '',
    }));
    setErrors({});
  };

  const handleChange = <K extends keyof ResourceFormValues>(
    key: K,
    value: ResourceFormValues[K],
  ) => {
    setValues((current) => ({
      ...current,
      [key]: key === 'id' ? String(value).toUpperCase() : value,
    }));
    setErrors((current) => {
      if (!current[key]) return current;
      const next = { ...current };
      delete next[key];
      return next;
    });
  };

  const validate = (): ResourceFormErrors => {
    const next: ResourceFormErrors = {};
    const id = values.id.trim().toUpperCase();
    const expectedPrefix = kind === 'activo' ? 'ACT-' : 'ESP-';

    if (!id) {
      next.id = 'El ID del recurso es obligatorio.';
    } else if (!ID_PATTERN.test(id) || !id.startsWith(expectedPrefix)) {
      next.id =
        kind === 'activo'
          ? 'Usa el formato ACT-12345 (ACT- + números).'
          : 'Usa el formato ESP-201A (ESP- + números o letras).';
    } else if (resources.some((resource) => resource.id === id)) {
      next.id = `El ID ${id} ya está registrado en el inventario.`;
    }

    if (!values.name.trim()) {
      next.name = 'El nombre descriptivo es obligatorio.';
    } else if (values.name.trim().length < 3) {
      next.name = 'El nombre debe tener al menos 3 caracteres.';
    }

    if (!values.location.trim()) next.location = 'Selecciona una ubicación.';
    if (!values.category) {
      next.category =
        kind === 'activo' ? 'Selecciona el tipo de activo.' : 'Selecciona la categoría del espacio.';
    }

    if (kind === 'activo') {
      if (!values.condition) next.condition = 'Selecciona la condición física.';
    } else {
      const capacity = Number(values.capacity);
      if (!values.capacity.trim() || !Number.isInteger(capacity)) {
        next.capacity = 'Ingresa el aforo del espacio.';
      } else if (capacity < 1 || capacity > 999) {
        next.capacity = 'El aforo debe estar entre 1 y 999 personas.';
      }
    }

    if (!values.maxTerm) next.maxTerm = 'Selecciona un plazo máximo.';
    if (!values.faculty) next.faculty = 'Selecciona la facultad responsable.';

    return next;
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0) {
      pushToast({
        variant: 'error',
        title: 'Revisa el formulario',
        description: 'Hay campos obligatorios pendientes o con información inválida.',
      });
      return;
    }

    const resource = addResource({
      id: values.id.trim().toUpperCase(),
      name: values.name.trim(),
      kind,
      category: values.category,
      location: values.location,
      faculty: values.faculty,
      condition: kind === 'activo' ? values.condition : 'Bueno',
      capacity: kind === 'espacio' ? Number(values.capacity) : undefined,
      maxTerm: values.maxTerm,
      status: 'disponible',
      responsible: values.responsible.trim() || 'Sin asignar',
    });

    pushToast({
      variant: 'success',
      title: 'Recurso creado',
      description: `${resource.name} (${resource.id}) se registró en estado Disponible.`,
    });

    navigate('/inventario', { state: { createdId: resource.id } });
  };

  return (
    <>
      <PageHeader
        title="Registrar nuevo recurso universitario"
        className="mt-4 md:mt-[21px]"
      />

      <div className="mt-3 px-3 pb-8 sm:px-4 lg:mt-[13px] lg:px-[13px] lg:pr-5">
        <ContentSurface>
          <form onSubmit={handleSubmit} noValidate>
            <div className="rounded-2xl border border-line bg-white p-4 sm:p-6">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div className="min-w-0">
                  <h2 className="font-display text-[24px] font-semibold leading-tight text-black sm:text-[28px] lg:text-[32px]">
                    Tipo de recurso
                  </h2>
                  <p className="mt-1 text-[13px] leading-snug text-muted">
                    Define si registrarás un activo físico o un espacio académico.
                  </p>
                </div>
                <Button
                  type="submit"
                  size="lg"
                  icon={<Database className="h-[18px] w-[18px]" aria-hidden />}
                  className="hidden font-sans text-[20px] md:inline-flex xl:text-[24px]"
                >
                  Crear Recurso
                </Button>
              </div>

              <div className="mt-5">
                <ResourceTypeSelector value={kind} onChange={handleKindChange} />
              </div>

              <div className="my-6 h-px bg-line" aria-hidden />

              <ResourceForm
                kind={kind}
                values={values}
                errors={errors}
                onChange={handleChange}
              />

              <div className="mt-6 flex items-start gap-2 rounded-lg bg-soft px-3.5 py-3 text-[12px] leading-snug text-muted">
                <Info className="mt-px h-4 w-4 shrink-0 text-brand" aria-hidden />
                <p>
                  El recurso se registrará con estado <strong className="text-ink">Disponible</strong>{' '}
                  y podrás cambiar su estado, editarlo o consultar su historial desde el
                  inventario.
                </p>
              </div>

              <div className="mt-5 flex flex-col-reverse gap-2.5 md:hidden">
                <Button variant="outline" onClick={() => navigate('/inventario')}>
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  size="md"
                  icon={<Database className="h-4 w-4" aria-hidden />}
                  fullWidth
                >
                  Crear Recurso
                </Button>
              </div>
            </div>
          </form>
        </ContentSurface>
      </div>
    </>
  );
}
