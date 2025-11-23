App Clínica ATL
================

Aplicación Android (Kotlin + Jetpack Compose) para la gestión integral de la clínica ATL. Incluye flujos completos para pacientes, doctores y administradores, conectados a microservicios REST (usuarios/personal, citas, seguros, historiales).

Tabla de contenido
------------------
- Visión general
- Arquitectura y stack
- Estructura del código
- Configuración del entorno
- Backends y endpoints
- Funcionalidades por rol
- Ejecución y build
- Pruebas
- Solución de problemas
- Notas de desarrollo

Visión general
--------------
- Autenticación y roles (paciente, doctor, administrador).
- Paciente: reserva de citas, gestión de seguros, perfil e historial de citas.
- Doctor: agenda, búsqueda de pacientes, perfil con estadísticas y ajustes básicos.
- Administrador: catálogo de especialidades, alta de doctores, listado/edición básica de doctores, perfil.

Arquitectura y stack
--------------------
- Presentación: Jetpack Compose + Navigation; estado en ViewModels con StateFlow.
- Datos: repositorios sobre Retrofit (`RetrofitClient`) + `UserPreferences` para almacenamiento simple.
- Inversión de control: factories manuales para ViewModels.
- Lenguaje/SDK: Kotlin 1.9+, Android API 24+; JDK 17; Gradle Kotlin DSL.

Estructura del código
---------------------
- `navigation/Routes.kt` y `navigation/NavGraph.kt`: rutas y grafo de navegación.
- `ui/screen/*`: pantallas Compose (login, registro, paciente, doctor, admin, etc.).
- `ui/viewmodel/*`: lógica de presentación, validaciones y manejo de estado.
- `data/remote/*`: Retrofit APIs (`UsuariosApi`, `CitasApi`, `SegurosApi`, `HistorialesApi`) y DTOs.
- `data/repository/*`: repositorios (usuarios, citas, seguros, historiales, especialidades, doctor).
- `data/local/*`: preferencias (`UserPreferences`).
- `app/src/test/*`: pruebas unitarias (validaciones).

Configuración del entorno
-------------------------
1) SDK Android: crea/ajusta `local.properties` (ver ejemplo en `local.propierties.txt.txt`):
   ```
   sdk.dir=/ruta/a/Android/Sdk
   ```
2) URLs de backend: define el código/túnel en `data/remote/RetrofitClient.kt` (constante `CODIGO` y puertos 8080/8082/8083/8084).
3) Sin claves secretas: el acceso es HTTP directo a microservicios.

Backends y endpoints relevantes
-------------------------------
- Usuarios / personal_service (`UsuariosApi`):
  - Auth: `POST auth/login`, `POST auth/register`
  - Usuarios: `GET/POST/PUT/DELETE /usuarios`, `GET /usuarios/{id}`
  - Doctores: `GET/POST/PUT/DELETE /doctores`, `GET /doctores/{id}`
  - Especialidades: `GET /especialidades`, `GET /doctores/{doctorId}/especialidades`, `POST /especialidades`, `PUT /especialidades/{id}`
- Citas (`CitasApi`):
  - `GET citas/doctor/{idDoctor}/fecha/{fecha}` → slots y citas por fecha
  - `POST citas` → crea cita; `PUT citas/{id}` → actualiza; `GET citas/usuario/{idUsuario}` → citas por usuario
- Seguros (`SegurosApi`): catálogo de seguros y contratación.
- Historiales (`HistorialesApi`): historiales médicos por doctor/usuario.

El cliente tolera alias de campos en especialidades (`nombre`, `especialidad`, `name`, `id_especialidad`, etc.) para evitar fallos por diferencias de nombres.

Funcionalidades por rol
-----------------------
- Paciente
  - Login/registro.
  - Home con accesos rápidos.
  - Seguros: listado y contratación.
  - Citas: seleccionar especialidad → doctor → fecha/hora; visualizar próximas/anteriores.
  - Perfil: datos personales básicos.

- Doctor
  - Menú principal.
  - Agenda: visualización de citas próximas.
  - Búsqueda de pacientes y acceso a ficha del paciente.
  - Perfil: estadísticas mensuales, teléfono, cambio de password.

- Administrador
  - Menú de administración.
  - Especialidades: listado y edición de nombre (usa `/especialidades`).
  - Alta de doctor: crea usuario doctor, ficha de doctor y especialidades nuevas asociadas.
  - Listado/edición básica de doctores.
  - Perfil del administrador.

Ejecución y build
-----------------
- Android Studio: Run sobre el módulo `app` (emulador o dispositivo).
- Línea de comando:
  ```
  ./gradlew assembleDebug
  ./gradlew installDebug
  ```

Pruebas
-------
- Unit tests (validaciones y lógica pura):
  ```
  ./gradlew testDebugUnitTest
  ```
- No hay instrumented tests configurados en CI; pueden correrse desde Android Studio si se requieren.

Solución de problemas
---------------------
- Especialidades vacías: revisa logcat de `GET /api/v1/especialidades` (códigos 200/204/404) y confirma URLs en `RetrofitClient`.
- Login falla: verifica credenciales y host de `auth/login` (BASE_URL_USUARIO).
- Horarios vacíos: backend de citas debe marcar slots como `disponible=true` y `estado=DISPONIBLE` para que aparezcan.

Notas de desarrollo
-------------------
- Solo el módulo `app` se compila; resto son configuraciones del IDE.
- Código en ASCII. Evita introducir caracteres especiales en nuevos archivos.
