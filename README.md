# Clinica ATL (Android - Jetpack Compose)

Aplicación móvil para pacientes, doctores y administradores de la Clínica ATL. Incluye gestión de citas, seguros, perfiles y administración de personal.

## Funcionalidades clave (por rol)
- **Paciente**: home con clima (Open‑Meteo) y carrusel de doctores populares; contratación/cancelación de seguros con beneficiarios; agenda de citas (buscar por especialidad, reservar, ver/cancelar); perfil con edición de datos, contraseña y foto.
- **Doctor**: agenda propia (ver/cancelar citas), búsqueda de pacientes, vista de perfil de paciente (seguros, historial y citas con el doctor), perfil propio (teléfono, contraseña, foto), métricas mensuales.
- **Administrador**: CRUD de especialidades; alta de doctores (crea usuario + ficha doctor), edición/baja y listado; perfil de administrador (datos y foto).
- **UX**: tema claro/oscuro/sistema persistido en DataStore; splash; top bar y drawer según ruta/rol; notificaciones para citas, seguros y creación de doctores (POST_NOTIFICATIONS en Android 13+).

## Arquitectura y datos
- **UI**: Jetpack Compose + Navigation; pantallas en `ui/screen/**`. Estados con ViewModel + StateFlow (factories manuales).
- **Red**: Retrofit + OkHttp + Gson. `AuthInterceptor` añade JWT de `UserPreferences`. APIs: `UsuariosApi`, `SegurosApi`, `CitasApi`, `HistorialesApi`, `WeatherApi` apuntando a túneles `*.devtunnels.ms` (ajustar por entorno).
- **Repositorios**: capa en `data/repository/**` (coroutines + Dispatchers.IO). Manejan mapeos y normalización (roles, especialidades, fechas ISO).
- **Almacenamiento**: DataStore Preferences (id usuario, rol normalizado, idDoctor, token, tema, cache de fotos).
- **Imágenes**: Coil con header Authorization, utilitario `ImageFileUtils` para comprimir y bust de cache `?ts=`.
- **Validaciones**: email, contraseña fuerte, teléfono chileno, fechas `dd-MM-aaaa`, nombres; helpers en `domain/validation`.

## Flujos sensibles
- **Recuperación de contraseña**: verificación por correo + fecha de nacimiento; busca en pacientes y doctores; luego permite actualizar contraseña del usuario recuperado.
- **Doctores populares**: se normaliza rol y especialidad; si el backend no envía `especialidad`, se resuelve con `idEspecialidad` vs catálogo de especialidades antes de pintar el carrusel.
- **Fechas**: formularios de doctores usan entrada `dd-MM-aaaa` (se formatea y se envía al backend en ISO `yyyy-MM-dd`).

## Navegación (rutas)
- Auth: `login`, `register`, `password_recovery_verify`, `password_recovery_change`.
- Paciente: `home`, `patient_profile`, `patient_change_password`, `seguros`, `contratarSeguro/{id}`, `book_appointment`.
- Doctor: `doctor_menu`, `doctor_schedule`, `doctor_search_patient`, `doctor_patient_profile/{id}`, `doctor_profile/{id}`, `doctor_preview/{id}`.
- Admin: `admin_menu`, `admin_add_specialty`, `admin_add_doctor`, `admin_view_doctors`, `admin_profile`, `admin_edit_doctor/{id}`.
- Utilidad: `restart_app` (solo debugging; preferir logout).

## APIs principales
- `UsuariosApi`: login/register, CRUD usuarios/doctores/admin, especialidades, fotos de perfil, cambio de contraseña admin.
- `CitasApi`: citas por usuario/doctor/fecha (crear/cancelar/actualizar).
- `SegurosApi`: seguros y contratos (crear, cancelar, listar).
- `HistorialesApi`: historiales por usuario/doctor.
- `WeatherApi`: clima actual.

## Estructura
- `navigation/` NavHost y rutas.
- `ui/screen/**` pantallas; `ui/viewmodel/**` viewmodels + factories.
- `data/remote/**` DTOs y Retrofit.
- `data/repository/**` lógica de red/datos.
- `data/local/storage/` DataStore.
- `util/` utilitarios generales.

## Build y pruebas
- Build: `./gradlew assembleDebug` (ajustar `compileSdk/targetSdk/Compose` según tu entorno).
- Tests: `./gradlew test` (validaciones y algunos ViewModel). Deshabilitar `HttpLoggingInterceptor.Level.BODY` en release.
