# CRUD, API REST y Cliente-Servidor
Ejemplo aplicado: proyecto **Mundial** (Spring Boot + MongoDB Atlas + Thymeleaf + Bootstrap 5)

## 1. Cliente-servidor
Modelo en el que el trabajo se reparte entre dos partes que se comunican por red (HTTP):

- **Cliente:** muestra la interfaz y pide datos. En el proyecto: el navegador con las páginas `home.html`, `estadio.html`, `club.html`, etc. y su JavaScript.
- **Servidor:** recibe las peticiones, aplica las reglas y accede a la base de datos. En el proyecto: la aplicación Spring Boot, que usa MongoDB Atlas.

Flujo: el cliente envía una **petición** (request) y el servidor devuelve una **respuesta** (response).

```
Navegador (cliente)  --HTTP/JSON-->  Spring Boot (servidor)  --->  MongoDB Atlas
                     <--HTTP/JSON--                           <---
```

Ventajas: la interfaz y la lógica se desarrollan por separado, varios clientes (web, móvil, Postman) usan el mismo servidor y los datos viven en un solo lugar.

## 2. API REST
Una API (interfaz de programación) permite que un programa use los servicios de otro. **REST** es un estilo para diseñarla:

| Principio | Significado | En el proyecto |
|---|---|---|
| Recursos con URL | Cada cosa tiene una dirección | `/api/clubes`, `/api/clubes/{id}` |
| Verbos HTTP | El verbo indica la acción | GET, POST, PUT, DELETE |
| Representación JSON | Los datos viajan como texto JSON | `{"nombre":"Atlético"}` |
| Sin estado | Cada petición trae todo lo necesario | no hay sesión en el servidor |
| Códigos de estado | Resultado estandarizado | 200, 201, 204, 400, 404, 409 |

Códigos usados: **200** OK, **201** creado, **204** sin contenido (borrado), **400** petición inválida (falta una dependencia), **404** no existe, **409** conflicto (otros registros dependen de él).

## 3. CRUD
Las cuatro operaciones básicas sobre los datos:

| Operación | Verbo HTTP | Ejemplo | Respuesta |
|---|---|---|---|
| **C**reate (crear) | POST | `POST /api/estadios` | 201 |
| **R**ead (leer) | GET | `GET /api/estadios` y `/api/estadios/{id}` | 200 / 404 |
| **U**pdate (actualizar) | PUT | `PUT /api/estadios/{id}` | 200 / 404 |
| **D**elete (eliminar) | DELETE | `DELETE /api/estadios/{id}` | 204 / 404 |

## 4. Cómo encaja en el proyecto Mundial
Dos tipos de controlador por entidad:

- `…WebControlador` (`@Controller`): solo entrega la página HTML (Thymeleaf).
- `…RestControlador` (`@RestController`, `@RequestMapping("/api")`): entrega y recibe JSON.

Capas: `Controlador` → `Repositorio` (MongoRepository) → `Entidades` (@Document).

Pasos de una acción, por ejemplo "guardar un club":
1. El usuario abre `/clubes`: `ClubWebControlador` devuelve `club.html`.
2. El JavaScript pide con `fetch` los estadios, entrenadores y asociaciones a la API.
3. El usuario llena el formulario; el JavaScript envía `POST /api/clubes` con JSON.
4. `ClubRestControlador` valida las dependencias y guarda con `ClubRepositorio` en MongoDB.
5. El servidor responde 201 con el club guardado (o 400 con `{"error": "..."}`).
6. El navegador actualiza la tabla.

## 5. Relaciones entre entidades

| Relación | Entidades | Regla |
|---|---|---|
| Uno a uno | Club – Estadio | Cada club tiene su estadio; un estadio no puede estar en dos clubes |
| Uno a uno | Club – Entrenador | Cada club tiene su entrenador; no se comparte |
| Muchos a uno | Club – Asociación | Muchos clubes pertenecen a una asociación |
| Muchos a uno | Jugador – Club | Muchos jugadores pertenecen a un club |
| Muchos a muchos | Competición – Club | Una competición reúne varios clubes y un club juega varias competiciones |

En MongoDB se implementan con `@DocumentReference`: el documento guarda solo el `_id` del documento relacionado.

**Una entidad no funciona sin la otra:** la API rechaza (400) guardar un club sin estadio, entrenador o asociación existentes, un jugador sin club, o una competición con menos de dos clubes. Y rechaza (409) borrar un registro del que otros dependen.

Orden de registro: Estadio / Asociación / Entrenador → Club → Jugador / Competición.

## 6. Endpoints
Recursos: `/api/estadios`, `/api/asociaciones`, `/api/entrenadores`, `/api/clubes`, `/api/jugadores`, `/api/competiciones`. Cada uno admite GET (lista), GET `/{id}`, POST, PUT `/{id}` y DELETE `/{id}`.

## 7. Preguntas típicas de exposición
- **¿Qué diferencia hay entre @Controller y @RestController?** El primero devuelve vistas HTML; el segundo devuelve datos (JSON) directamente en el cuerpo de la respuesta.
- **¿Por qué no hay capa Service?** Los repositorios que extienden MongoRepository ya traen el CRUD; las reglas de dependencia están en los controladores REST.
- **¿Qué diferencia hay entre PUT y POST?** POST crea un recurso nuevo; PUT reemplaza uno existente identificado por su id.
- **¿Qué es JSON?** Formato de texto para intercambiar datos: pares clave-valor entre llaves.
- **¿Qué significa "sin estado"?** El servidor no recuerda peticiones anteriores; cada una se resuelve sola.
