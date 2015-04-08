(ns triassic.utils
  (:require
   [vec3]
   [mat4]
   [cljs-webgl.buffers :refer [create-buffer clear-color-buffer draw!]]
   [cljs-webgl.context :refer [get-context]]
   [cljs-webgl.constants.parameter-name :as parameter-name]
   [cljs-webgl.shaders :refer [get-shader create-program get-attrib-location]]
   [cljs-webgl.typed-arrays :as ta]))

(enable-console-print!)

(defn mapply [f & args]
  "Applies the contents of a map to the given function, which should expect unbounded keyword arguments "
  (apply f (apply concat (butlast args) (last args))))

(defn get-viewport
  "Returns the current viewport for a given `gl-context` as a map with the form:

  {:x,
  :y,
  :width,
  :height}"
  [gl-context]
  (let [[x y w h] (.apply js/Array [] (.getParameter gl-context parameter-name/viewport))] ;; TODO: Is there any other way to access typed array values?
    {:x      x,
     :y      y,
     :width  w,
     :height h}))



(defn init-gl [canvas]
  "Returns the gl context of a given canvas element"
  (let [gl (get-context canvas)]
    (when-not gl
      (throw (js/Error. "Could not initialize WebGL")))
    gl))

(defn animate [draw-fn]
  "For each frame, exectures the given draw function"
  (letfn [(loop [frame]
            (fn []
              (.requestAnimFrame  js/window (loop (inc frame)))
              (draw-fn frame)))]
    ((loop 0))))