FROM clojure

COPY . /

WORKDIR /

RUN lein uberjar

EXPOSE 3000

CMD ["lein", "run"]