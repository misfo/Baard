(ns test.baard
  (:use baard
        clojure.test
        ring.mock.request))

(defn response
  [body]
  {:status 200, :headers {}, :body body})

(def hello (response "hello"))
(def not-found (assoc (response "not found") :status 404))

(deftest request-method-matching
  (let [f (app [_ "hi"] (constantly hello))]
    (is (= hello (f (request :get "/hi"))))
    (is (= hello (f (request :post "/hi")))))
  (let [f (app [:get "hi"] (constantly hello)
               :else (constantly not-found))]
    (is (= hello (f (request :get "/hi"))))
    (is (= not-found (f (request :post "/hi"))))))

(deftest uri-decoding
  (let [f (app [_ "sp ace" "sl/ash"] (constantly hello))]
    (is (= hello (f (request :get "/sp%20ace/sl%2Fash"))))))