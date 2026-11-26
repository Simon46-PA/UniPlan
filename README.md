# UniPlan (Android, Jetpack Compose + Room + Hilt)

Paquete: `mx.edu.uttt.uniplan`

## Pantallas (un archivo por pantalla)
| Archivo | Pantalla |
|---|---|
| `ui/login/PrimeraPantalla.kt` | Primera pantalla: Inicio de sesión (valida contra la base de datos). Su pantalla auxiliar de registro está en `ui/login/SextaPantalla.kt` |
| `ui/home/SegundaPantalla.kt` | Segunda pantalla: Inicio (tareas y clases de hoy desde Room; editar perfil / eliminar cuenta / cerrar sesión) |
| `ui/tasks/TerceraPantalla.kt` | Tercera pantalla: Mis tareas (búsqueda, filtros, completar, deslizar para eliminar con "Deshacer", botón "+") |
| `ui/subjects/CuartaPantalla.kt` | Cuarta pantalla: Materias (CRUD) |
| `ui/schedule/QuintaPantalla.kt` | Quinta pantalla: Calendario / Horario (CRUD de clases por día) |

## Base de datos (Room / SQLite, versión 2) — 4 entidades con CRUD
| Entidad | Tabla | Operaciones |
|---|---|---|
| UserEntity | user | insertar (registro), seleccionar (login), actualizar nombre, eliminar cuenta |
| TaskEntity | task | insertar, seleccionar, actualizar (completar), eliminar |
| SubjectEntity | subject | insertar, seleccionar, actualizar, eliminar |
| ClassSessionEntity | class_session (FK -> subject, CASCADE) | insertar, seleccionar, actualizar, eliminar |

Seguridad: contraseñas con PBKDF2-HMAC-SHA256 + sal aleatoria (`data/security/PasswordHasher.kt`), `allowBackup=false`.

Patrones: Inyección de dependencias (Hilt), UDF (UiState + eventos en cada ViewModel), Repository, Singleton (`@Singleton`), Observer (Flow/StateFlow de Room).
Navegación: Navigation Compose (`NavHost` + `composable`) en `ui/UniPlanApp.kt`.

> Si ya habías instalado una versión anterior de la app, desinstálala antes de correr esta (la base de datos pasó de la versión 1 a la 2 y se recrea con los datos de ejemplo).

## Arquitectura (según la guía de Room)
```
mx.edu.uttt.uniplan
├── UniPlanApplication.kt      @HiltAndroidApp
├── MainActivity.kt            @AndroidEntryPoint
├── data
│   ├── database
│   │   ├── AppDatabase.kt
│   │   ├── TaskSeed.kt        datos iniciales (se insertan al crear la BD)
│   │   ├── dao/TaskDao.kt
│   │   └── entity/TaskEntity.kt
│   ├── di/DataBaseModule.kt, RepositoryModule.kt
│   ├── mapper/TaskMapper.kt
│   └── repository/TaskRepositoryImp.kt
├── domain
│   ├── model/Task.kt
│   └── repository/TaskRepository.kt
└── ui
    ├── UniPlanApp.kt
    ├── login/ home/ theme/
    └── tasks/TaskViewModel.kt (@HiltViewModel), TasksScreen.kt, AddTaskDialog.kt
```

Recursos: textos en `res/values/strings.xml`, colores en `colors.xml`, medidas en `dimens.xml`,
íconos vectoriales en `res/drawable/`.
Dependencias en `gradle/libs.versions.toml`.

## Cómo abrirlo
1. Descomprime el zip.
2. Android Studio -> File -> Open -> carpeta `UniPlan`.
3. Espera la sincronización de Gradle (necesita internet la primera vez) y ejecuta en un emulador o teléfono (Android 8.0+).
