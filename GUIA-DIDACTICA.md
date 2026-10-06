# Guía didáctica · Mundial (API REST + cliente web)

Lee esta guía primero y luego abre el código: cada archivo trae comentarios que explican qué hace y por qué.

## Mapa del proyecto

```
src/main/java/com/mundial/app
├── MundialApplication.java      <- arranque (main)
├── Entidades/                   <- QUÉ datos guardamos (clases con @Document)
├── Repositorio/                 <- CÓMO hablamos con MongoDB (interfaces MongoRepository)
└── Controlador/                 <- QUIÉN atiende las peticiones
│       ├── *RestControlador     <- API: devuelve JSON (@RestController)
│       └── *WebControlador      <- Vistas: devuelve páginas HTML (@Controller)
src/main/resources
├── application.properties       <- puerto y conexión a MongoDB Atlas
├── templates/                   <- una página Thymeleaf por sección + home + fragments/navbar
└── static/ (css, js)            <- diseño (app.css), llamadas a la API (app.js) y CRUD genérico (crud.js)
docs/                            <- teoría: CRUD, API REST y cliente-servidor
```

## 1. Qué pasa cuando guardas un club (de principio a fin)
1. Abres `/clubes` -> `ClubWebControlador` devuelve `club.html` (solo la página, sin datos).
2. `crud.js` pide a la API los estadios, entrenadores y asociaciones (`GET /api/...`) y llena las listas.
3. Llenas el formulario y pulsas **Guardar** -> el navegador envía `POST /api/clubes` con JSON.
4. `ClubRestControlador` revisa las dependencias (¿existen estadio, entrenador y asociación? ¿no están usados por otro club?).
5. Si todo está bien, `ClubRepositorio.save()` lo guarda en MongoDB y la API responde **201**. Si no, responde **400** con `{"error": "..."}`.
6. El navegador muestra el mensaje y vuelve a pedir la lista para actualizar la tabla.

## 2. Glosario de anotaciones
| Anotación | Para qué sirve |
|---|---|
| `@SpringBootApplication` | Arranca la aplicación y configura Spring automáticamente |
| `@Document` | La clase se guarda como documento en una colección de MongoDB |
| `@Id` | Campo identificador único |
| `@DocumentReference` | Relación: guarda solo el id del otro documento |
| `@Controller` | Devuelve vistas HTML (Thymeleaf) |
| `@RestController` | Devuelve datos en JSON |
| `@RequestMapping("/api")` | Prefijo común de las rutas |
| `@GetMapping / @PostMapping / @PutMapping / @DeleteMapping` | Enlazan un método con un verbo HTTP y una ruta |
| `@PathVariable` | Toma un valor de la URL (el `{id}`) |
| `@RequestBody` | Convierte el JSON recibido en un objeto Java |
| `@Query` | Consulta propia en un repositorio |

## 3. Relaciones y reglas
| Relación | Entidades | Regla que aplica la API |
|---|---|---|
| Uno a uno | Club - Estadio | un estadio no puede estar en dos clubes |
| Uno a uno | Club - Entrenador | un entrenador no dirige dos clubes |
| Muchos a uno | Club - Asociación | la asociación debe existir |
| Muchos a uno | Jugador - Club | el club debe existir |
| Muchos a muchos | Competición - Clubes | al menos dos clubes, todos existentes |

Una entidad **no funciona sin la otra**: 400 al guardar sin su dependencia, 409 al borrar algo que otros usan.
Orden para registrar: Estadio / Asociación / Entrenador -> Club -> Jugador / Competición.

## 4. Pruébalo
1. Importa el proyecto (File > Import > Maven > Existing Maven Projects) y ejecuta `MundialApplication`.
2. Abre http://localhost:8080 y registra en el orden indicado.
3. Mira la API en el navegador: http://localhost:8080/api/clubes
4. Intenta guardar un club sin estadio y borrar un estadio en uso: lee el mensaje del servidor.

## 5. Retos para practicar
1. Añade el campo `pais` a Estadio (entidad, formulario y columna de la tabla).
2. Crea el endpoint `GET /api/jugadores/club/{clubId}` que liste los jugadores de un club (pista: un método nuevo en `JugadorRepositorio`).
3. Evita dos jugadores con el mismo dorsal en un club.
4. Cambia el color de una sección en `app.css` (busca `[data-sec="club"]`).
5. Mueve la contraseña de Atlas a una variable de entorno: `spring.data.mongodb.uri=${MONGODB_URI}`.
