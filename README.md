# 🎟️ Sistema Distribuido de Venta de Entradas
### Trabajo Final - Programación 2 (2025)

**Alumno:** Matias Agustin Perez  
**Universidad:** Universidad de Mendoza  
**Carrera:** Ingeniería Informática
**Legajo** 61218

---

## 📋 Descripción del Proyecto

Este sistema implementa una arquitectura distribuida para la gestión y venta de entradas de espectáculos. El sistema local se sincroniza en tiempo real con un servidor central ("Cátedra") utilizando patrones de **Resiliencia** y mensajería asíncrona.

El objetivo es permitir la venta de entradas desde una Aplicación Móvil (Android) incluso si la conexión con el servidor central es intermitente, garantizando la consistencia de datos y la persistencia de asientos ocupados.

---

## 🏗️ Arquitectura del Sistema

El proyecto se divide en 4 módulos principales interconectados:

1.  **Proxy (Middleware):**
    * Actúa como frontera entre el sistema local y la Cátedra.
    * Escucha eventos de **Kafka** y consume la API REST del profesor.
    * Implementa lógica de **Sanitización** y **Resiliencia** para reparar datos entrantes corruptos o incompletos antes de que lleguen al sistema local.

2.  **Backend (JHipster/Spring Boot):**
    * API REST local que gestiona la base de datos y la lógica de negocio.
    * Expone endpoints seguros para la App Móvil.
    * Gestiona la autenticación y las transacciones de venta.

3.  **Base de Datos (MySQL - Docker):**
    * Contenedor Dockerizado para persistencia de datos.
    * Estructura optimizada para soportar grandes volúmenes de metadatos de asientos sin truncamiento.

4.  **App Móvil (Android):**
    * Interfaz de usuario para visualización de mapas de asientos (Rojo/Verde) y compra de entradas en tiempo real.

---

## 🚀 Instrucciones de Ejecución

Siga estos pasos en orden estricto para iniciar el ecosistema correctamente.

### 1. Prerrequisitos
* Java JDK 17 o superior.
* Docker y Docker Compose instalados y corriendo.
* Conexión a la red de la Cátedra activa (VPN o ZeroTier).

### 2. Base de Datos (MySQL)
Levantar el contenedor Docker.
```bash
cd backend
docker compose -f src/main/docker/mysql.yml up -d
```

### 3. Backend Local
Iniciar el servidor JHipster (Puerto 8080).
```bash
cd backend
./mvnw
```

### 4. Proxy (Sincronizador)
Iniciar el servicio de comunicación (Puerto 8081).
*Nota: Al iniciar, el Proxy intentará sincronizar automáticamente con la Cátedra.*
```bash
cd proxy
./mvnw spring-boot:run
```

### 5. Aplicación Móvil
* Instalar el APK en un dispositivo Android o ejecutar desde Android Studio.
* **Importante:** Asegurarse de que el dispositivo esté en la misma red (Wi-Fi/ZeroTier) que el servidor.

---

**© 2025 Matias Agustin Perez**