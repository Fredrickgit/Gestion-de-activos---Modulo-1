export const ASSET_CATEGORIES = [
  'Cómputo Alto Rendimiento',
  'Cómputo Universitario',
  'Audiovisual Producción',
  'Multimedia',
  'Equipo Técnico',
  'Equipo de Laboratorio',
  'Laboratorio Clínico',
  'Mobiliario Universitario',
];

export const SPACE_CATEGORIES = [
  'Laboratorio Académico',
  'Aula Teórica',
  'Aula Especial',
  'Sala Especial',
  'Sala Tecnológica',
  'Auditorio',
];

export const FACULTIES = [
  'Ingeniería',
  'Ciencias Básicas',
  'Humanidades',
  'Ciencias de la Salud',
  'Ciencias Empresariales',
  'General',
];

export const MAX_TERMS = ['1 día', '2 días', '3 días', '5 días', '7 días', '15 días'];

export const LOCATION_SUGGESTIONS = [
  'ESP-114 Lab. Modelado y Simulación',
  'ESP-433 Salon de Teatro y Diseño 2',
  'ESP-525 Edificio de aulas Mar Caribe',
  'Bloque Ciencias, Piso 1',
  'Bloque Sierra Nevada, Piso 2',
  'Biblioteca Central Germán Bula',
  'Auditorio Roque Morelli',
  'Bloque de Ingenierías, Terraza',
];

export const REPORT_TYPES = [
  { value: 'todos', label: 'Todos los recursos' },
  { value: 'activo', label: 'Activos físicos' },
  { value: 'espacio', label: 'Espacios académicos' },
] as const;
