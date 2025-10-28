# App Clínica ATL

Aplicación móvil desarrollada en Kotlin/Jetpack Compose para la gestión integral de la clínica "A tu Lado". Reúne las experiencias de pacientes, doctores y administradores dentro de un mismo proyecto.

## Características principales
- **Autenticación y registro** de usuarios con flujo guiado.
- **Panel de inicio** con acceso rápido a reservas, seguros y perfil.
- **Reservas de horas médicas** con soporte para selección y gestión de citas.
- **Gestión de pacientes** que incluye búsqueda y visualización de historiales clínicos.
- **Flujo de doctores** con menú dedicado para citas, historiales y perfil profesional.
- **Perfil de administrador** con las siguientes pantallas incluidas:
  - Menú principal del administrador.
  - Consulta de horarios por doctor.
  - Visualización de historiales de usuarios.
  - Administración de datos de doctores (modificar/eliminar).
  - Registro de nuevos doctores.

## Stack tecnológico
- **Lenguaje:** Kotlin.
- **UI:** Jetpack Compose y Material 3.
- **Navegación:** Navigation Compose.
- **Persistencia:** Room (vía `AppDatabase`) y DataStore Preferences para configuraciones de usuario.
- **Arquitectura:** MVVM con repositorios dedicados para usuarios, pacientes y reservas.

## Estructura del proyecto
```
app/
 └── src/main/java/com/example/app_clinica_atl/
     ├── data/          # Entidades locales, DAO y repositorios
     ├── navigation/    # Definición de rutas y grafo de navegación
     ├── ui/
     │   ├── components/  # Drawer, top bars y componentes reutilizables
     │   ├── screen/      # Pantallas Compose (incluye flujo administrador)
     │   └── viewmodel/   # ViewModels y factories
     └── MainActivity.kt  # Composition root y arranque de la app
```

## Ejecución
1. Abrir el proyecto en **Android Studio** (Flamingo o superior recomendado).
2. Sincronizar Gradle al importar el proyecto.
3. Seleccionar un emulador o dispositivo físico.
4. Ejecutar desde el botón *Run* para compilar e instalar la app.

> Nota: Si prefieres trabajar desde la línea de comandos, utiliza las tareas de Gradle estándar según tus necesidades (por ejemplo, `./gradlew assembleDebug`).

## Próximos pasos sugeridos
- Conectar las pantallas de administrador a los repositorios reales para persistir cambios.
- Añadir validaciones y mensajes de éxito/error en los formularios de doctores.
- Incorporar control de roles para mostrar el menú adecuado según el usuario autenticado.
- Agregar pruebas unitarias e instrumentadas para flujos críticos.
