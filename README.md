# MediConecta

Aplicación móvil desarrollada para facilitar la organización y consulta de información médica personal desde un dispositivo Android. MediConecta reúne en una sola plataforma funciones relacionadas con citas, historial médico, medicamentos y localización de centros de salud.

## Descripción general

MediConecta busca ofrecer una experiencia sencilla y organizada para que el usuario pueda administrar información básica relacionada con su salud. La aplicación permite crear una cuenta, iniciar sesión, mantener la sesión activa y acceder a diferentes módulos mediante una navegación inferior

## Funcionalidades principales

- Registro e inicio de sesión de usuarios.
- Recuperación o restablecimiento de contraseña.
- Persistencia de sesión.
- Visualización y edición del perfil.
- Gestión de citas médicas.
- Consulta del historial médico.
- Registro y seguimiento de medicamentos.
- Localización de hospitales y centros médicos en un mapa.
- Navegación mediante menú inferior.
- Deslizamiento horizontal entre las secciones principales.
- Almacenamiento local de la información.
- Interfaz adaptable a diferentes tamaños de pantalla.

## Módulos principales

### Autenticación

Contiene las pantallas relacionadas con el acceso del usuario:

- Inicio de sesión.
- Registro.
- Recuperación de contraseña.
- Perfil del usuario.
- Cierre de sesión.

### Inicio

Presenta un resumen general de la aplicación y permite acceder rápidamente a las funciones más importantes.

### Citas

Permite registrar, consultar y administrar citas médicas.

### Historial médico

Muestra la información médica registrada por el usuario, como consultas, diagnósticos o antecedentes.

### Medicamentos

Permite organizar los medicamentos del usuario y consultar la información asociada a cada registro.

### Mapas

Muestra hospitales y centros médicos mediante Google Maps, utilizando coordenadas almacenadas en la base de datos local.

## Estructura general del proyecto

La aplicación está organizada por responsabilidades para mantener el código ordenado y facilitar su mantenimiento.

```text
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/
│   └── paquete_principal/
│       ├── auth/
│       ├── data/
│       ├── database/
│       ├── dao/
│       ├── fragments/
│       ├── models/
│       ├── adapters/
│       └── utils/
└── res/
    ├── drawable/
    ├── layout/
    ├── menu/
    ├── mipmap/
    ├── values/
    └── xml/
```

### Descripción de las carpetas

- **auth:** actividades relacionadas con el registro, inicio de sesión, recuperación de contraseña y perfil.
- **data / database:** configuración y acceso a la base de datos local.
- **dao:** interfaces que contienen las operaciones de consulta, inserción, actualización y eliminación.
- **fragments:** pantallas principales mostradas dentro de la actividad principal.
- **models:** clases que representan los datos utilizados por la aplicación.
- **adapters:** componentes que conectan listas de datos con elementos visuales.
- **utils:** clases auxiliares, como el manejo de sesión y validaciones.
- **res/layout:** archivos XML que definen la interfaz.
- **AndroidManifest.xml:** declara actividades, permisos y configuraciones generales de la aplicación.


## Arquitectura

El proyecto utiliza una organización basada en **MVVM** y separación de responsabilidades.

- **Model:** representa los datos de la aplicación.
- **View:** corresponde a las actividades, fragmentos y archivos XML.
- **ViewModel:** administra la información utilizada por la interfaz y ayuda a separar la lógica de presentación.

Esta organización facilita la lectura del código, las pruebas y el mantenimiento del proyecto.

## Navegación

La actividad principal contiene las secciones principales de la aplicación:

1. Inicio.
2. Citas.
3. Historial.
4. Medicamentos.
5. Mapas.

El usuario puede cambiar de sección mediante el menú inferior o deslizando horizontalmente entre las pantallas.

## Base de datos

MediConecta utiliza una base de datos local basada en **Room**, que funciona como una capa de abstracción sobre SQLite.

La base de datos almacena información como:

- Usuarios.
- Citas.
- Registros médicos.
- Medicamentos.
- Hospitales y centros de salud.

Las operaciones se realizan mediante objetos DAO, evitando escribir directamente gran parte del código tradicional de SQLite.

## Tecnologías utilizadas

- **Kotlin:** lenguaje principal de programación.
- **XML:** diseño de interfaces.
- **Android Studio:** entorno de desarrollo.
- **Android SDK:** herramientas y componentes de Android.
- **Room Database:** almacenamiento local.
- **SQLite:** motor de base de datos utilizado internamente.
- **MVVM:** organización general del proyecto.
- **Fragments:** construcción de las pantallas principales.
- **ViewModel y LiveData:** manejo de datos y actualización de la interfaz.
- **ViewPager2:** navegación horizontal entre secciones.
- **BottomNavigationView:** navegación inferior.
- **Google Maps SDK:** visualización de hospitales y ubicaciones.
- **Material Components:** elementos visuales y controles de interfaz.
- **Gradle:** gestión de dependencias y compilación.

## Permisos utilizados

Según las funciones habilitadas, la aplicación puede requerir permisos como:

- Acceso a Internet.
- Acceso al estado de la red.
- Ubicación aproximada.
- Ubicación precisa.

Los permisos relacionados con la ubicación se utilizan para mostrar mapas o trabajar con la posición del usuario.

## Requisitos

- Android Studio.
- JDK compatible con la versión de Gradle utilizada.
- Dispositivo o emulador Android.
- Conexión a Internet para cargar Google Maps.
- Clave válida de Google Maps configurada en el proyecto.
- Sincronización correcta de las dependencias de Gradle.

## Instalación y ejecución

1. Clonar o descargar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Esperar a que Gradle termine de sincronizar.
4. Configurar la clave de Google Maps.
5. Seleccionar un emulador o dispositivo físico.
6. Compilar y ejecutar la aplicación.

```bash
git clone URL_DEL_REPOSITORIO
```

## Consideraciones importantes

- La información se almacena principalmente de forma local.
- El proyecto tiene fines académicos y no reemplaza un sistema clínico profesional.
- No debe utilizarse para emitir diagnósticos médicos, ni en entornos reales.

## Información

- Herramientas de Programación Aplicada IV.
- Universidad Tecnológica de Panamá.
- Proyecto final Aplicación móvil MediConecta.

## Grupo

- Andrés Soto.
- Roger Herman.
- Alan Aguilar.

## Objetivo

El objetivo principal del proyecto es aplicar los conocimientos adquiridos durante la materia mediante el desarrollo de una aplicación Android funcional. Entre los conceptos utilizados se encuentran:

- Desarrollo de interfaces móviles.
- Programación orientada a objetos.
- Navegación entre actividades y fragmentos.
- Persistencia de datos.
- Arquitectura de software.
- Consumo e integración de servicios externos.
- Trabajo colaborativo y control de versiones.

## Estado del proyecto

Las funciones principales están implementadas, aunque pueden realizarse mejoras relacionadas con seguridad, sincronización en la nube, accesibilidad, pruebas automatizadas y administración avanzada de datos.
