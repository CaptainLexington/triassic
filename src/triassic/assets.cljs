(ns triassic.assets
  (:require [cljs-webgl.texture :refer [create-texture]]
            [cljs-webgl.constants.texture-parameter-name :as texture-parameter-name]
            [cljs-webgl.constants.texture-filter :as texture-filter]
            [cljs-webgl.constants.webgl :as webgl]
            [cljs.core.async :refer [chan put! take! timeout] :as async]
            [clojure.walk :refer [prewalk]]
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

(def a (go (println (<! (go (<! (http-get "/test.json")))))))

(def b (go (println (<! (go (<! (http-get "/test.json")))))))



(defn load-asset [k v process-function a]
  (go (swap! a
             assoc
             k
             (process-function
               (<! (go (<! (http-get v))))))))    


(defn load-assets [gl asset-locations callback-fn]
  (create-texture
    gl
    :image nil
    :pixel-store-modes {webgl/unpack-flip-y-webgl true}
    :parameters {texture-parameter-name/texture-mag-filter texture-filter/nearest
                 texture-parameter-name/texture-min-filter texture-filter/nearest}))




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

