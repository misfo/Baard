(ns baard
  (:require [clojure.string :as s])
  (:use [clojure.core.match :only [match]]))

(defn pattern [req]
  (vec (cons
    (:request-method req)
    (for [seg (s/split (:uri req) #"/") :when (not (#{""} seg))]
      (java.net.URLDecoder/decode seg "UTF-8")))))

(defn delegate [handler & args]
  (fn [req]
    (apply handler req args)))

(defmacro app [& routes]
  `(fn [req#]
     ((match (pattern req#)
        ~@routes)
        req#)))

(comment
  (defn handler3 [req user] user)

  (def the-app
    (app
      [:get] handler1
      [_ "form"] handler2
      [:get "timeline" user] (delegate handler3 user)))

  (the-app {:request-method :get, :uri "/timeline/bob"})
  => "bob")
