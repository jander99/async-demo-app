package main

import (
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"strconv"
	"time"
)

func foo(w http.ResponseWriter, r *http.Request) {
	timeout, err := strconv.Atoi(r.FormValue("t"))
	if err != nil {
		w.WriteHeader(500)
		_, _ = w.Write([]byte("Oof\n"))
		return
	}
	time.Sleep(time.Duration(timeout) * time.Millisecond)
	_, _ = w.Write([]byte("Foo: " + strconv.Itoa(timeout) + "\n"))
	return
}

func main() {
	router := mux.NewRouter()
	router.Path("/").HandlerFunc(foo).Methods("GET").Queries("t", "{t}")
	log.Fatal(http.ListenAndServe(":8000", router))
}
