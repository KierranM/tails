FROM scratch

LABEL "org.opencontainers.image.title"         = "kierranm/tail"
LABEL "org.opencontainers.image.description"   = "A scratch image for tailing files"
LABEL "org.opencontainers.image.authors"       = "Kierran McPherson <kierranm@gmail.com>"
LABEL "org.opencontainers.image.url"           = "https://github.com/kierranm/docker.tails"
LABEL "org.opencontainers.image.documentation" = "https://github.com/kierranm/docker.tails/tree/master/README.md"
LABEL "org.opencontainers.image.source"        = "https://github.com/kierranm/docker.tails"
LABEL "org.opencontainers.image.vendor"        = "Kierran McPherson <kierranm@gmail.com>"
LABEL "org.opencontainers.image.licenses"      = "Apache License 2.0"

# Set in CI
# LABEL "org.opencontainers.image.created" = ""
# LABEL "org.opencontainers.image.revision" = ""
# LABEL "org.opencontainers.image.version" = ""


COPY --from=busybox:1.31.1 /bin/tail /bin/tail

ENTRYPOINT ["/bin/tail"]
CMD [""]
