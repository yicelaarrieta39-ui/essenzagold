CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(150) NOT NULL,
    nit VARCHAR(30) NOT NULL UNIQUE,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    correo VARCHAR(120),
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    estado VARCHAR(20) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion CLOB,
    categoria VARCHAR(30) NOT NULL,
    presentacion VARCHAR(50),
    capacidad_ml DECIMAL(8,2),
    concentracion VARCHAR(50),
    precio_compra DECIMAL(12,2) NOT NULL DEFAULT 0,
    precio_venta DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    estado VARCHAR(20) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compras (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_factura VARCHAR(50),
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observaciones CLOB
);

CREATE TABLE IF NOT EXISTS detalle_compras (
    id_detalle_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    costo_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre_cliente VARCHAR(150),
    documento_cliente VARCHAR(30),
    telefono_cliente VARCHAR(20),
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observaciones CLOB
);

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS devoluciones (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_detalle_venta INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_devolucion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cantidad INT NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    estado_producto VARCHAR(20) NOT NULL,
    observaciones CLOB
);
