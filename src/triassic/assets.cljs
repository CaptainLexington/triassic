(ns triassic.assets
  (:require [cljs-webgl.texture :refer [create-texture]]
            [cljs-webgl.constants.texture-parameter-name :as texture-parameter-name]
            [cljs-webgl.constants.texture-filter :as texture-filter]
            [cljs-webgl.constants.webgl :as webgl]
            [cljs.core.async :refer [chan put! take! timeout] :as async]
            [clojure.walk :refer [prewalk]]
            [clojure.string :as string]
            [goog.net.XhrIo])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn http-get [url]
  "Returns a channel that will return the result of the ajax call to the given URL"
  (let [c (chan 1)]
    (goog.net.XhrIo/send url (fn [e]
                               (->> e
                                    .-target
                                    .getResponse
                                    ;js->clj
                                    ;fixup-keys
                                    (put! c))))
    c))

(defn load-image
  "Returns a channel that will return the a JS image object when it finishes loading the resource at the given URL"
  [url]
  (let [c (chan 1)
        img (js/Image.)]
    (set! (.-onload img) (fn [] (put! c img)))
    (set! (.-crossOrigin img) "anonymous")
    (set! (.-src img) url)
    c))


(defn- conj-in [map k v]
  (assoc map
    k
    (apply conj
           (k map)
           v)))

(defn- parse-vertices
  ([x y z] [(/ (js/parseFloat x) 1)
            (/ (js/parseFloat y) 1)
            (/ (js/parseFloat z) 1)])
  ([x y z w] [(js/parseFloat x)
              (js/parseFloat y)
              (js/parseFloat z)
              (js/parseFloat w)]))
(defn- parse-uv-coords
  ([u v] [(js/parseFloat u)
          (js/parseFloat v)])
  ([u v w] (if (zero? (js/parseFloat w))
             (parse-uv-coords u v)
             [(js/parseFloat u)
              (js/parseFloat v)
              (js/parseFloat w)])))
(defn- parse-normals [x y z]
  [(js/parseFloat x)
   (js/parseFloat y)
   (js/parseFloat z)])

(defn- els [seg index]
  (-> seg
      (nth index)
      js/parseFloat
      dec))

(defn- parse-faces
  ([map a b c]
   (let [fs-a (string/split a #"/")
         fs-b (string/split b #"/")
         fs-c (string/split c #"/")]
     (case (count fs-a)
       1 (conj-in map :indices [(els fs-a 0)
                                (els fs-b 0)
                                (els fs-c 0)])
       2 (-> map
             (conj-in :indices [(els fs-a 0)
                                (els fs-b 0)
                                (els fs-c 0)])
             (conj-in :uv-indices [(els fs-a 1)
                                   (els fs-b 1)
                                   (els fs-c 1)]))
       3 (-> map
             (conj-in :indices [(els fs-a 0)
                                (els fs-b 0)
                                (els fs-c 0)])
             (conj-in :uv-indices [(els fs-a 1)
                                   (els fs-b 1)
                                   (els fs-c 1)])

             (conj-in :normals [(els fs-a 2)
                                (els fs-b 2)
                                (els fs-c 2)])))))
  ([map a b c d]
    (-> map
        (parse-faces a b c)
        (parse-faces a c d))))

(defn- parse-line [map line-string]
  (let [sanitized-line (string/replace line-string #"\s+" " ")
        args (string/split sanitized-line #" ")
        type (first args)
        params (rest args)]
    (case type
      "v"  (conj-in map :vertices  (apply parse-vertices params))
      "vt" (conj-in map :uv-coords (apply parse-uv-coords params))
      "vn" (conj-in map :normals   (apply parse-normals params))
      "f"  (apply parse-faces map params)
      map)))

(defn- parse-obj [file-string]
  "Returns a ClojureScript map representing the vertices, vertex indices, uv coordinates, and uv indices of a 3D model given by the specified LightWave OBJ file"
  (let [lines (string/split file-string #"\n")
        parsed-obj (reduce parse-line
                           {}
                           lines)
        vertices (:vertices parsed-obj)
        indices (:indices parsed-obj)
        uv-indices (:uv-indices parsed-obj)
        uv-coords (:uv-coords parsed-obj)]
    (-> parsed-obj
        (assoc :vertices (reverse vertices))
        (assoc :indices (reverse indices))
        (assoc :uv-indices (reverse uv-indices))
        (assoc :uv-coords (reverse uv-coords)))))

(defn lw-obj [URI]
  "Returns a triassic-specific data-structure for loading LightWave OBJ files from the given remote URI"
  {:value URI
   :fn parse-obj
   :loader http-get})

(defn img [URI]
  "Returns a triassic-specific data-structure for loading image files from the given remote URI"
  {:value URI
   :fn identity
   :loader load-image})


(defn- load-asset [[k v]]
  (let [process-function (:fn v)
        URI (:value v)
        loader (:loader v)]
    (go (assoc {}
          k
          (process-function
            (<! (go (<! (loader URI)))))))))

(defn load-assets [gl asset-locations callback-fn]
  "Given a single-depth map of keys and URIs, returns a single-depth map of identical keys and loaded, processed assets retrieved from those URIs"
  (go (callback-fn (<!
                     (async/map merge
                                (map load-asset asset-locations))))))




; TODO: probably want to parameterize some of the details here
; TODO: deprecate this method?