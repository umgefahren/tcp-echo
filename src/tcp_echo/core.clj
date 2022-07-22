(ns tcp-echo.core
  (:gen-class)
  (:require [clojure.core.async :as a :refer [go]]))



(import java.net.ServerSocket)
(import (java.io BufferedReader PrintWriter InputStreamReader))

(defn- bind
  ([]  (new ServerSocket))
  ([port] (new ServerSocket port))
  ([port backlog] (new ServerSocket port backlog)))

(defn- close
  [socket] (.close socket))

(defn- accept
  [socket] (.accept socket))

(defn- getInputStream
  [conn] (.getInputStream conn))

(defn- getOutputStream
  [conn] (.getOutputStream conn))

(defn- bufferedReader
  [chan] (new BufferedReader (new InputStreamReader chan)))

(defn- bufferedWriter
  [chan] (new PrintWriter chan))

(defn- readLine
  [reader] (.readLine reader))

(defn- writeLine
  [printer line] (.println printer line))


(defn- respond
  [reader writer]
  (let [inputLine (readLine reader)]
    (if (or (nil? inputLine) (nil? reader) (nil? writer))
      (throw (RuntimeException. "is nil"))
      (do (println inputLine)
          (writeLine writer inputLine)
          (.flush writer)
          (recur reader writer)))))

(defn- handleConn [conn]
  (let [reader (bufferedReader (getInputStream conn)) writer (bufferedWriter (getOutputStream conn))]
    (try (respond reader writer)
         (catch Exception _))))

(defn- acceptConn [socket] 
  (let [conn (accept socket)]
    (go (handleConn conn))
    (recur socket)))


(defn- testConnection
  [] (let [socket (bind 8080)]
       (acceptConn socket)
       (close socket)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (testConnection))
