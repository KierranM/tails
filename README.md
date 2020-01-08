# Tails
A tiny sidecar image for tailing files. Useful for when your containerised
application logs to multiple files that you'd like to be able to retrieve
using something like `kubectl logs`.

## What's Inside?

`/bin/tail`, that's it. This image is built from scratch and the `tail`
binary is copied from `busybox`.

By default the container runs as the `tails` user with group ID `12001`,
so you'll need to make sure that the files you are wanting to tail can
be read by this user. In Kubernetes for example, you can use the
[PodSecurityContext](https://kubernetes.io/docs/tasks/configure-pod-container/security-context/)
to set the group ID for all files created in a mounted volume.

## Why not just use busybox?

You can. This was built so I could have a sidecar image with a very minimal
attack surface. Busybox is fantastically small (nearly as small as this),
but it still includes a whole lot of binaries that I don't need for my logging
sidecars.

## Usage

### Kubernetes

Add tails as a sidecar to your application pods:

```yaml
securityContext:
    fsGroup: 2000 # You must set the fsGroup so that the tails user is able to read the files created by your application
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
      - /logs/http.txt
    volumeMounts:
      - mountPath: /logs
        name: log-volume
  - name: error-logs
    image: kierranm/tails
    args:
      - /logs/error.txt
    volumeMounts:
      - mountPath: /logs
        name: log-volume
```
