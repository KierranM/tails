# Tails
A tiny sidecar image for tailing files

## What's Inside?

`/bin/tail`, that's it. This image is built from scratch and the `tail`
binary is copied from `busybox`.

## Usage

### Kubernetes

Add tails as a sidecar to your application pods:

```yaml
containers:
  - name: myapp
    image: myapp
    ports:
      - containerPort: 80
    volumeMounts:
      - mountPath: /opt/myapp/logs
        name: log-volume
  - name: http-logs
    image: kierranm/tails
    args:
      - -f
      - /logs/http.txt
    volumeMounts:
      - mountPath: /logs
        name: log-volume
  - name: error-logs
    image: kierranm/tails
    args:
      - -f
      - /logs/error.txt
    volumeMounts:
      - mountPath: /logs
        name: log-volume
```