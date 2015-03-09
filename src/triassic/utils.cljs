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
  (let [gl (get-context canvas)]
    (when-not gl
      (throw (js/Error. "Could not initialize WebGL")))
    gl))

(defn init-shaders [gl]
  (let [fragment-shader (get-shader gl "shader-fs")
        vertex-shader (get-shader gl "shader-vs")]
    (create-program gl fragment-shader vertex-shader)))

(defn animate [draw-fn]
  (letfn [(loop [frame]
            (fn []
              (.requestAnimFrame  js/window (loop (inc frame)))
              (draw-fn frame)))]
    ((loop 0))))

(defn checked? [element-id]
  (.-checked
   (.getElementById
    js/document
    element-id)))

(defn get-float [element-id]
  (js/parseFloat
   (.-value
    (.getElementById
     js/document
     element-id))))

(defn ambient-color []
  {:name "uAmbientColor"
   :type :vec3
   :values (ta/float32 [
                        (get-float "ambientR")
                        (get-float "ambientG")
                        (get-float "ambientB")])})

(defn directional-color []
  {:name "uDirectionalColor"
   :type :vec3
   :values (ta/float32 [
                        (get-float "directionalR")
                        (get-float "directionalG")
                        (get-float "directionalB")])})

(defn lighting-direction []
  (let [lighting-dir (ta/float32 [
                                  (get-float "lightDirectionX")
                                  (get-float "lightDirectionY")
                                  (get-float "lightDirectionZ")])
        adjusted-dir (vec3/create)]

    (vec3/normalize adjusted-dir lighting-dir)
    (vec3/scale adjusted-dir adjusted-dir -1.0)

    {:name "uLightingDirection"
     :type :vec3
     :values adjusted-dir}))

(defn blending [use-blending?]
  (when use-blending?
    [{:name "uAlpha"
      :type :float
      :values (ta/float32 [(get-float "alpha")])}]))

(defn lighting [use-lighting?]
  (cons
   {:name "uUseLighting" :type :int :values (ta/int32 [(if use-lighting? 1 0)])}
   (when use-lighting?
     [(ambient-color) (lighting-direction) (directional-color) ])))
