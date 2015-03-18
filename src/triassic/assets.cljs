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

(defn fixup-keys [x]
  (prewalk
    (fn [x]
      (if (map? x)
        (zipmap (map keyword (keys x))
                (vals x))
        x))
    x))

(defn http-get [url]
  (let [c (chan 1)]
    (goog.net.XhrIo/send url (fn [e]
                               (->> e
                                    .-target
                                    .getResponse
                                    ;js->clj
                                    ;fixup-keys
                                    (put! c))))
    c))


(defn conj-in [map k v]
  (assoc map
    k
    (apply conj
           (k map)
           v)))

(defn- parse-vertices
  ([x y z] [(/ (js/parseFloat x) 25)
            (/ (js/parseFloat y) 25)
            (/ (js/parseFloat z) 25)])
  ([x y z w] [(js/parseFloat x)
              (js/parseFloat y)
              (js/parseFloat z)
              (js/parseFloat w)]))
(defn- parse-uv-coords
  ([u v] [(js/parseFloat u)
          (js/parseFloat v)])
  ([u v w] [(js/parseFloat u)
            (js/parseFloat v)
            (js/parseFloat w)]))
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
  (let [lines (string/split file-string #"\n")
        parsed-obj (reduce parse-line
                           {}
                           lines)
        vertices (:vertices parsed-obj)
        indices (:indices parsed-obj)]
    (-> parsed-obj
        (assoc :vertices (reverse vertices))
        (assoc :indices (reverse indices)))))

(defn lw-obj [URI]
  {:value URI
   :fn parse-obj})


(defn load-asset [[k v]]
  (let [process-function (:fn v)
        URI (:value v)]
    (go (assoc {}
          k
          (process-function
            (<! (go (<! (http-get URI)))))))))

(defn load-assets [gl asset-locations callback-fn]
  (go (callback-fn (<!
                     (async/map merge
                                (map load-asset asset-locations))))))

;(create-texture
;  gl
;  :image nil
;  :pixel-store-modes {webgl/unpack-flip-y-webgl true}
;  :parameters {texture-parameter-name/texture-mag-filter texture-filter/nearest
;               texture-parameter-name/texture-min-filter texture-filter/nearest})




(defn load-image
  ""
  [url callback-fn]
  (let [img (js/Image.)]
    (set! (.-onload img) (fn [] (callback-fn img)))
    (set! (.-crossOrigin img) "anonymous")
    (set! (.-src img) url)))

; TODO: probably want to parameterize some of the details here
; TODO: deprecate this method?
(defn load-texture
  "Loads the texture from the given URL. Note that the image is loaded in the background,
  and the returned texture will not immediately be fully initialized."
  [gl-context url callback-fn]
  (load-image url (fn [img] (callback-fn
                              ))))

