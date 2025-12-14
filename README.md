# Clinica ATL (Android - Jetpack Compose)

Aplicacion movil que centraliza la experiencia de pacientes, doctores y administradores de la Clinica ATL.

## Funcionalidades por rol
- Paciente
  - Home con clima (Open-Meteo), carrusel de doctores y accesos directos.
  - Perfil: edicion de datos, cambio de contrasena, foto de perfil (DataStore + upload con JWT).
  - Seguros: listado, contratacion con beneficiarios, cancelacion, vista de seguro activo.
  - Citas: buscar doctor por especialidad, ver disponibilidad por fecha, reservar, ver y cancelar citas.
- Doctor
  - Menu con agenda y busqueda de pacientes.
  - Agenda: proximas citas (filtra canceladas/disponibles), cancelar cita.
  - Perfil de paciente: datos, seguros, historial, citas con el doctor; cancelar o finalizar cita.
  - Perfil propio: telefono, contrasena, foto; estadisticas mensuales de atencion.
- Administrador
  - Especialidades: listar, crear, editar, eliminar.
  - Doctores: alta (crea usuario + ficha doctor), listado, edicion, baja.
  - Perfil admin: datos y foto.
- Temas y UX: tema claro/oscuro/sistema persistido en DataStore; splash; top bar condicionado por ruta; drawer para paciente.
- Notificaciones: canales para citas, seguros y creacion de doctores (POST_NOTIFICATIONS en Android 13+).

## Arquitectura y flujo de datos
- UI: Jetpack Compose + Navigation Compose. Pantallas por rol en `ui/screen/**`.
- Estado: ViewModels con StateFlow; factories manuales en `MainActivity` (sin DI).
- Datos y red:
  - Retrofit + OkHttp + Gson; `AuthInterceptor` agrega JWT desde `UserPreferences`.
  - `RetrofitClient` expone `UsuariosApi`, `SegurosApi`, `CitasApi`, `HistorialesApi`, `WeatherApi`. Endpoints apuntan a tuneles `*.devtunnels.ms` (configurar por entorno).
  - Repositorios por dominio (`data/repository/**`), todos IO con coroutines.
  - DataStore Preferences: id de usuario, rol normalizado, idDoctor, token JWT, tema, cache de url de fotos.
  - Imagenes: Coil con header Authorization; `ImageFileUtils` comprime <1 MB y agrega `?ts=` para bust de cache.
- Validaciones: `domain/validation` (email, password fuerte, telefono chileno, fechas dd-MM-aaaa, nombres).

## Navegacion (Routes principales)
- Autenticacion: `login`, `register`, `password_recovery_verify`, `password_recovery_change`.
- Paciente: `home`, `patient_profile`, `patient_change_password`, `seguros`, `contratarSeguro/{id}`, `book_appointment`.
- Doctor: `doctor_menu`, `doctor_schedule`, `doctor_search_patient`, `doctor_patient_profile/{id}`, `doctor_profile/{id}`, `doctor_preview/{id}`.
- Admin: `admin_menu`, `admin_add_specialty`, `admin_add_doctor`, `admin_view_doctors`, `admin_profile`, `admin_edit_doctor/{id}`.
- Utilidad: `restart_app` (evitar en produccion; preferir logout limpio).

## API y modelos
- UsuariosApi: login/register, CRUD usuarios/doctores, especialidades, fotos de perfil, cambio de contrasena admin.
- CitasApi: citas por usuario/doctor/fecha, reservar, cancelar, actualizar.
- SegurosApi: seguros, contratos (crear, cancelar, listar por usuario/seguro).
- HistorialesApi: historiales por usuario o doctor.
- WeatherApi: clima actual.
- DTOs en `data/remote/dto/**` (mappers a `UsuarioDto`, `DoctorDto`, etc.).

## Consideraciones y buenas practicas
- Llamar `RetrofitClient.configure(userPreferences)` antes de usar APIs para inyectar el JWT.
- Ajustar `compileSdk/targetSdk` y versiones de AGP/Compose a valores reales (revisar `build.gradle.kts` y `libs.versions.toml`).
- Separar URLs por entorno y desactivar `HttpLoggingInterceptor.Level.BODY` en release.
- `subscribeToInsurance` en `SegurosRepositoryImpl` esta marcado como pendiente; no usar en UI hasta implementarlo.
- Estandarizar el POST de doctores: existe mas de un `@POST("doctores")` en `UsuariosApi`; usar un unico contrato (p.ej. `DoctorCreateRequestDto`) segun backend.

## Estructura de carpetas (clave)
- `navigation/` rutas y NavHost.
- `ui/screen/**` pantallas por rol.
- `ui/viewmodel/**` ViewModels y factories.
- `data/repository/**` repositorios remotos por dominio.
- `data/remote/**` Retrofit interfaces y DTOs.
- `data/local/storage/UserPreferences.kt` DataStore (sesion/tema/token).
- `util/ImageFileUtils.kt` manejo de imagenes locales antes de subir.

## Build y pruebas
- Build: `./gradlew assembleDebug` (ajustar SDK/AGP/Compose antes).
- Tests locales: `./gradlew test` (validaciones y algunos viewmodels).
