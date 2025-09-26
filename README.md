## Actividad - Entregable 2 - Dev Ops
### Grupo: Juan Magrini, Claudio Cabrera
### Profesor: Rodrigo Lopez

Este proyecto implementa un REST API en Java Spring Boot para simular la toma de pedidos en una cafetería.
La aplicación está contenerizada con Docker y desplegada en un cluster de Kubernetes (Minikube).

```bash
cafe-api/
├── src/                     # Código fuente Java
│   └── main/java/com/cafeteria/cafe_api/
│       ├── CafeApiApplication.java   # Clase principal Spring Boot
│       ├── model/                   # Entidades (Product, Order)
│       ├── repository/              # JPA Repositories
│       ├── service/                 # Lógica de negocio (OrderService, ProductService)
│       └── controller/              # Endpoints REST
│
├── pom.xml                  # Dependencias y configuración Maven
├── Dockerfile               # Construcción de imagen Docker
├── k8s/                     # Manifiestos Kubernetes
│   ├── namespace.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── servicemonitor.yaml  # Scraping de métricas por Prometheus Operator
│   ├── order-alerts.yaml    # Reglas de alerta
│   └── grafana-dashboard-configmap.yaml   # Dashboard provisionado en Grafana
├── grafana/                 # Dashboards JSON (versión editable)
│   └── cafe-api-dashboard.json
└── README.md

```

Funcionalidades principales:
- Gestión de productos
  - Crear un producto con nombre y precio.
  - Editar un producto existente.
  - Eliminar productos.
  - Listar todos los productos.
  - Endpoints:
    - POST /api/products
    - GET /api/products
    - PUT /api/products/{id}
    - DELETE /api/products/{id}

- Gestión de órdenes
  - Crear órdenes de productos.
  - Marcar una orden como pagada.
  - Cancelar una orden.
  - Listar todas las órdenes.
  - Endpoints:
    - POST /api/orders/{productId}
    - POST /api/orders/{orderId}/pay
    - POST /api/orders/{orderId}/cancel
    - GET /api/orders

Monitoreo y alertas
- La aplicación expone métricas en /actuator/prometheus.
- Se trackean todas las órdenes con etiquetas (producto, estado).
- Prometheus recolecta estas métricas automáticamente.
- Grafana permite visualizarlas en dashboards.
- Alertas disparan cuando un producto supera determinada cantidad de órdenes en 5 minutos.

Construcción de la imagen Docker
```bash
docker build -t cafe-api:latest .
```
Cargar la imagen en Minikube:
```bash
minikube image load cafe-api:latest
```

Despliegue en Kubernetes
- Crear namespace y recursos:
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

- Exponer la aplicación con Minikube:
```bash
minikube service cafe-api -n cafeteria --url
```

Monitoreo con Prometheus + Grafana
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm upgrade --install kps prometheus-community/kube-prometheus-stack -n monitoring --create-namespace
```

- Conectar la app cafe-api con Prometheus. Para que Prometheus scrapee las métricas expuestas en /actuator/prometheus, hay que aplicar el ServiceMonitor incluido en k8s/servicemonitor.yaml:
```bash
kubectl apply -f k8s/servicemonitor.yaml
```

Visualizar Prometheus:
```bash
kubectl port-forward -n monitoring svc/kps-kube-prometheus-stack-prometheus 9090:9090
```
Abrir en navegador en http://localhost:9090
Consultar métricas: orders_total

Visualizar grafana:
```bash
kubectl port-forward -n monitoring svc/kps-grafana 3000:80
```
Abrir en navegador en http://localhost:3000
Credenciales por defecto:
- user: admin
- pass: prom-operator

- Dashboards:
- Opción 1: Importación manual
  - En Grafana, ir a Dashboards → New → Import.
  - Subir el archivo grafana/cafe-api-dashboard.json
  - Seleccionar la fuente de datos Prometheus.

- Opción 2: Importación automática con ConfigMap
  - En el proyecto incluimos k8s/grafana-dashboard-configmap.yaml
, que crea un ConfigMap con el dashboard y Grafana lo carga automáticamente gracias al label grafana_dashboard: "1".

Para aplicarlo:
```bash
kubectl apply -f k8s/grafana-dashboard-configmap.yaml
```

Alertas
- El archivo k8s/order-alerts.yaml define una regla de Prometheus que dispara una alerta si un producto supera 10 órdenes en 5 minutos:
Esto dispara una alerta si un producto supera 10 órdenes en 5 minutos y también en Grafana (si integrás con Alertmanager).
  - http://localhost:9090/alerts

- Volver a estado inicial (limpieza total):
```bash
kubectl delete namespace cafeteria
kubectl delete namespace monitoring
```
Esto elimina:

- Pods, Deployments y Services de la app.
- Namespace y recursos asociados.
- Stack de Prometheus y Grafana.
