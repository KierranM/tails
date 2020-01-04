# Tails
A tiny sidecar image for tailing files. Useful for when your containerised
application logs to multiple files that you'd like to be able to retrieve
using something like `kubectl logs`.

## What's Inside?

`/bin/tail`, that's it. This image is built from scratch and the `tail`
binary is copied from `busybox`.

## Why not just use busybox?

You can. This was built so I could have a sidecar image with a very minimal
attack surface. Busybox is fantastically small (nearly as small as this),
but it still includes a whole lot of binaries that I don't need for my logging
sidecars.

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