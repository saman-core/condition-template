
###
FROM registry.access.redhat.com/ubi9-minimal:9.3 AS ubi
# -- Stage scratch
FROM registry.access.redhat.com/ubi9-micro:9.3 AS scratch
# -- Final Stage
FROM scratch
COPY --from=ubi /usr/lib64/libgcc_s.so.1 /usr/lib64/libgcc_s.so.1
COPY --from=ubi /usr/lib64/libstdc++.so.6 /usr/lib64/libstdc++.so.6
COPY --from=ubi /usr/lib64/libz.so.1 /usr/lib64/libz.so.1

WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application

RUN mkdir /work/classes
RUN chown 1001:root /work/classes
COPY --chown=1001:root target/classes/undeleteme.txt /work/classes/undeleteme.txt
COPY --chown=1001:root target/classes/*.dmn /work/classes/
RUN chmod 444 -R /work/classes


EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]

