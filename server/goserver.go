package main

import (
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"os"
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

var port = os.Getenv("PORT")

func init() {
	if port == "" {
		port = "8000"
	}
}


func main() {
	router := mux.NewRouter()

	router.
	Path("/goserver/").
	Handler(http.HandlerFunc(foo)).
	Methods("GET").
	Queries("t", "{t}")


	router.PathPrefix("/").Handler(http.HandlerFunc(func (w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(500)
		return
	}))

	//
	//router.Handle("/", handlers.LoggingHandler(os.Stdout,
	//http.HandlerFunc(foo))).Methods("GET").Queries("t", "{t}")
	//
	//router.PathPrefix("/").Handler(handlers.LoggingHandler(os.Stdout,
	//http.HandlerFunc(foo))).Methods("GET").Queries("t", "{t}")

	// router.Path("/").HandlerFunc(foo).Methods("GET").Queries("t", "{t}")
	log.Fatal(http.ListenAndServe(":"+port, router))
}
