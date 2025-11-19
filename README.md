# App Clínica ATL

Aplicación móvil nativa para Android desarrollada en Kotlin con Jetpack Compose que centraliza los flujos de pacientes, doctores y administradores de la clínica “A tu Lado”. El proyecto demuestra autenticación, formularios validados, navegación multinivel, manejo de datos locales y envío de notificaciones del sistema, todo dentro de una misma base de código.

## Tabla de contenidos
- [Descripción general](#descripción-general)
- [Casos de uso por rol](#casos-de-uso-por-rol)
- [Arquitectura y organización](#arquitectura-y-organización)
- [Tecnologías y dependencias](#tecnologías-y-dependencias)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Persistencia y fuentes de datos](#persistencia-y-fuentes-de-datos)
- [Navegación y flujo de pantallas](#navegación-y-flujo-de-pantallas)
- [Notificaciones y permisos](#notificaciones-y-permisos)
- [Validaciones y experiencia de usuario](#validaciones-y-experiencia-de-usuario)
- [Recursos gráficos y theming](#recursos-gráficos-y-theming)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Credenciales de prueba](#credenciales-de-prueba)
- [Tareas pendientes y mejoras](#tareas-pendientes-y-mejoras)

## Descripción general
- `app/src/main/java/com/example/app_clinica_atl/MainActivity.kt` actúa como composition root: crea el canal de notificaciones, instancia Room (`AppDatabase`), DataStore (`UserPreferences`) y fabrica los distintos `ViewModel` mediante factories dedicadas.
- `AppRoot` aplica el tema `AppClinicaATLTheme`, levanta el `NavHostController` y delega la orquestación a `AppNavGraph`.
- La navegación se basa en un drawer lateral (`AppDrawer`) y una `TopAppBar` personalizada (`AppTopBar`) que conviven con un `Scaffold` Material 3.
- El estado de UI se administra con `StateFlow` y `collectAsStateWithLifecycle`, garantizando pantallas reactivas ante actualizaciones de repositorios.

## Casos de uso por rol
### Paciente
- Flujo de autenticación y registro (`LoginScreenVm`, `RegisterScreenVm`) con validaciones en tiempo real y persistencia del estado de sesión vía DataStore.
- Pantalla de inicio (`HomeScreenVm`) con saludo personalizado, accesos directos a reservas, seguros y perfil.
- Reserva de horas médicas (`BookAppointmentScreenVm`): selección de especialidad, doctor y horario con confirmación y notificación opcional.
- Visualización y edición de datos personales (`PatientProfileScreen`), incluyendo acciones para actualizar contacto y adjuntar historial clínico (mock).
- Catálogo de seguros (`SegurosScreen`) y formulario de contratación (`FormularioSeguroScreen`) con validaciones estrictas y confirmación mediante diálogo/toast.

### Doctor
- Menú operativo (`DoctorMenuScreen`) con acceso a agenda (`DoctorScheduleScreen`), perfil profesional (`DoctorProfileScreenVm`) y búsqueda de pacientes (`PatientSearchScreenVm`).
- Perfil del doctor editable con estadísticas, carga diferida de datos desde `DoctorRepository` y simulación de guardado (`DoctorProfileViewModel`).

### Administrador
- Tablero principal (`AdminMenuScreen`) que centraliza accesos a las gestiones administrativas.
- Consulta de horarios por doctor (`AdminDoctorScheduleScreen`) y revisión de historiales (`AdminUserHistoriesScreen`).
- Búsqueda avanzada de doctores (`AdminDoctorSearchScreenVm`) con filtrado sobre el repositorio in-memory.
- Gestión completa de datos de doctores (`AdminManageDoctorScreenVm`): búsqueda por ID, edición de campos sensibles y acciones de guardado/eliminación.
- Registro de nuevos doctores (`AdminAddDoctorScreen`) con validaciones básicas y alta en `DoctorRepository`.

### Funcionalidades compartidas
- Drawer lateral común (`AppDrawer`) con accesos a inicio, seguros, reservas y perfil.
- Persistencia del estado de login con `UserPreferences` (DataStore), utilizado por `AuthViewModel` para recordar sesiones.
- `NotificationHelper` crea canales y dispara notificaciones tanto para reservas como para seguros.
- Temas, iconografía extendida y recursos reutilizables desde `ui/theme` y `ui/components`.

## Arquitectura y organización
- Arquitectura MVVM: los `ViewModel` exponen `StateFlow` con estados inmutables (por ejemplo `AuthViewModel`, `BookAppointmentViewModel`, `AdminManageDoctorViewModel`).
- Capa de navegación (`navigation/`) declarada mediante `NavHost`, con rutas tipadas en `Routes.kt`.
- Capa de dominio mínima (`domain/validation/`) que centraliza validaciones reutilizables (email, teléfonos chilenos, contraseñas seguras, etc.).
- Repositorios especializados (`data/repository/`) encapsulan la lógica de origen de datos. Algunos utilizan data sets en memoria (`DoctorRepository`, `PatientRepository`) y otros acceden a Room (`UserRepository`).
- La inyección de dependencias es manual en `AppRoot`, evitando frameworks externos y preservando el control explícito sobre el ciclo de vida.

## Tecnologías y dependencias
- **Lenguaje y compilador:** Kotlin, Kotlin DSL para Gradle, targeting JVM 17.
- **UI:** Jetpack Compose, Material 3, icons extendidos y `collectAsStateWithLifecycle`.
- **Navegación:** Navigation Compose 2.9.5 con `NavHost`, `ModalNavigationDrawer` y `Scaffold`.
- **Persistencia local:** Room 2.6.1 (con generación de esquemas vía KSP) para usuarios y DataStore Preferences para estado de sesión.
- **Concurrencia:** Kotlin Coroutines y `viewModelScope`.
- **Carga de imágenes:** Coil Compose 2.7.0 para mostrar recursos gráficos.
- **Notificaciones:** `NotificationCompat`, canales por API 26+ y permisos dinámicos para API 33+.
- **Configuración Android:** `compileSdk = 36`, `targetSdk = 36`, `minSdk = 24`, firma de build configurada en `app/build.gradle.kts`.

## Estructura del proyecto
```
app_clinica_atl/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── java/com/example/app_clinica_atl/
│       │   ├── MainActivity.kt
│       │   ├── navigation/          # NavGraph, routes y helpers de Drawer/TopBar
│       │   ├── notifications/       # NotificationHelper y canales
│       │   ├── data/
│       │   │   ├── local/           # Room (AppDatabase, UsuarioDao, UsuarioEntity) y DataStore
│       │   │   ├── model/           # Entidades de dominio (DoctorInfo, Patient)
│       │   │   └── repository/      # Repositorios por agregado (usuarios, doctores, reservas, pacientes)
│       │   ├── domain/validation/   # Reglas de validación reutilizables
│       │   └── ui/
│       │       ├── components/      # Drawer, TopBar y otros elementos reutilizables
│       │       ├── screen/          # Pantallas Compose por rol
│       │       ├── theme/           # Colores, tipografías y tema Material 3
│       │       └── viewmodel/       # ViewModels y factories
│       └── res/                     # Layouts, drawables, strings y assets estáticos
├── gradle/                          # Wrapper de Gradle
├── build.gradle.kts                 # Configuración de nivel raíz
└── settings.gradle.kts
```

## Persistencia y fuentes de datos
- **Room (`data/local/database/AppDatabase.kt`):** base `app_clinica_atl.db` con `UsuarioEntity`. Al crear la base se insertan usuarios semilla (admin, pacientes y doctores). Las migraciones utilizan `fallbackToDestructiveMigration`.
- **UsuariosRepository:** expone `login`, `register`, búsqueda por id y flujo del usuario autenticado sobre Room.
- **UserPreferences (DataStore):** almacena un flag de sesión para mantener al usuario conectado entre reinicios.
- **DoctorRepository:** consulta doctores desde Room (vía `UsuarioDao`) y filtra por especialidad.
- **CitasRepository:** gestiona citas en Room y devuelve horas ocupadas por doctor/fecha.
- **SegurosRepository:** administra seguros (`SeguroEntity`) y la relación `UsuarioSeguroEntity`.

## Navegación y flujo de pantallas
- `Routes.kt` define una sealed class con todas las rutas utilizadas por `NavHost`.
- `NavGraph.kt` levanta un `ModalNavigationDrawer` y un `Scaffold`; cada `composable` comparte lambdas de navegación para mantener el drawer sincronizado.

| Ruta (`Route`)             | Rol           | Pantalla asociada                          | Descripción principal |
|---------------------------|---------------|--------------------------------------------|-----------------------|
| `Home`                    | Paciente      | `HomeScreenVm`                             | Dashboard con accesos rápidos y recomendaciones. |
| `Login` / `Register`      | Paciente      | `LoginScreenVm`, `RegisterScreenVm`        | Autenticación y creación de cuenta con validaciones de dominio. |
| `BookAppointment`         | Paciente      | `BookAppointmentScreenVm`                  | Reserva guiada con selección de doctor, fecha y hora. |
| `Insurance` / `InsuranceForm` | Paciente | `SegurosScreen`, `FormularioSeguroScreen`  | Catálogo de seguros y formulario de solicitud con notificaciones. |
| `Profile`                 | Paciente      | `PatientProfileScreen`                     | Gestión del perfil y archivos adjuntos (mock). |
| `PatientSearch`           | Doctor        | `PatientSearchScreenVm`                    | Búsqueda y detalle rápido de pacientes asignados. |
| `DoctorMenu`              | Doctor        | `DoctorMenuScreen`                         | Menú principal del rol doctor. |
| `DoctorAppointments`      | Doctor        | `DoctorScheduleScreen`                     | Agenda diaria/semana disponible. |
| `DoctorProfile`           | Doctor        | `DoctorProfileScreenVm`                    | Estadísticas, contacto y edición de datos del profesional. |
| `AdminMenu`               | Administrador | `AdminMenuScreen`                          | Inicio del panel administrativo. |
| `AdminDoctorSchedule`     | Administrador | `AdminDoctorScheduleScreen`                | Consulta de disponibilidad por doctor. |
| `AdminUserHistories`      | Administrador | `AdminUserHistoriesScreen`                 | Listado de historiales clínicos (mock). |
| `AdminDoctorSearch`       | Administrador | `AdminDoctorSearchScreenVm`                | Búsqueda y filtrado de doctores. |
| `AdminManageDoctor`       | Administrador | `AdminManageDoctorScreenVm`                | Edición/elim. de doctores existentes. |
| `AdminAddDoctor`          | Administrador | `AdminAddDoctorScreen`                     | Alta de nuevos doctores. |

## Notificaciones y permisos
- `NotificationHelper` define canales independientes para reservas (`appointment_confirmations`) y seguros (`insurance_confirmations`) y se registra al iniciar la app.
- `BookAppointmentScreenVm` solicita el permiso `POST_NOTIFICATIONS` en Android 13+ y, en caso de aprobación, genera una notificación con detalle de la hora agendada.
- `FormularioSeguroScreen` muestra un toast de confirmación y dispara una notificación cuando el usuario posee el permiso.
- Para probar notificaciones en emuladores con API 33+, acepta manualmente el permiso cuando la app lo solicite.

## Validaciones y experiencia de usuario
- `domain/validation/Validators.kt` centraliza reglas reutilizables (email, nombre, teléfono, fecha, contraseña y confirmación).
- `AuthViewModel` y `FormularioSeguroScreen` integran estas reglas para habilitar/deshabilitar botones y desplegar mensajes de error contextual.
- Formateo asistido para fechas (`DD-MM-YYYY`) y teléfonos (solo dígitos) en formularios.
- `BookAppointmentViewModel` controla la habilitación de menús desplegables y dialogos (`DatePickerDialog`, `AlertDialog`) en función de las selecciones previas.

## Recursos gráficos y theming
- `ui/theme/` define paleta de colores, tipografías y estilos Material 3 coherentes con la identidad de la clínica.
- Recursos en `res/drawable` incluyen logotipos, fotografías y artes de seguros utilizados en pantallas de inicio y catálogo.
- `strings.xml` agrupa textos reutilizables, mensajes de validación y copys de notificación para facilitar traducciones futuras.
- Se usa Coil para cargar imágenes de recursos locales con soporte para escalado y placeholders.

## Configuración y ejecución
### Requisitos previos
- Android Studio Flamingo (o superior) con Kotlin y soporte para Jetpack Compose.
- JDK 17 (configurado automáticamente por el wrapper de Gradle).
- Dispositivo físico o emulador con Android 7.0 (API 24) o superior.

### Pasos en Android Studio
1. Abrir el directorio raíz `app_clinica_atl` desde *File > Open…*.
2. Permitir que Gradle sincronice dependencias (KSP generará los esquemas de Room en `app/schemas`).
3. Seleccionar un dispositivo/emulador y ejecutar *Run*.
4. Si pruebas notificaciones en API 33+, acepta el permiso `POST_NOTIFICATIONS` cuando se solicite.

### Línea de comandos
```bash
# Compilar la variante debug
./gradlew app:assembleDebug

# Ejecutar pruebas instrumentadas (requiere dispositivo/emulador conectado)
./gradlew app:connectedAndroidTest

# Limpiar resultado de compilación
./gradlew clean
```


> Las nuevas cuentas creadas desde el flujo de registro quedan con rol Paciente (id\_rol = 1).

## Tareas pendientes y mejoras
- Reemplazar datos mockeados de doctores, pacientes y reservas por servicios remotos o sincronización con una API real.
- Implementar control de roles dinámico en la navegación para mostrar únicamente el menú correspondiente al usuario autenticado.
- Persistir formularios de doctores y seguros en un backend (o en Room) y añadir feedback en línea para operaciones CRUD.
- Incorporar pruebas unitarias para los `ViewModel` y pruebas instrumentadas para flujos críticos (login, reserva, formulario de seguros).
- Internacionalizar textos y mensajes, habilitando traducciones y formatos de fecha/hora regionales.
- Añadir manejo de errores de red/persistencia y estados vacíos más elaborados en pantallas de búsqueda/listado.
- Automatizar la generación de capturas o videos (por ejemplo con Macrobenchmark / Espresso) para documentación del proyecto.
