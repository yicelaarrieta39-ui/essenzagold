# Essenza Gold

Aplicación web para la gestión de usuarios, productos, proveedores, compras, ventas y devoluciones. Está desarrollada con Java 17, Spring Boot, Spring MVC, Thymeleaf y JDBC; utiliza MySQL como base de datos en tiempo de ejecución.

## Requisitos

- JDK 17.
- MySQL instalado y en ejecución.
- Git, si se va a clonar el repositorio.

No es necesario instalar Maven por separado: el proyecto incluye Maven Wrapper (`mvnw` y `mvnw.cmd`).

## Configuración de la base de datos

Cree la base de datos que usa la configuración predeterminada:

```sql
CREATE DATABASE essenza_gold
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

La aplicación se conecta por defecto a `jdbc:mysql://localhost:3306/essenza_gold`. Configure las credenciales de un usuario MySQL que tenga permisos sobre esa base de datos mediante estas variables de entorno:

- `DB_USERNAME`: usuario de MySQL.
- `DB_PASSWORD`: contraseña de MySQL.
- `DB_URL` (opcional): URL JDBC completa; si no se define, se usa la URL predeterminada indicada arriba.

Ejemplos de configuración por terminal:

**Windows PowerShell**

```powershell
$env:DB_USERNAME = "essenza_user"
$env:DB_PASSWORD = "tu contraseña"
# Opcional si la URL predeterminada no aplica:
$env:DB_URL = "jdbc:mysql://localhost:3306/essenza_gold"
```

**Linux / macOS**

```bash
export DB_USERNAME=essenza_user
export DB_PASSWORD='tu contraseña'
# Opcional si la URL predeterminada no aplica:
export DB_URL='jdbc:mysql://localhost:3306/essenza_gold'
```

La aplicación espera que las tablas del sistema ya existan. El archivo `src/test/resources/schema.sql` se utiliza para las pruebas con H2 y no debe tomarse como un script de instalación para MySQL. No se incluye actualmente un script de creación del esquema para el entorno MySQL.

## Ejecutar en desarrollo

Desde la raíz del proyecto, con las variables de base de datos configuradas:

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

Al iniciar, abre [http://localhost:8080](http://localhost:8080). El panel está disponible en `/` y `/app`; los módulos web incluyen `/usuarios`, `/productos`, `/proveedores`, `/compras`, `/ventas` y `/devoluciones`.

## Compilar y probar

Ejecute las pruebas automatizadas con:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

Las pruebas usan una base de datos H2 en memoria configurada en `src/test/resources/application.properties`; no requieren que MySQL esté iniciado. Para generar el paquete ejecutable:

```powershell
.\mvnw.cmd clean package
```

El JAR se genera en `target/essenzagold-0.0.1-SNAPSHOT.jar` y puede iniciarse con:

```powershell
java -jar target/essenzagold-0.0.1-SNAPSHOT.jar
```

Configure `DB_USERNAME`, `DB_PASSWORD` y, si corresponde, `DB_URL` también en la terminal desde la que se inicie el JAR.

## API disponible

Los controladores REST exponen recursos bajo `/api`, entre ellos:

- `/api/usuarios`
- `/api/productos`
- `/api/proveedores`
- `/api/compras` y `/api/detalles-compras`
- `/api/ventas` y `/api/detalles-ventas`
- `/api/devoluciones`
- `/api/operaciones` (operaciones de compra y venta)
